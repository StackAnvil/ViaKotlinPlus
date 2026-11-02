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
package com.viaversion.viafabricplus.updater

import org.junit.jupiter.api.Test
import com.viaversion.viaversion.api.protocol.version.ProtocolVersionRange
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslationImpl
import com.viaversion.viafabricplus.ViaFabricPlusImpl
import com.viaversion.viafabricplus.protocoltranslator.impl.ViaFabricPlusMappingDataLoader
import net.lenni0451.reflect.stream.RStream
import java.io.FileWriter
import java.io.IOException

class UpdateTaskTest {

    private val GSON: com.viaversion.viaversion.libs.gson.Gson = com.viaversion.viaversion.libs.gson.GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()
    private val CURRENT_VERSION_RANGE: kotlin.String = ProtocolVersionRange.andNewer(ProtocolTranslationImpl.NATIVE_VERSION).toString()
    
    @Test fun update() {
        this.bootstrapClient()
        if (net.minecraft.SharedConstants.getProtocolVersion() != ProtocolTranslationImpl.NATIVE_VERSION.getOriginalVersion()) {
            throw java.lang.UnsupportedOperationException("Please update ProtocolTranslator.NATIVE_VERSION to the current protocol version.")
        }
        
        updateVersionedRegistries()
        updateResourcePacks()
    }
    
    private fun bootstrapClient() {
        net.fabricmc.loader.api.FabricLoader.getInstance().invokeEntrypoints<com.viaversion.viafabricplus.api.entrypoint.ViaFabricPlusEntrypoint>("viafabricplus", com.viaversion.viafabricplus.api.entrypoint.ViaFabricPlusEntrypoint::class.java, java.util.function.Consumer {obj: com.viaversion.viafabricplus.api.entrypoint.ViaFabricPlusEntrypoint -> obj.onPreLoading()})
        net.minecraft.SharedConstants.tryDetectVersion()
        net.minecraft.server.Bootstrap.bootStrap()
        ViaFabricPlusImpl.impl().limitations().init()
    }
    
    private fun updateVersionedRegistries() {
        val data: com.viaversion.viaversion.libs.gson.JsonObject = ViaFabricPlusMappingDataLoader.INSTANCE.loadData("versioned-registries.json")!!
        addMissingItems(data.getAsJsonObject("items"))
        addMissingEnchantments(data.getAsJsonObject("enchantments"))
        addMissingPatterns(data.getAsJsonObject("banner_patterns"))
        addMissingEffects(data.getAsJsonObject("effects"))
        
        write("versioned-registries.json", data)
    }
    
    private fun addMissingItems(items: com.viaversion.viaversion.libs.gson.JsonObject) {
        for (item in net.minecraft.core.registries.BuiltInRegistries.ITEM) {
            if (ViaFabricPlusImpl.impl().limitations().getItemDiff().containsKey(item) || item === net.minecraft.world.item.Items.AIR) {
                continue 
            }
            
            items.addProperty(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item).toString(), CURRENT_VERSION_RANGE)
        }
    }
    
    private fun addMissingEnchantments(enchantments: com.viaversion.viaversion.libs.gson.JsonObject) {
        RStream.of(net.minecraft.world.item.enchantment.Enchantments::class.java).fields().forEach(java.util.function.Consumer {
        fieldWrapper: net.lenni0451.reflect.stream.field.FieldWrapper -> val registryKey: net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment> = fieldWrapper.get<net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment>>()
        if (ViaFabricPlusImpl.impl().limitations().getEnchantmentDiff().containsKey(registryKey)) {
            return@Consumer 
        }
        
        enchantments.addProperty(registryKey.identifier().toString(), CURRENT_VERSION_RANGE)
        
        })
    }
    
    private fun addMissingPatterns(patterns: com.viaversion.viaversion.libs.gson.JsonObject) {
        RStream.of(net.minecraft.world.level.block.entity.BannerPatterns::class.java).fields().forEach(java.util.function.Consumer {
        fieldWrapper: net.lenni0451.reflect.stream.field.FieldWrapper -> val registryKey: net.minecraft.resources.ResourceKey<net.minecraft.world.level.block.entity.BannerPattern> = fieldWrapper.get<net.minecraft.resources.ResourceKey<net.minecraft.world.level.block.entity.BannerPattern>>()
        if (ViaFabricPlusImpl.impl().limitations().getPatternDiff().containsKey(registryKey)) {
            return@Consumer 
        }
        
        patterns.addProperty(registryKey.identifier().toString(), CURRENT_VERSION_RANGE)
        
        })
    }
    
    private fun addMissingEffects(effects: com.viaversion.viaversion.libs.gson.JsonObject) {
        for (effect in net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT) {
            if (ViaFabricPlusImpl.impl().limitations().getEffectDiff().containsKey(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect))) {
                continue 
            }
            
            effects.addProperty(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.getKey(effect).toString(), CURRENT_VERSION_RANGE)
        }
    }
    
    private fun updateResourcePacks() {
        val data: com.viaversion.viaversion.libs.gson.JsonObject = ViaFabricPlusMappingDataLoader.INSTANCE.loadData("resource-pack-headers.json")!!
        
        val version: net.minecraft.WorldVersion = net.minecraft.SharedConstants.getCurrentVersion()
        if (data.has(version.name())) {
            return 
        }
        
        val packVersion: net.minecraft.server.packs.metadata.pack.PackFormat = version.packVersion(net.minecraft.server.packs.PackType.CLIENT_RESOURCES)
        val packFormat: com.viaversion.viaversion.libs.gson.JsonObject = com.viaversion.viaversion.libs.gson.JsonObject()
        packFormat.addProperty("major", packVersion.major())
        packFormat.addProperty("minor", packVersion.minor())
        
        val header: com.viaversion.viaversion.libs.gson.JsonObject = com.viaversion.viaversion.libs.gson.JsonObject()
        header.addProperty("version", version.protocolVersion())
        header.add("pack_format", packFormat)
        data.add(version.name(), header)
        
        write("resource-pack-headers.json", data)
    }
    
    private fun write(name: kotlin.String, data: com.viaversion.viaversion.libs.gson.JsonObject) {
        try {
            FileWriter("../src/main/resources/assets/viafabricplus/data/" + name).use {writer -> GSON.toJson(data, writer)
            } }catch (e: IOException) {
            throw java.lang.RuntimeException(e)
        }
    }
    
}
