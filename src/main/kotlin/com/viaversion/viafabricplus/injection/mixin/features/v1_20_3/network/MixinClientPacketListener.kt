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
package com.viaversion.viafabricplus.injection.mixin.features.v1_20_3.network

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(net.minecraft.client.multiplayer.ClientPacketListener::class) 
abstract class MixinClientPacketListener protected constructor(client: net.minecraft.client.Minecraft, connection: net.minecraft.network.Connection, connectionState: net.minecraft.client.multiplayer.CommonListenerCookie) : net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl(client, connection, connectionState) {

    @org.spongepowered.asm.mixin.Shadow 
    @JvmField val lastSeenMessages: net.minecraft.network.chat.LastSeenMessagesTracker = uninitialized()
    @Inject(method = ["handleConfigurationStart"], at = [At("HEAD")]) private fun disableAutoRead(packet: net.minecraft.network.protocol.game.ClientboundStartConfigurationPacket, ci: CallbackInfo) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20_3)) {
            this.connection.channel.config().setAutoRead(false)
        }
    }
    
    @Inject(method = ["handleConfigurationStart"], at = [At("RETURN")]) private fun enableAutoRead(packet: net.minecraft.network.protocol.game.ClientboundStartConfigurationPacket, ci: CallbackInfo) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20_3)) {
            this.connection.channel.config().setAutoRead(true)
        }
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["sendCommand"], at = [At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z")]) private fun alwaysSignCommands(instance: kotlin.collections.MutableList<*>): kotlin.Boolean {
        return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20_3) && instance.isEmpty()
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["sendUnattendedCommand"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V")]) private fun alwaysSignCommands(instance: net.minecraft.client.multiplayer.ClientPacketListener, packet: net.minecraft.network.protocol.Packet<*>, @com.llamalad7.mixinextras.sugar.Local(argsOnly = true) command: kotlin.String) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20_3)) {
            this.send(net.minecraft.network.protocol.game.ServerboundChatCommandSignedPacket(command, java.time.Instant.now(), 0L, net.minecraft.commands.arguments.ArgumentSignatures.EMPTY, this.lastSeenMessages.generateAndApplyUpdate().update()))
        } else {
            instance.send(packet)
        }
    }
    
    @com.llamalad7.mixinextras.injector.v2.WrapWithCondition(method = ["handleRespawn"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;startWaitingForNewLevel(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/client/gui/screens/LevelLoadingScreen\$Reason;)V")]) private fun checkDimensionChange(instance: net.minecraft.client.multiplayer.ClientPacketListener, player: net.minecraft.client.player.LocalPlayer, level: net.minecraft.client.multiplayer.ClientLevel, reason: net.minecraft.client.gui.screens.LevelLoadingScreen.Reason, @com.llamalad7.mixinextras.sugar.Local(name = ["dimensionKey"]) dimensionKey: net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level>): kotlin.Boolean {
        return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20_3) || dimensionKey !== this.minecraft.player.level().dimension()
    }
    
}
