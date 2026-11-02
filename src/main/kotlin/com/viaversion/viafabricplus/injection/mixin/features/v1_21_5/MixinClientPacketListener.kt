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
package com.viaversion.viafabricplus.injection.mixin.features.v1_21_5

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At

@Mixin(net.minecraft.client.multiplayer.ClientPacketListener::class) 
abstract class MixinClientPacketListener {

    @org.spongepowered.asm.mixin.Shadow protected abstract fun verifyCommand(command: kotlin.String): net.minecraft.client.multiplayer.ClientPacketListener.CommandCheckResult
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["sendUnattendedCommand"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;verifyCommand(Ljava/lang/String;)Lnet/minecraft/client/multiplayer/ClientPacketListener\$CommandCheckResult;")]) private fun dontOpenConfirmationScreens(instance: net.minecraft.client.multiplayer.ClientPacketListener, command: kotlin.String): net.minecraft.client.multiplayer.ClientPacketListener.CommandCheckResult {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_5)) {
            val result: net.minecraft.client.multiplayer.ClientPacketListener.CommandCheckResult = verifyCommand(command)
            return if (result == net.minecraft.client.multiplayer.ClientPacketListener.CommandCheckResult.PARSE_ERRORS || result == net.minecraft.client.multiplayer.ClientPacketListener.CommandCheckResult.PERMISSIONS_REQUIRED) net.minecraft.client.multiplayer.ClientPacketListener.CommandCheckResult.NO_ISSUES else result
        } else {
            return verifyCommand(command)
        }
    }
    
}
