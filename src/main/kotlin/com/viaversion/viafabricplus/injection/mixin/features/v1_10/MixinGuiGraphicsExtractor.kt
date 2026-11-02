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
package com.viaversion.viafabricplus.injection.mixin.features.v1_10

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At

@Mixin(net.minecraft.client.gui.GuiGraphicsExtractor::class) 
abstract class MixinGuiGraphicsExtractor {

    @org.spongepowered.asm.mixin.injection.Redirect(method = ["itemCount"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getCount()I")]) private fun handleNegativeItemCount(instance: net.minecraft.world.item.ItemStack): kotlin.Int {
        return com.viaversion.viafabricplus.ViaFabricPlus.api().limitations().getStackCount(instance)
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["itemCount"], at = [At(value = "INVOKE", target = "Ljava/lang/String;valueOf(I)Ljava/lang/String;", remap = false)]) private fun makeTextRed(i: kotlin.Int): kotlin.String {
        if (i <= 0) {
            return net.minecraft.ChatFormatting.RED.toString() + i
        } else {
            return i.toString()
        }
    }
    
}
