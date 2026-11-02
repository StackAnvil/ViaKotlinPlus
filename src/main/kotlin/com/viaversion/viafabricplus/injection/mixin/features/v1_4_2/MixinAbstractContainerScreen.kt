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
package com.viaversion.viafabricplus.injection.mixin.features.v1_4_2

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import net.raphimc.vialegacy.api.LegacyProtocolVersion

@Mixin(net.minecraft.client.gui.screens.inventory.AbstractContainerScreen::class) 
abstract class MixinAbstractContainerScreen {

    @com.llamalad7.mixinextras.injector.v2.WrapWithCondition(method = ["keyPressed"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;slotClicked(Lnet/minecraft/world/inventory/Slot;IILnet/minecraft/world/inventory/ContainerInput;)V", ordinal = 0)]) private fun disableItemCloning(instance: net.minecraft.client.gui.screens.inventory.AbstractContainerScreen<*>, slot: net.minecraft.world.inventory.Slot, slotId: kotlin.Int, buttonNum: kotlin.Int, containerInput: net.minecraft.world.inventory.ContainerInput): kotlin.Boolean {
        return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThanOrEqualTo(LegacyProtocolVersion.r1_4_2)
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["checkHotbarKeyPressed"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;matches(Lnet/minecraft/client/input/KeyEvent;)Z", ordinal = 1)]) private fun disableHotbarKeys(instance: net.minecraft.client.KeyMapping, event: net.minecraft.client.input.KeyEvent): kotlin.Boolean {
        return instance.matches(event) && com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThanOrEqualTo(LegacyProtocolVersion.r1_4_2)
    }
    
}
