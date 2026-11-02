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
package com.viaversion.viafabricplus.injection.mixin.features.v1_17_1

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import com.viaversion.viafabricplus.injection.access.v1_21_4.IAbstractContainerMenu
import org.spongepowered.asm.mixin.injection.At
import org.objectweb.asm.Opcodes

@Mixin(net.minecraft.world.inventory.AbstractContainerMenu::class) 
abstract class MixinAbstractContainerMenu : IAbstractContainerMenu {

    @field:org.spongepowered.asm.mixin.Shadow 
    @JvmField var carried: net.minecraft.world.item.ItemStack = uninitialized()
    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$actionId`: kotlin.Short = 0
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["initializeContents"], at = [At(value = "FIELD", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;carried:Lnet/minecraft/world/item/ItemStack;", opcode = Opcodes.PUTFIELD)]) private fun preventUpdate(instance: net.minecraft.world.inventory.AbstractContainerMenu, value: net.minecraft.world.item.ItemStack) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_17_1)) {
            this.carried = value
        }
    }
    
    override fun `viaFabricPlus$getActionId`(): kotlin.Short {
        return `viaFabricPlus$actionId`
    }
    
    override fun `viaFabricPlus$incrementAndGetActionId`(): kotlin.Short {
        return ++`viaFabricPlus$actionId`
    }
    
}
