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
package com.viaversion.viafabricplus.injection.mixin.features.v1_2_1

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import net.raphimc.vialegacy.api.LegacyProtocolVersion

@Mixin(net.minecraft.world.inventory.AbstractFurnaceMenu::class) 
abstract class MixinAbstractFurnaceMenu {

    @org.spongepowered.asm.mixin.Shadow protected abstract fun canSmelt(itemStack: net.minecraft.world.item.ItemStack): kotlin.Boolean
    
    @org.spongepowered.asm.mixin.Shadow protected abstract fun isFuel(itemStack: net.minecraft.world.item.ItemStack): kotlin.Boolean
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["quickMoveStack"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractFurnaceMenu;canSmelt(Lnet/minecraft/world/item/ItemStack;)Z")]) private fun disableShiftClickSmeltingSlot(instance: net.minecraft.world.inventory.AbstractFurnaceMenu, itemStack: net.minecraft.world.item.ItemStack): kotlin.Boolean {
        return this.canSmelt(itemStack) && com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThan(LegacyProtocolVersion.r1_2_1tor1_2_3)
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["quickMoveStack"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractFurnaceMenu;isFuel(Lnet/minecraft/world/item/ItemStack;)Z")]) private fun disableShiftClickFuelSlot(instance: net.minecraft.world.inventory.AbstractFurnaceMenu, itemStack: net.minecraft.world.item.ItemStack): kotlin.Boolean {
        return this.isFuel(itemStack) && com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThan(LegacyProtocolVersion.r1_2_1tor1_2_3)
    }
    
}
