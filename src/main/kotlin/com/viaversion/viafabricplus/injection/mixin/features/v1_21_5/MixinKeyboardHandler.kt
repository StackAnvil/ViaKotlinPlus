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
import com.viaversion.viafabricplus.features.v1_21_5.SignedCommands1_21_6

@Mixin(net.minecraft.client.KeyboardHandler::class) 
abstract class MixinKeyboardHandler {

    @org.spongepowered.asm.mixin.injection.Redirect(method = ["handleDebugKeys"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V")]) private fun wrapAsCommand(instance: net.minecraft.client.multiplayer.ClientPacketListener, packet: net.minecraft.network.protocol.Packet<net.minecraft.network.protocol.game.ServerGamePacketListener>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_5) && packet is net.minecraft.network.protocol.game.ServerboundChangeGameModePacket) {
            SignedCommands1_21_6.sendGameMode(packet.mode())
        } else {
            instance.send(packet)
        }
    }
    
}
