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
package com.viaversion.viafabricplus.injection.mixin.features.v26_2.network

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(net.minecraft.client.multiplayer.ClientPacketListener::class) 
abstract class MixinClientPacketListener protected constructor(minecraft: net.minecraft.client.Minecraft, connection: net.minecraft.network.Connection, cookie: net.minecraft.client.multiplayer.CommonListenerCookie) : net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl(minecraft, connection, cookie) {

    @Inject(method = ["handleTeleportEntity"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;setValuesFromPositionPacket(Lnet/minecraft/world/entity/PositionMoveRotation;Ljava/util/Set;Lnet/minecraft/world/entity/Entity;Z)Z", shift = At.Shift.AFTER, ordinal = 0)]) private fun confirmRemovedVehicleTeleport(packet: net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket, ci: CallbackInfo) {
        // 26.2 and older clients reported their new position after a teleport meant for their removed vehicle
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v26_2)) {
            val player: net.minecraft.client.player.LocalPlayer = this.minecraft.player
            this.send(net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.PosRot(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), false, false))
        }
    }
    
    @com.llamalad7.mixinextras.injector.v2.WrapWithCondition(method = ["handleMovePlayer"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;stopDestroyBlock()V")]) private fun dontStopDestroyingOnTeleport(instance: net.minecraft.client.multiplayer.MultiPlayerGameMode): kotlin.Boolean {
        return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v26_3)
    }
    
}
