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
package com.viaversion.viafabricplus.injection.mixin.features.v1_5_2

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.injection.At
import net.raphimc.vialegacy.api.LegacyProtocolVersion
import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viafabricplus.injection.access.core.IConnection
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper
import net.raphimc.vialegacy.protocol.release.r1_5_2tor1_6_1.packet.ServerboundPackets1_5_2
import net.raphimc.vialegacy.protocol.release.r1_5_2tor1_6_1.Protocolr1_5_2Tor1_6_1
import com.viaversion.viaversion.protocols.v1_21to1_21_2.storage.ClientVehicleStorage
import com.viaversion.viaversion.protocols.v1_21to1_21_2.storage.ProtocolStorables1_21_2
import com.viaversion.viaversion.protocols.v1_21to1_21_2.Protocol1_21To1_21_2
import com.viaversion.viaversion.protocols.v1_20_3to1_20_5.packet.ServerboundPackets1_20_5

@Mixin(net.minecraft.client.player.LocalPlayer::class) 
abstract class MixinLocalPlayer (world: net.minecraft.client.multiplayer.ClientLevel, profile: com.mojang.authlib.GameProfile) : net.minecraft.client.player.AbstractClientPlayer(world, profile) {

    @org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField var connection: net.minecraft.client.multiplayer.ClientPacketListener = uninitialized()
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["sendChanges"], slice = [org.spongepowered.asm.mixin.injection.Slice(from = At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isPassenger()Z"))], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V", ordinal = 0)]) private fun modifyPositionPacket(instance: net.minecraft.client.multiplayer.ClientPacketListener, packet: net.minecraft.network.protocol.Packet<*>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThan(LegacyProtocolVersion.r1_5_2)) {
            instance.send(packet)
            return 
        }
        
        val connection: UserConnection = (this.connection.getConnection() as IConnection).`viaFabricPlus$getUserConnection`()
        connection.getChannel()!!.eventLoop().execute(java.lang.Runnable {
        val movePlayerPosRot: PacketWrapper = PacketWrapper.create(ServerboundPackets1_5_2.MOVE_PLAYER_POS_ROT, connection)
        movePlayerPosRot.write<kotlin.Double>(com.viaversion.viaversion.api.type.Types.DOUBLE, this.getDeltaMovement().x) // x
        movePlayerPosRot.write<kotlin.Double>(com.viaversion.viaversion.api.type.Types.DOUBLE, -999.0) // y
        movePlayerPosRot.write<kotlin.Double>(com.viaversion.viaversion.api.type.Types.DOUBLE, -999.0) // stance
        movePlayerPosRot.write<kotlin.Double>(com.viaversion.viaversion.api.type.Types.DOUBLE, this.getDeltaMovement().z) // z
        movePlayerPosRot.write<kotlin.Float>(com.viaversion.viaversion.api.type.Types.FLOAT, this.getYRot()) // yaw
        movePlayerPosRot.write<kotlin.Float>(com.viaversion.viaversion.api.type.Types.FLOAT, this.getXRot()) // pitch
        movePlayerPosRot.write<kotlin.Boolean>(com.viaversion.viaversion.api.type.Types.BOOLEAN, this.onGround()) // onGround
        movePlayerPosRot.sendToServer(Protocolr1_5_2Tor1_6_1::class.java)
        
                    // Copied from the 1.21->1.21.2 protocol since it's changing the packet order, and we manually send the movement packet here
        val storage: ClientVehicleStorage = connection.storables<ProtocolStorables1_21_2>(Protocol1_21To1_21_2::class.java).clientVehicleStorage()!!
        if (storage != null) {
            val playerInput: PacketWrapper = PacketWrapper.create(ServerboundPackets1_20_5.PLAYER_INPUT, connection)
            playerInput.write<kotlin.Float>(com.viaversion.viaversion.api.type.Types.FLOAT, storage.sidewaysMovement())
            playerInput.write<kotlin.Float>(com.viaversion.viaversion.api.type.Types.FLOAT, storage.forwardMovement())
            playerInput.write<kotlin.Byte>(com.viaversion.viaversion.api.type.Types.BYTE, storage.flags())
            playerInput.sendToServer(Protocol1_21To1_21_2::class.java)
        }
        
        })
    }
    
}
