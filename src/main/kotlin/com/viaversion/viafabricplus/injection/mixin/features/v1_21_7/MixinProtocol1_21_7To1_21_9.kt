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
package com.viaversion.viafabricplus.injection.mixin.features.v1_21_7

import org.spongepowered.asm.mixin.Mixin
import com.viaversion.viaversion.protocols.v1_21_7to1_21_9.Protocol1_21_7To1_21_9
import com.viaversion.viaversion.protocols.v1_21_5to1_21_6.packet.ClientboundPacket1_21_6
import com.viaversion.viaversion.protocols.v1_21_7to1_21_9.packet.ClientboundPacket1_21_9
import com.viaversion.viaversion.protocols.v1_21_5to1_21_6.packet.ServerboundPacket1_21_6
import com.viaversion.viaversion.protocols.v1_21_7to1_21_9.packet.ServerboundPacket1_21_9
import com.viaversion.viaversion.api.protocol.AbstractProtocol
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.viaversion.viaversion.protocols.v1_21_5to1_21_6.packet.ClientboundPackets1_21_6
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper
import com.viaversion.viafabricplus.util.network.SyncTasks
import com.viaversion.viafabricplus.injection.access.v1_21_7.IGameTestBlockHighlightRenderer

@Mixin(value = [Protocol1_21_7To1_21_9::class], remap = false) 
abstract class MixinProtocol1_21_7To1_21_9 (unmappedClientboundPacketType: java.lang.Class<ClientboundPacket1_21_6>, mappedClientboundPacketType: java.lang.Class<ClientboundPacket1_21_9>, mappedServerboundPacketType: java.lang.Class<ServerboundPacket1_21_6>, unmappedServerboundPacketType: java.lang.Class<ServerboundPacket1_21_9>) : AbstractProtocol<ClientboundPacket1_21_6, ClientboundPacket1_21_9, ServerboundPacket1_21_6, ServerboundPacket1_21_9>(unmappedClientboundPacketType, mappedClientboundPacketType, mappedServerboundPacketType, unmappedServerboundPacketType) {

    @Inject(method = ["registerPackets"], at = [At("RETURN")]) private fun handleGameTestPayloads(ci: CallbackInfo) {
        registerClientbound(ClientboundPackets1_21_6.CUSTOM_PAYLOAD, PacketHandler {
        wrapper: PacketWrapper -> val channel: kotlin.String = wrapper.passthrough<kotlin.String>(com.viaversion.viaversion.api.type.Types.STRING)
        if (channel == "minecraft:debug/game_test_add_marker") {
            wrapper.set<kotlin.String>(com.viaversion.viaversion.api.type.Types.STRING, 0, SyncTasks.PACKET_SYNC_IDENTIFIER)
            wrapper.write<kotlin.String>(com.viaversion.viaversion.api.type.Types.STRING, SyncTasks.executeSyncTask(java.util.function.Consumer {
            buf: net.minecraft.network.RegistryFriendlyByteBuf -> val pos: net.minecraft.core.BlockPos = buf.readBlockPos()
            val color: kotlin.Int = buf.readInt()
            val name: kotlin.String = buf.readUtf()
            val duration: kotlin.Int = buf.readInt()
            
            val mixinTestDebugRenderer: IGameTestBlockHighlightRenderer = net.minecraft.client.Minecraft.getInstance().levelExtractor.gameTestBlockHighlightRenderer as IGameTestBlockHighlightRenderer
            mixinTestDebugRenderer.`viaFabricPlus$addMarker`(pos, color, name, duration)
            
            }))
        } else if (channel == "minecraft:debug/game_test_clear") {
            wrapper.set<kotlin.String>(com.viaversion.viaversion.api.type.Types.STRING, 0, SyncTasks.PACKET_SYNC_IDENTIFIER)
            wrapper.write<kotlin.String>(com.viaversion.viaversion.api.type.Types.STRING, SyncTasks.executeSyncTask(java.util.function.Consumer {buf: net.minecraft.network.RegistryFriendlyByteBuf -> net.minecraft.client.Minecraft.getInstance().levelExtractor.gameTestBlockHighlightRenderer.clear()}))
        }
        
        })
    }
    
}
