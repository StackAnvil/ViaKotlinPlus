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
package com.viaversion.viafabricplus.injection.mixin.features.v1_21_2

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper
import com.viaversion.viaversion.protocols.v1_21_11to26_1.packet.ServerboundPackets26_1
import com.viaversion.viaversion.protocols.v26_2to26_3.Protocol26_2To26_3
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(net.minecraft.client.multiplayer.ClientPacketListener::class) 
abstract class MixinClientPacketListener {

    @org.spongepowered.asm.mixin.Shadow abstract fun getConnection(): net.minecraft.network.Connection
    
    @org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$teleportConfirmPacket`: net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket = uninitialized()
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["handleMoveVehicle"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;distanceTo(Lnet/minecraft/world/phys/Vec3;)D")]) private fun allowSmallValues(instance: net.minecraft.world.phys.Vec3, vec: net.minecraft.world.phys.Vec3): kotlin.Double {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_2)) {
            return kotlin.Int.Companion.MAX_VALUE.toDouble()
        } else {
            return instance.distanceTo(vec)
        }
    }
    
    @com.llamalad7.mixinextras.injector.v2.WrapWithCondition(method = ["handleMovePlayer"], at = [At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;send(Lnet/minecraft/network/protocol/Packet;)V", ordinal = 0)]) private fun changePacketOrder(instance: net.minecraft.network.Connection, packet: net.minecraft.network.protocol.Packet<*>): kotlin.Boolean {
        val cancel: kotlin.Boolean = com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().equalTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_2)
        if (cancel) {
            this.`viaFabricPlus$teleportConfirmPacket` = packet as net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket
        }
        return !cancel
    }
    
    @Inject(method = ["handleMovePlayer"], at = [At("RETURN")]) private fun changePacketOrder(packet: net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket, ci: CallbackInfo) {
        if (`viaFabricPlus$teleportConfirmPacket` != null) {
            val confirm: net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket = `viaFabricPlus$teleportConfirmPacket`
            `viaFabricPlus$teleportConfirmPacket` = uninitialized()
            this.getConnection().send(net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.PosRot(confirm.x(), confirm.y(), confirm.z(), confirm.yRot(), confirm.xRot(), false, false))
            
                        // The 26.3 packet also carries the position and would be split into confirm + move again, send the plain 26.2 confirm instead
            val acceptTeleportation: PacketWrapper = PacketWrapper.create(ServerboundPackets26_1.ACCEPT_TELEPORTATION, com.viaversion.viafabricplus.ViaFabricPlus.api().userConnection())
            acceptTeleportation.write<kotlin.Int>(com.viaversion.viaversion.api.type.Types.VAR_INT, confirm.id())
            acceptTeleportation.scheduleSendToServer(Protocol26_2To26_3::class.java)
        }
    }
    
    @com.llamalad7.mixinextras.injector.v2.WrapWithCondition(method = ["handleMoveEntity"], at = [At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/VecDeltaCodec;setBase(Lnet/minecraft/world/phys/Vec3;)V", ordinal = 0)]) private fun dontHandleEntityPositionChange(instance: net.minecraft.network.protocol.game.VecDeltaCodec, base: net.minecraft.world.phys.Vec3): kotlin.Boolean {
        return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_2)
    }
    
    @Inject(method = ["hasClientLoaded"], at = [At("HEAD")], cancellable = true) private fun alwaysLoadPlayer(cir: CallbackInfoReturnable<kotlin.Boolean>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_2)) {
            cir.setReturnValue(true)
        }
    }
    
}
