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

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(net.minecraft.client.gui.screens.inventory.CommandBlockEditScreen::class) 
abstract class MixinCommandBlockEditScreen {

    @org.spongepowered.asm.mixin.Shadow 
    @JvmField val modeButton: net.minecraft.client.gui.components.CycleButton<net.minecraft.world.level.block.entity.CommandBlockEntity.Mode> = uninitialized()
    @org.spongepowered.asm.mixin.Shadow 
    @JvmField val conditionalButton: net.minecraft.client.gui.components.CycleButton<kotlin.Boolean> = uninitialized()
    @org.spongepowered.asm.mixin.Shadow 
    @JvmField val autoexecButton: net.minecraft.client.gui.components.CycleButton<kotlin.Boolean> = uninitialized()
    @org.spongepowered.asm.mixin.Shadow abstract fun updateGui()
    
    @Inject(method = ["init"], at = [At("TAIL")]) private fun removeWidgets(ci: CallbackInfo) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8)) {
            modeButton.visible = false
            conditionalButton.visible = false
            autoexecButton.visible = false
            
            updateGui()
        }
    }
    
}
