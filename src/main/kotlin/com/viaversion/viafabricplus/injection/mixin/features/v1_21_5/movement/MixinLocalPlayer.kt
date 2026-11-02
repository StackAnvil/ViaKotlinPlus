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
package com.viaversion.viafabricplus.injection.mixin.features.v1_21_5.movement

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper
import com.viaversion.viaversion.protocols.v1_21_4to1_21_5.packet.ServerboundPackets1_21_5
import com.viaversion.viaversion.protocols.v1_21_5to1_21_6.Protocol1_21_5To1_21_6

@Mixin(net.minecraft.client.player.LocalPlayer::class) 
abstract class MixinLocalPlayer (level: net.minecraft.client.multiplayer.ClientLevel, gameProfile: com.mojang.authlib.GameProfile) : net.minecraft.client.player.AbstractClientPlayer(level, gameProfile) {

    @org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField var minecraft: net.minecraft.client.Minecraft = uninitialized()
    @org.spongepowered.asm.mixin.Shadow abstract override fun isShiftKeyDown(): kotlin.Boolean
    
    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$lastSneaking`: kotlin.Boolean = false
    
    @Inject(method = ["<init>"], at = [At("RETURN")]) private fun initLastSneaking(minecraft: net.minecraft.client.Minecraft, level: net.minecraft.client.multiplayer.ClientLevel, connection: net.minecraft.client.multiplayer.ClientPacketListener, stats: net.minecraft.stats.StatsCounter, recipeBook: net.minecraft.client.ClientRecipeBook, lastSentInput: net.minecraft.world.entity.player.Input, wasSprinting: kotlin.Boolean, chatAbilities: net.minecraft.client.multiplayer.chat.ChatAbilities, itemActivation: net.minecraft.client.player.ItemActivation, ci: CallbackInfo) {
        `viaFabricPlus$lastSneaking` = lastSentInput.shift()
    }
    
    @Inject(method = ["tick"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;tick()V", shift = At.Shift.AFTER)]) private fun sendSneakingPacket(ci: CallbackInfo) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().betweenInclusive(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_2, com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_5)) {
            this.`viaFabricPlus$sendSneakingPacket`()
        }
    }
    
    @Inject(method = ["sendPosition"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;sendIsSprintingIfNeeded()V", shift = At.Shift.AFTER)]) private fun sendSneakingAfterSprinting(ci: CallbackInfo) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21)) {
            this.`viaFabricPlus$sendSneakingPacket`()
        }
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["sendChanges"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V", ordinal = 0)]) private fun skipVVProtocol(instance: net.minecraft.client.multiplayer.ClientPacketListener, packet: net.minecraft.network.protocol.Packet<*>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_5) && packet is net.minecraft.network.protocol.game.ServerboundPlayerInputPacket) {
            // Directly send the player input packet to bypass the code in the 1.21.5->1.21.6 protocol.
            // This allows mods to directly send raw packets which will then be remapped by VV instead of us.
            this.`viaFabricPlus$sendInputPacket`(packet.input())
        } else {
            instance.send(packet)
        }
    }
    
    @org.spongepowered.asm.mixin.Unique private fun `viaFabricPlus$sendInputPacket`(playerInput: net.minecraft.world.entity.player.Input) {
        var flags: kotlin.Byte = 0
        flags = (flags.toInt() or (if (playerInput.forward()) 0x1 else 0)).toByte()
        flags = (flags.toInt() or (if (playerInput.backward()) 0x2 else 0)).toByte()
        flags = (flags.toInt() or (if (playerInput.left()) 0x4 else 0)).toByte()
        flags = (flags.toInt() or (if (playerInput.right()) 0x8 else 0)).toByte()
        flags = (flags.toInt() or (if (playerInput.jump()) 0x10 else 0)).toByte()
        flags = (flags.toInt() or (if (playerInput.shift()) 0x20 else 0)).toByte()
        flags = (flags.toInt() or (if (playerInput.sprint()) 0x40 else 0)).toByte()
        
        val inputPacket: PacketWrapper = PacketWrapper.create(ServerboundPackets1_21_5.PLAYER_INPUT, com.viaversion.viafabricplus.ViaFabricPlus.api().userConnection())
        inputPacket.write<kotlin.Byte>(com.viaversion.viaversion.api.type.Types.BYTE, flags)
        inputPacket.scheduleSendToServer(Protocol1_21_5To1_21_6::class.java)
    }
    
    @org.spongepowered.asm.mixin.Unique private fun `viaFabricPlus$sendSneakingPacket`() {
        val sneaking: kotlin.Boolean = this.isShiftKeyDown()
        if (sneaking == this.`viaFabricPlus$lastSneaking`) {
            return 
        }
        
        val sneakingPacket: PacketWrapper = PacketWrapper.create(ServerboundPackets1_21_5.PLAYER_COMMAND, com.viaversion.viafabricplus.ViaFabricPlus.api().userConnection())
        sneakingPacket.write<kotlin.Int>(com.viaversion.viaversion.api.type.Types.VAR_INT, getId())
        sneakingPacket.write<kotlin.Int>(com.viaversion.viaversion.api.type.Types.VAR_INT, if (sneaking) 0 else 1)
        sneakingPacket.write<kotlin.Int>(com.viaversion.viaversion.api.type.Types.VAR_INT, 0) // No data
        sneakingPacket.scheduleSendToServer(Protocol1_21_5To1_21_6::class.java)
        this.`viaFabricPlus$lastSneaking` = sneaking
    }
    
}
