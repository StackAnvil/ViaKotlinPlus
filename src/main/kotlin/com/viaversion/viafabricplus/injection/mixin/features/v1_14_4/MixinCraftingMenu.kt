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
import org.spongepowered.asm.mixin.injection.At

@Mixin(net.minecraft.world.inventory.CraftingMenu::class) 
abstract class MixinCraftingMenu (type: net.minecraft.world.inventory.MenuType<*>, syncId: kotlin.Int, width: kotlin.Int, height: kotlin.Int) : net.minecraft.world.inventory.AbstractCraftingMenu(type, syncId, width, height) {

    @org.spongepowered.asm.mixin.injection.Redirect(method = ["quickMoveStack"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/CraftingMenu;moveItemStackTo(Lnet/minecraft/world/item/ItemStack;IIZ)Z", ordinal = 1)]) private fun noShiftClickMoveIntoCraftingTable(instance: net.minecraft.world.inventory.CraftingMenu, itemStack: net.minecraft.world.item.ItemStack, startIndex: kotlin.Int, endIndex: kotlin.Int, fromLast: kotlin.Boolean): kotlin.Boolean {
        return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_14_4) && this.moveItemStackTo(itemStack, startIndex, endIndex, fromLast)
    }
    
}
