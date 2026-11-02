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
package com.viaversion.viafabricplus.injection.mixin.features.v1_8.item

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.viaversion.viafabricplus.injection.access.v1_20_5.IDisplayDefault

@Mixin(net.minecraft.world.item.ItemStack::class) 
abstract class MixinItemStack {

    @Inject(method = ["addAttributeTooltips"], at = [At("HEAD")]) private fun captureItemEnchantments(consumer: java.util.function.Consumer<net.minecraft.network.chat.Component>, display: net.minecraft.world.item.component.TooltipDisplay, player: net.minecraft.world.entity.player.Player, ci: CallbackInfo) {
        val itemStack: net.minecraft.world.item.ItemStack = this as kotlin.Any as net.minecraft.world.item.ItemStack
        val mixinDefault: IDisplayDefault = net.minecraft.world.item.component.ItemAttributeModifiers.Display.attributeModifiers() as IDisplayDefault
        mixinDefault.`viaFabricPlus$setItemEnchantments`(net.minecraft.world.item.enchantment.EnchantmentHelper.getEnchantmentsForCrafting(itemStack))
    }
    
}
