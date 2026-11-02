 /*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - Florian Reuth <git@florianreuth.de>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2026 ViaVersion and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.viaversion.viafabricplus.protocoltranslator

import com.viaversion.viaversion.api.protocol.version.ProtocolVersionRange
import com.viaversion.viafabricplus.protocoltranslator.impl.ViaFabricPlusMappingDataLoader
import net.raphimc.vialegacy.api.LegacyProtocolVersion
import net.raphimc.vialegacy.protocol.classic.c0_30cpetoc0_28_30.storage.ExtensionProtocolMetadataStorage
import net.raphimc.vialegacy.protocol.classic.c0_30cpetoc0_28_30.data.ClassicProtocolExtension
import com.viaversion.viafabricplus.features.c0_30cpe.CPEAdditions
import com.viaversion.viafabricplus.protocoltranslator.util.ItemUtil
import com.viaversion.viaversion.protocols.v1_10to1_11.Protocol1_10To1_11

class LimitationsImpl : com.viaversion.viafabricplus.api.protocoltranslator.Limitations {

    private val enchantmentDiff: it.unimi.dsi.fastutil.objects.Reference2ObjectMap<net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment>, ProtocolVersionRange> = it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap<net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment>, ProtocolVersionRange>()
    private val patternDiff: it.unimi.dsi.fastutil.objects.Reference2ObjectMap<net.minecraft.resources.ResourceKey<net.minecraft.world.level.block.entity.BannerPattern>, ProtocolVersionRange> = it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap<net.minecraft.resources.ResourceKey<net.minecraft.world.level.block.entity.BannerPattern>, ProtocolVersionRange>()
    private val effectDiff: it.unimi.dsi.fastutil.objects.Reference2ObjectMap<net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect>, ProtocolVersionRange> = it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap<net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect>, ProtocolVersionRange>()
    private val itemDiff: it.unimi.dsi.fastutil.objects.Reference2ObjectMap<net.minecraft.world.item.Item, ProtocolVersionRange> = it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap<net.minecraft.world.item.Item, ProtocolVersionRange>()
    
    fun init() {
        com.google.common.base.Preconditions.checkState(this.itemDiff.isEmpty(), "Limitations already initialized!")
        val data: com.viaversion.viaversion.libs.gson.JsonObject = ViaFabricPlusMappingDataLoader.INSTANCE.loadData("versioned-registries.json")!!
        fillKeys(data.getAsJsonObject("enchantments"), net.minecraft.core.registries.Registries.ENCHANTMENT, enchantmentDiff)
        fillKeys(data.getAsJsonObject("banner_patterns"), net.minecraft.core.registries.Registries.BANNER_PATTERN, patternDiff)
        fillEntries(data.getAsJsonObject("effects"), net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT, effectDiff)
        fillItems(data.getAsJsonObject("items"))
    }
    
    override fun maxChatLength(version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion): kotlin.Int {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(LegacyProtocolVersion.c0_28toc0_30)) {
            val storage: ExtensionProtocolMetadataStorage = com.viaversion.viafabricplus.ViaFabricPlus.api().userConnection()!!.get<ExtensionProtocolMetadataStorage>(ExtensionProtocolMetadataStorage::class.java)!!
            if (storage != null && storage.hasServerExtension(ClassicProtocolExtension.LONGER_MESSAGES)) {
                return kotlin.Short.Companion.MAX_VALUE * 2
            } else {
                return 64 - (net.minecraft.client.Minecraft.getInstance().getUser().getName().length + 2)
            }
        } else if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_9_3)) {
            return 100
        } else {
            return net.minecraft.SharedConstants.MAX_CHAT_LENGTH
        }
    }
    
    override fun itemExists(item: net.minecraft.world.item.Item, version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion): kotlin.Boolean {
        return !this.itemDiff.containsKey(item) || this.itemDiff.get(item)!!.contains(version)
    }
    
    override fun enchantmentExists(enchantment: net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment>, version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion): kotlin.Boolean {
        return !this.enchantmentDiff.containsKey(enchantment) || this.enchantmentDiff.get(enchantment)!!.contains(version)
    }
    
    override fun effectExists(effect: net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect>, version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion): kotlin.Boolean {
        return !this.effectDiff.containsKey(effect) || this.effectDiff.get(effect)!!.contains(version)
    }
    
    override fun bannerPatternExists(pattern: net.minecraft.resources.ResourceKey<net.minecraft.world.level.block.entity.BannerPattern>, version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion): kotlin.Boolean {
        return !this.patternDiff.containsKey(pattern) || this.patternDiff.get(pattern)!!.contains(version)
    }
    
    override fun itemExistsInConnection(item: net.minecraft.world.item.Item): kotlin.Boolean {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion() == LegacyProtocolVersion.c0_30cpe) {
            if (com.viaversion.viafabricplus.ViaFabricPlus.api().userConnection() != null) {
                val storage: ExtensionProtocolMetadataStorage = com.viaversion.viafabricplus.ViaFabricPlus.api().userConnection()!!.get<ExtensionProtocolMetadataStorage>(ExtensionProtocolMetadataStorage::class.java)!!
                if (storage.hasServerExtension(ClassicProtocolExtension.CUSTOM_BLOCKS, 1) && CPEAdditions.EXTENDED_CLASSIC_ITEMS.contains(item)) {
                    return true
                }
            }
        }
        
        return this.itemExists(item, com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion())
    }
    
    override fun itemExistsInConnection(stack: net.minecraft.world.item.ItemStack): kotlin.Boolean {
        if (!this.itemExistsInConnection(stack.getItem())) {
            return false
        }
        
        if (this.filterEnchantments(net.minecraft.core.component.DataComponents.ENCHANTMENTS, stack)) {
            return false
        }
        
        if (this.filterEnchantments(net.minecraft.core.component.DataComponents.STORED_ENCHANTMENTS, stack)) {
            return false
        }
        
        val bannerPatterns: net.minecraft.world.level.block.entity.BannerPatternLayers = stack.get<net.minecraft.world.level.block.entity.BannerPatternLayers>(net.minecraft.core.component.DataComponents.BANNER_PATTERNS)
        if (bannerPatterns != null) {
            for (layer in bannerPatterns.layers()) {
                if (!layer.pattern().unwrapKey().map<kotlin.Boolean>(java.util.function.Function {key: net.minecraft.resources.ResourceKey<net.minecraft.world.level.block.entity.BannerPattern> -> this.bannerPatternExists(key, com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion())}).orElse(true)) {
                    return false
                }
            }
        }
        
        val potionContents: net.minecraft.world.item.alchemy.PotionContents = stack.get<net.minecraft.world.item.alchemy.PotionContents>(net.minecraft.core.component.DataComponents.POTION_CONTENTS)
        if (potionContents != null) {
            for (effectInstance in java.util.Objects.requireNonNull<net.minecraft.world.item.alchemy.PotionContents>(potionContents).getAllEffects()) {
                if (!effectExists(effectInstance.getEffect(), com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion())) {
                    return false
                }
            }
        }
        return true
    }
    
    private fun filterEnchantments(componentType: net.minecraft.core.component.DataComponentType<net.minecraft.world.item.enchantment.ItemEnchantments>, stack: net.minecraft.world.item.ItemStack): kotlin.Boolean {
        val enchantments: net.minecraft.world.item.enchantment.ItemEnchantments = stack.get<net.minecraft.world.item.enchantment.ItemEnchantments>(componentType)
        if (enchantments != null) {
            for (enchantment in enchantments.keySet()) {
                if (!enchantment.unwrapKey().map<kotlin.Boolean>(java.util.function.Function {key: net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment> -> this.enchantmentExists(key, com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion())}).orElse(true)) {
                    return true
                }
            }
        }
        return false
    }
    
    override fun getStackCount(stack: net.minecraft.world.item.ItemStack): kotlin.Int {
        val tag: net.minecraft.nbt.CompoundTag = ItemUtil.getTagOrNull(stack)!!
        if (tag != null) {
            return tag.getIntOr(ItemUtil.vvNbtName(Protocol1_10To1_11::class.java), stack.getCount())
        } else {
            return stack.getCount()
        }
    }
    
    private fun <K> fillKeys(`object`: com.viaversion.viaversion.libs.gson.JsonObject, registryKey: net.minecraft.resources.ResourceKey<*>, map: it.unimi.dsi.fastutil.objects.Reference2ObjectMap<K, ProtocolVersionRange>) {
        for (element in `object`.keySet()) {
            val versions: ProtocolVersionRange = ProtocolVersionRange.fromString(`object`.get(element).getAsString())
            val key = net.minecraft.resources.ResourceKey.create<kotlin.Any>(registryKey as net.minecraft.resources.ResourceKey<net.minecraft.core.Registry<kotlin.Any>>, net.minecraft.resources.Identifier.parse(element))
            @Suppress("UNCHECKED_CAST")
            map.put(key as K, versions)
        }
    }
    
    private fun <K> fillEntries(`object`: com.viaversion.viaversion.libs.gson.JsonObject, registry: net.minecraft.core.Registry<*>, map: it.unimi.dsi.fastutil.objects.Reference2ObjectMap<K, ProtocolVersionRange>) {
        for (element in `object`.keySet()) {
            val versions: ProtocolVersionRange = ProtocolVersionRange.fromString(`object`.get(element).getAsString())
            val entry: net.minecraft.core.Holder<*> = registry.get(net.minecraft.resources.Identifier.parse(element)).orElseThrow()
            @Suppress("UNCHECKED_CAST")
            map.put(entry as K, versions)
        }
    }
    
    private fun fillItems(`object`: com.viaversion.viaversion.libs.gson.JsonObject) {
        for (element in `object`.keySet()) {
            val versions: ProtocolVersionRange = ProtocolVersionRange.fromString(`object`.get(element).getAsString())
            val item: net.minecraft.world.item.Item = net.minecraft.core.registries.BuiltInRegistries.ITEM.getOptional(net.minecraft.resources.Identifier.parse(element)).orElse(null)
            kotlin.checkNotNull(item) {"Unknown item: " + element} 
            
            itemDiff.put(item, versions)
        }
    }
    
    fun getItemDiff(): it.unimi.dsi.fastutil.objects.Reference2ObjectMap<net.minecraft.world.item.Item, ProtocolVersionRange> {
        return itemDiff
    }
    
    fun getEnchantmentDiff(): it.unimi.dsi.fastutil.objects.Reference2ObjectMap<net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment>, ProtocolVersionRange> {
        return enchantmentDiff
    }
    
    
    fun getEffectDiff(): it.unimi.dsi.fastutil.objects.Reference2ObjectMap<net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect>, ProtocolVersionRange> {
        return effectDiff
    }
    
    fun getPatternDiff(): it.unimi.dsi.fastutil.objects.Reference2ObjectMap<net.minecraft.resources.ResourceKey<net.minecraft.world.level.block.entity.BannerPattern>, ProtocolVersionRange> {
        return patternDiff
    }
    
}
