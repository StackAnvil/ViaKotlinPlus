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
package com.viaversion.viafabricplus.injection.mixin.features.v1_21_4.item

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import com.viaversion.viafabricplus.protocoltranslator.util.ItemUtil
import com.viaversion.viaversion.protocols.v1_21_4to1_21_5.Protocol1_21_4To1_21_5

@Mixin(net.minecraft.world.item.ItemStack::class) 
abstract class MixinItemStack {

    @com.llamalad7.mixinextras.injector.v2.WrapWithCondition(method = ["addDetailsToTooltip"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/Item\$TooltipContext;Lnet/minecraft/world/item/component/TooltipDisplay;Ljava/util/function/Consumer;Lnet/minecraft/world/item/TooltipFlag;)V")]) private fun hideAdditionalTooltip(instance: net.minecraft.world.item.Item, itemStack: net.minecraft.world.item.ItemStack, context: net.minecraft.world.item.Item.TooltipContext, display: net.minecraft.world.item.component.TooltipDisplay, builder: java.util.function.Consumer<net.minecraft.network.chat.Component>, tooltipFlag: net.minecraft.world.item.TooltipFlag): kotlin.Boolean {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_4)) {
            val tag: net.minecraft.nbt.CompoundTag = ItemUtil.getTagOrNull(this as kotlin.Any as net.minecraft.world.item.ItemStack)!!
            val backup: net.minecraft.nbt.CompoundTag? = if (tag == null) null else tag.getCompoundOrEmpty(ItemUtil.vvNbtName(Protocol1_21_4To1_21_5::class.java, "backup"))!!
            return backup == null || !backup.contains("hide_additional_tooltip")
        } else {
            return true
        }
    }
    
}
