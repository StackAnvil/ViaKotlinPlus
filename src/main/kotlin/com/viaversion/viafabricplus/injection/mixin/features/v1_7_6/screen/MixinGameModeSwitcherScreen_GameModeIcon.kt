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
package com.viaversion.viafabricplus.injection.mixin.features.v1_7_6.screen

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import com.viaversion.viafabricplus.injection.mixin.features.v1_7_6.screen.MixinGameModeSwitcherScreen_GameModeIcon
import net.raphimc.vialegacy.api.LegacyProtocolVersion
import org.spongepowered.asm.mixin.Final

@Mixin(net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen.GameModeIcon::class) 
abstract class MixinGameModeSwitcherScreen_GameModeIcon {

    @Inject(method = ["getNext"], at = [At("HEAD")], cancellable = true) private fun unwrapGameModes(cir: CallbackInfoReturnable<net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen.GameModeIcon>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_7_6)) {
            when (this as kotlin.Any as net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen.GameModeIcon){net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen.GameModeIcon.CREATIVE -> cir.setReturnValue(MixinGameModeSwitcherScreen_GameModeIcon.Companion.SURVIVAL)
                net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen.GameModeIcon.SURVIVAL -> {
                    if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_2_4tor1_2_5)) {
                        cir.setReturnValue(MixinGameModeSwitcherScreen_GameModeIcon.Companion.CREATIVE)
                    } else {
                        cir.setReturnValue(net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen.GameModeIcon.ADVENTURE)
                    }
                }
                net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen.GameModeIcon.ADVENTURE -> cir.setReturnValue(MixinGameModeSwitcherScreen_GameModeIcon.Companion.CREATIVE)
                else -> {}}
        }
    }
    
    private companion object {
        @field:org.spongepowered.asm.mixin.Shadow @Final 
        @JvmField var SURVIVAL: net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen.GameModeIcon = uninitialized()
        @field:org.spongepowered.asm.mixin.Shadow @Final 
        @JvmField var CREATIVE: net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen.GameModeIcon = uninitialized()
    }}
