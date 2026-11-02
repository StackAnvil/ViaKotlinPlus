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
package com.viaversion.viafabricplus.injection.mixin.features.v1_8.screen

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At

@Mixin(net.minecraft.world.inventory.InventoryMenu::class) 
abstract class MixinInventoryMenu (type: net.minecraft.world.inventory.MenuType<*>, syncId: kotlin.Int, width: kotlin.Int, height: kotlin.Int) : net.minecraft.world.inventory.AbstractCraftingMenu(type, syncId, width, height) {

    @org.spongepowered.asm.mixin.injection.Redirect(method = ["<init>"], slice = [org.spongepowered.asm.mixin.injection.Slice(from = At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/InventoryMenu$1;<init>(Lnet/minecraft/world/inventory/InventoryMenu;Lnet/minecraft/world/Container;IIILnet/minecraft/world/entity/player/Player;)V"))], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/InventoryMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;", ordinal = 0)]) private fun removeOffhandSlot(screenHandler: net.minecraft.world.inventory.InventoryMenu, slot: net.minecraft.world.inventory.Slot): net.minecraft.world.inventory.Slot? {
        return if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8)) null else addSlot(slot)
    }
    
}
