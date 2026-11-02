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
package com.viaversion.viafabricplus.injection.mixin.features.v1_20_5

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.objectweb.asm.Opcodes
import com.viaversion.viafabricplus.ViaFabricPlusImpl

@Mixin(net.minecraft.client.gui.screens.LevelLoadingScreen::class) 
abstract class MixinLevelLoadingScreen {

    @org.spongepowered.asm.mixin.Shadow 
    @JvmField val reason: net.minecraft.client.gui.screens.LevelLoadingScreen.Reason = uninitialized()
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["extractBackground"], at = [At(value = "FIELD", target = "Lnet/minecraft/client/gui/screens/LevelLoadingScreen;reason:Lnet/minecraft/client/gui/screens/LevelLoadingScreen\$Reason;", opcode = Opcodes.GETFIELD)]) private fun hideDownloadTerrainScreenTransitionEffects(levelLoadingScreen: net.minecraft.client.gui.screens.LevelLoadingScreen): net.minecraft.client.gui.screens.LevelLoadingScreen.Reason {
        if (ViaFabricPlusImpl.impl().visuals().hideDownloadTerrainScreenTransitionEffects().isActive()) {
            return net.minecraft.client.gui.screens.LevelLoadingScreen.Reason.OTHER
        } else {
            return this.reason
        }
    }
    
}
