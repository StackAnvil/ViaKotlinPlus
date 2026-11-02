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
package com.viaversion.viafabricplus.injection.mixin.features.v1_18_2

import org.spongepowered.asm.mixin.Mixin
import com.viaversion.viaversion.protocols.v1_18_2to1_19.rewriter.WorldPacketRewriter1_19
import org.spongepowered.asm.mixin.injection.At
import com.viaversion.viaversion.protocols.v1_18_2to1_19.Protocol1_18_2To1_19
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType
import com.viaversion.viaversion.protocols.v1_17_1to1_18.packet.ClientboundPackets1_18
import com.viaversion.viaversion.protocols.v1_18_2to1_19.packet.ClientboundPackets1_19
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper
import com.viaversion.viafabricplus.util.network.SyncTasks
import com.viaversion.viafabricplus.features.v1_18_2.BlockStateTranslator
import com.viaversion.viafabricplus.injection.access.v1_18_2.IMultiPlayerGameMode

@Mixin(value = [WorldPacketRewriter1_19::class], remap = false) 
class MixinWorldPacketRewriter1_19 {
private companion object {
        @JvmStatic @org.spongepowered.asm.mixin.injection.Redirect(method = ["register"], at = [At(value = "INVOKE", target = "Lcom/viaversion/viaversion/protocols/v1_18_2to1_19/Protocol1_18_2To1_19;cancelClientbound(Lcom/viaversion/viaversion/api/protocol/packet/ClientboundPacketType;)V")]) fun handleLegacyAcknowledgePlayerDigging(instance: Protocol1_18_2To1_19, clientboundPacketType: ClientboundPacketType) {
            instance.registerClientbound(ClientboundPackets1_18.BLOCK_BREAK_ACK, ClientboundPackets1_19.CUSTOM_PAYLOAD, PacketHandler {
            wrapper: PacketWrapper -> wrapper.resetReader()
        
            val uuid: kotlin.String = SyncTasks.executeSyncTask(java.util.function.Consumer {data: net.minecraft.network.RegistryFriendlyByteBuf -> try {
                val pos: net.minecraft.core.BlockPos = data.readBlockPos()
                val blockState: net.minecraft.world.level.block.state.BlockState = BlockStateTranslator.via1_18_2toMc(data.readVarInt())
                val action: net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action = data.readEnum<net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action>(net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action::class.java)
                val allGood: kotlin.Boolean = data.readBoolean()
            
                val mixinInteractionManager: IMultiPlayerGameMode = net.minecraft.client.Minecraft.getInstance().gameMode as IMultiPlayerGameMode
                mixinInteractionManager.`viaFabricPlus$get1_18_2InteractionManager`().handleBlockBreakAck(pos, blockState, action, allGood)
            }catch (t: Throwable) {
                throw java.lang.RuntimeException("Failed to handle BlockBreakAck packet data", t)
            }
            })
            wrapper.write<kotlin.String>(com.viaversion.viaversion.api.type.Types.STRING, SyncTasks.PACKET_SYNC_IDENTIFIER)
            wrapper.write<kotlin.String>(com.viaversion.viaversion.api.type.Types.STRING, uuid)
        
            })
        }
    
    }
}
