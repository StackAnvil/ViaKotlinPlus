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
package com.viaversion.viafabricplus.injection.mixin.features.v1_14_4

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.viaversion.viafabricplus.protocoltranslator.util.ItemUtil
import com.viaversion.viafabricplus.features.v1_14_4.Enchantments1_14_4

@Mixin(net.minecraft.world.item.ItemStack::class) 
abstract class MixinItemStack {

    @Inject(method = ["addToTooltip(Lnet/minecraft/core/component/DataComponentType;Lnet/minecraft/world/item/component/TooltipProvider\$Getter;Lnet/minecraft/world/item/Item\$TooltipContext;Lnet/minecraft/world/item/component/TooltipDisplay;Ljava/util/function/Consumer;Lnet/minecraft/world/item/TooltipFlag;)V"], at = [At("HEAD")], cancellable = true) private fun <T>replaceEnchantmentTooltip(type: net.minecraft.core.component.DataComponentType<T>, tooltipGetter: net.minecraft.world.item.component.TooltipProvider.Getter<T>, context: net.minecraft.world.item.Item.TooltipContext, display: net.minecraft.world.item.component.TooltipDisplay, consumer: java.util.function.Consumer<net.minecraft.network.chat.Component>, flag: net.minecraft.world.item.TooltipFlag, ci: CallbackInfo) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_14_4)) {
            return 
        }
        
        val tag: net.minecraft.nbt.CompoundTag = ItemUtil.getTagOrNull(this as kotlin.Any as net.minecraft.world.item.ItemStack)!!
        if (tag == null) {
            return 
        }
        if (type === net.minecraft.core.component.DataComponents.ENCHANTMENTS) {
            this.`viaFabricPlus$appendEnchantments1_14_4`("Enchantments", tag, context, consumer)
            ci.cancel()
        } else if (type === net.minecraft.core.component.DataComponents.STORED_ENCHANTMENTS) {
            this.`viaFabricPlus$appendEnchantments1_14_4`("StoredEnchantments", tag, context, consumer)
            ci.cancel()
        }
    }
    
    @org.spongepowered.asm.mixin.Unique private fun `viaFabricPlus$appendEnchantments1_14_4`(name: kotlin.String, nbt: net.minecraft.nbt.CompoundTag, context: net.minecraft.world.item.Item.TooltipContext, tooltip: java.util.function.Consumer<net.minecraft.network.chat.Component>) {
        val registryLookup: net.minecraft.core.HolderLookup.Provider = context.registries()
        val enchantments: net.minecraft.nbt.ListTag = nbt.getList(name).orElse(null)
        if (enchantments == null) {
            return 
        }
        
        for (element in enchantments) {
            val enchantment: net.minecraft.nbt.CompoundTag = element as net.minecraft.nbt.CompoundTag
            
            val id: kotlin.String = enchantment.getStringOr("id", "")
            val value: java.util.Optional<net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment>> = Enchantments1_14_4.getOrEmpty(id)
            value.ifPresent(java.util.function.Consumer {
            e: net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment> -> val lvl: kotlin.Int = enchantment.getIntOr("lvl", 0)
            if (registryLookup != null) {
                val v: java.util.Optional<net.minecraft.core.Holder.Reference<net.minecraft.world.item.enchantment.Enchantment>> = registryLookup.lookupOrThrow<net.minecraft.world.item.enchantment.Enchantment>(net.minecraft.core.registries.Registries.ENCHANTMENT).get(e)
                v.ifPresent(java.util.function.Consumer {enchantmentReference: net.minecraft.core.Holder.Reference<net.minecraft.world.item.enchantment.Enchantment> -> tooltip.accept(net.minecraft.world.item.enchantment.Enchantment.getFullname(enchantmentReference, net.minecraft.util.Mth.clamp(lvl, kotlin.Short.Companion.MIN_VALUE.toInt(), kotlin.Short.Companion.MAX_VALUE.toInt())))})
            }
            
            })
        }
    }
    
}
