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
package com.viaversion.viafabricplus.protocoltranslator

import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslationImpl
import io.netty.buffer.Unpooled
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper
import com.viaversion.viafabricplus.protocoltranslator.protocol.ViaFabricPlusProtocol
import com.viaversion.viafabricplus.ViaFabricPlusImpl
import com.viaversion.viaversion.protocols.v1_12to1_12_1.packet.ClientboundPackets1_12_1
import com.viaversion.viaversion.connection.UserConnectionImpl
import com.viaversion.viafabricplus.protocoltranslator.util.NoPacketSendChannel
import com.viaversion.viaversion.api.protocol.ProtocolPipeline
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl
import com.viaversion.viaversion.api.protocol.ProtocolPathEntry
import com.viaversion.viaversion.api.Via

class ConversionsImpl : com.viaversion.viafabricplus.api.protocoltranslator.Conversions {

    override fun translateItem(stack: net.minecraft.world.item.ItemStack, targetVersion: com.viaversion.viaversion.api.protocol.version.ProtocolVersion): com.viaversion.viaversion.api.minecraft.item.Item? {
        val connection: UserConnection = createDummyUserConnection(ProtocolTranslationImpl.NATIVE_VERSION, targetVersion)
        
        try {
            val buf: net.minecraft.network.RegistryFriendlyByteBuf = net.minecraft.network.RegistryFriendlyByteBuf(Unpooled.buffer(), net.minecraft.client.Minecraft.getInstance().getConnection().registryAccess())
            buf.writeShort(0) // slot
            net.minecraft.world.item.ItemStack.OPTIONAL_UNTRUSTED_STREAM_CODEC.encode(buf, stack) // item
            
            val setCreativeModeSlot: PacketWrapper = PacketWrapper.create(ViaFabricPlusProtocol.INSTANCE.getSetCreativeModeSlot(), buf, connection)
            connection.getProtocolInfo().getPipeline().transform(com.viaversion.viaversion.api.protocol.packet.Direction.SERVERBOUND, com.viaversion.viaversion.api.protocol.packet.State.PLAY, setCreativeModeSlot)
            
            setCreativeModeSlot.read<kotlin.Short>(com.viaversion.viaversion.api.type.Types.SHORT) // slot
            return setCreativeModeSlot.read<com.viaversion.viaversion.api.minecraft.item.Item>(ViaFabricPlusProtocol.INSTANCE.getServerboundItemType(targetVersion)) // item
        }catch (t: Throwable) {
            ViaFabricPlusImpl.impl().logger().error("Error converting native item stack to ViaVersion {} item stack", targetVersion, t)
            return null
        }
    }
    
    override fun translateItem(item: com.viaversion.viaversion.api.minecraft.item.Item, sourceVersion: com.viaversion.viaversion.api.protocol.version.ProtocolVersion): net.minecraft.world.item.ItemStack {
        val connection: UserConnection = createDummyUserConnection(ProtocolTranslationImpl.NATIVE_VERSION, sourceVersion)
        
        try {
            val sourceProtocol: com.viaversion.viaversion.api.protocol.Protocol<*, *, *, *> = connection.getProtocolInfo().getPipeline().reversedPipes().stream().filter {p: com.viaversion.viaversion.api.protocol.Protocol<*, *, *, *> -> !p.isBaseProtocol()} .findFirst().orElseThrow()
            val containerSetSlot: PacketWrapper = PacketWrapper.create(sourceProtocol.getPacketTypesProvider().unmappedClientboundType(com.viaversion.viaversion.api.protocol.packet.State.PLAY, ClientboundPackets1_12_1.CONTAINER_SET_SLOT.getName()), connection)
            if (sourceVersion.newerThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8)) {
                containerSetSlot.write<kotlin.Short>(com.viaversion.viaversion.api.type.Types.UNSIGNED_BYTE, 0.toShort()) // window id
            } else {
                containerSetSlot.write<kotlin.Byte>(com.viaversion.viaversion.api.type.Types.BYTE, 0.toByte()) // window id
            }
            containerSetSlot.write<kotlin.Short>(com.viaversion.viaversion.api.type.Types.SHORT, 0.toShort()) // slot
            containerSetSlot.write<com.viaversion.viaversion.api.minecraft.item.Item>(ViaFabricPlusProtocol.INSTANCE.getClientboundItemType(sourceVersion), if (item != null) item.copy() else null) // item
            
            containerSetSlot.resetReader()
            containerSetSlot.user().getProtocolInfo().getPipeline().transform(com.viaversion.viaversion.api.protocol.packet.Direction.CLIENTBOUND, com.viaversion.viaversion.api.protocol.packet.State.PLAY, containerSetSlot)
            val buf: net.minecraft.network.RegistryFriendlyByteBuf = net.minecraft.network.RegistryFriendlyByteBuf(Unpooled.buffer(), net.minecraft.client.Minecraft.getInstance().getConnection().registryAccess())
            containerSetSlot.setPacketType(null)
            containerSetSlot.writeToBuffer(buf)
            
            buf.readUnsignedByte() // sync id
            buf.readVarInt() // revision
            buf.readShort() // slot
            return net.minecraft.world.item.ItemStack.OPTIONAL_STREAM_CODEC.decode(buf)
        }catch (t: Throwable) {
            ViaFabricPlusImpl.impl().logger().error("Error converting ViaVersion {} item to native item stack", sourceVersion, t)
            return net.minecraft.world.item.ItemStack.EMPTY
        }
    }
    
    override fun createDummyUserConnection(clientVersion: com.viaversion.viaversion.api.protocol.version.ProtocolVersion, serverVersion: com.viaversion.viaversion.api.protocol.version.ProtocolVersion): UserConnection {
        val user: UserConnection = UserConnectionImpl(NoPacketSendChannel.INSTANCE, true)
        val pipeline: ProtocolPipeline = ProtocolPipelineImpl(user)
        val path: kotlin.collections.MutableList<ProtocolPathEntry> = Via.getManager().getProtocolManager().getProtocolPath(clientVersion, serverVersion)!!
        if (path != null) {
            for (pair in path) {
                pipeline.add(pair.protocol())
                pair.protocol().init(user)
            }
        }
        
        val info: com.viaversion.viaversion.api.connection.ProtocolInfo = user.getProtocolInfo()
        info.setState(com.viaversion.viaversion.api.protocol.packet.State.PLAY)
        info.setProtocolVersion(clientVersion)
        info.setServerProtocolVersion(serverVersion)
        val mc: net.minecraft.client.Minecraft = net.minecraft.client.Minecraft.getInstance()
        if (mc.player != null) {
            val profile: com.mojang.authlib.GameProfile = mc.player.getGameProfile()
            info.setUsername(profile.name())
            info.setUuid(profile.id())
        }
        
        return user
    }
    
}
