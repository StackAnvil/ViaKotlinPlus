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

import com.viaversion.viafabricplus.ViaFabricPlus
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen
import net.raphimc.vialegacy.api.LegacyProtocolVersion
import org.objectweb.asm.Opcodes
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Mutable
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.Unique
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.Redirect
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(GameModeSwitcherScreen::class)
abstract class MixinGameModeSwitcherScreen {

    @JvmField
    @Unique
    var unwrappedGameModes: Array<GameModeSwitcherScreen.GameModeIcon>? = null

    @Inject(method = ["<init>"], at = [At("RETURN")])
    private fun fixUIWidth(ci: CallbackInfo) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_7_6)) {
            val selections = GameModeSwitcherScreen.GameModeIcon.values().toMutableList()
            selections.remove(GameModeSwitcherScreen.GameModeIcon.SPECTATOR)
            if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_2_4tor1_2_5)) {
                selections.remove(GameModeSwitcherScreen.GameModeIcon.ADVENTURE)
            }
            val unwrapped = selections.toTypedArray()
            unwrappedGameModes = unwrapped
            ALL_SLOTS_WIDTH = unwrapped.size * SLOT_AREA_PADDED - SLOT_PADDING
        }
    }

    @Redirect(
        method = ["init"],
        at = [At(
            value = "FIELD",
            target = "Lnet/minecraft/client/gui/screens/debug/GameModeSwitcherScreen\$GameModeIcon;VALUES:[Lnet/minecraft/client/gui/screens/debug/GameModeSwitcherScreen\$GameModeIcon;",
            opcode = Opcodes.GETSTATIC
        )]
    )
    private fun removeNewerGameModes(): Array<GameModeSwitcherScreen.GameModeIcon> {
        return if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_7_6)) {
            unwrappedGameModes!!
        } else {
            GameModeSwitcherScreen.GameModeIcon.values()
        }
    }

    private companion object {
        @JvmField
        @Mutable
        @Shadow
        @Final
        var ALL_SLOTS_WIDTH: Int = 0

        @JvmField
        @Shadow
        @Final
        var SLOT_AREA_PADDED: Int = 0

        @JvmField
        @Shadow
        @Final
        var SLOT_PADDING: Int = 0
    }
}
