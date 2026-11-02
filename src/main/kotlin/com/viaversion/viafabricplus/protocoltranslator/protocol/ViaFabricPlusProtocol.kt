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
package com.viaversion.viafabricplus.protocoltranslator.protocol

import com.viaversion.viaversion.api.protocol.AbstractProtocol
import com.viaversion.viaversion.protocols.v26_2to26_3.packet.ClientboundPacket26_3
import com.viaversion.viaversion.protocols.v26_2to26_3.packet.ServerboundPacket26_3
import com.viaversion.viaversion.protocols.v1_21_7to1_21_9.packet.ServerboundConfigurationPackets1_21_9
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper
import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viafabricplus.features.v1_21_5.WolfHealthTracker1_14_4
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType
import com.viaversion.viafabricplus.protocoltranslator.protocol.ViaFabricPlusProtocol.ItemTypes
import com.viaversion.viafabricplus.protocoltranslator.protocol.ViaFabricPlusProtocol
import com.viaversion.viaversion.api.protocol.packet.provider.PacketTypesProvider
import com.viaversion.viaversion.api.protocol.packet.provider.SimplePacketTypesProvider
import com.viaversion.viaversion.util.ProtocolUtil
import com.viaversion.viaversion.protocols.v26_2to26_3.packet.ClientboundPackets26_3
import com.viaversion.viaversion.protocols.v26_2to26_3.packet.ClientboundConfigurationPackets26_3
import com.viaversion.viaversion.protocols.v26_2to26_3.packet.ServerboundPackets26_3
import java.util.NavigableMap
import java.util.TreeMap
import net.raphimc.vialegacy.api.LegacyProtocolVersion
import net.raphimc.vialegacy.protocol.release.r1_4_2tor1_4_4_5.types.Types1_4_2
import net.raphimc.vialegacy.protocol.beta.b1_8_0_1tor1_0_0_1.types.Typesb1_8_0_1
import net.raphimc.vialegacy.protocol.release.r1_2_4_5tor1_3_1_2.types.Types1_2_4
import net.raphimc.vialegacy.protocol.release.r1_7_6_10tor1_8.types.Types1_7_6
import com.viaversion.viaversion.api.type.types.version.VersionedTypes
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslationImpl

class ViaFabricPlusProtocol : AbstractProtocol<ClientboundPacket26_3, ClientboundPacket26_3, ServerboundPacket26_3, ServerboundPacket26_3>(ClientboundPacket26_3::class.java, ClientboundPacket26_3::class.java, ServerboundPacket26_3::class.java, ServerboundPacket26_3::class.java) {

    override fun registerPackets() {
        // Fixes an issue where the Fabric Particle API causes disconnects when both the client and server have the mod installed and both are 1.21.5+.
        // See https://github.com/ViaVersion/ViaFabric/issues/428
        this.registerServerbound(ServerboundConfigurationPackets1_21_9.CUSTOM_PAYLOAD, PacketHandler {
        wrapper: PacketWrapper -> val serverVersion: com.viaversion.viaversion.api.protocol.version.ProtocolVersion = wrapper.user().getProtocolInfo().serverProtocolVersion()
        if (serverVersion.newerThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_5) && serverVersion != wrapper.user().getProtocolInfo().protocolVersion()) {
            val channel: kotlin.String = com.viaversion.viaversion.util.Key.namespaced(wrapper.passthrough<kotlin.String>(com.viaversion.viaversion.api.type.Types.STRING))
            if (channel == "minecraft:register" || channel == "minecraft:unregister") {
                val channels: kotlin.collections.MutableList<kotlin.String> = com.google.common.collect.Lists.newArrayList<kotlin.String>(*kotlin.text.String(wrapper.passthrough<ByteArray>(com.viaversion.viaversion.api.type.Types.SERVERBOUND_CUSTOM_PAYLOAD_DATA), java.nio.charset.StandardCharsets.UTF_8).split("\u0000".toRegex()).dropLastWhile {it.isEmpty()} .toTypedArray())
                if (channels.remove("fabric:extended_block_particle_option_sync")) {
                    if (!channels.isEmpty()) {
                        wrapper.set<ByteArray>(com.viaversion.viaversion.api.type.Types.SERVERBOUND_CUSTOM_PAYLOAD_DATA, 0, java.lang.String.join("\u0000", channels).toByteArray(java.nio.charset.StandardCharsets.UTF_8))
                    } else {
                        wrapper.cancel()
                    }
                }
            }
        }
        
        })
    }
    
    override fun init(connection: UserConnection) {
        super.init(connection)
        
                // UserConnection#getProtocolInfo is not initialized yet
        val serverVersion: com.viaversion.viaversion.api.protocol.version.ProtocolVersion = com.viaversion.viafabricplus.ViaFabricPlus.api().protocolTranslation().targetVersion(connection.getChannel()!!)
        if (serverVersion.olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_14_4)) {
            connection.put(WolfHealthTracker1_14_4())
        }
    }
    
    override fun applySharedRegistrations() {
        // Not for us, protocols will already track states down the line
    }
    
    fun getClientboundCustomPayloadPacketType(): ClientboundPacketType {
        return packetTypesProvider.unmappedClientboundType(com.viaversion.viaversion.api.protocol.packet.State.PLAY, "CUSTOM_PAYLOAD")!!
    }
    
    fun getCustomPayloadPacketType(): ServerboundPacketType {
        return packetTypesProvider.unmappedServerboundType(com.viaversion.viaversion.api.protocol.packet.State.PLAY, "CUSTOM_PAYLOAD")!!
    }
    
    fun getSetCreativeModeSlot(): ServerboundPacketType {
        return packetTypesProvider.unmappedServerboundType(com.viaversion.viaversion.api.protocol.packet.State.PLAY, "SET_CREATIVE_MODE_SLOT")!!
    }
    
    fun getServerboundItemType(targetVersion: com.viaversion.viaversion.api.protocol.version.ProtocolVersion): com.viaversion.viaversion.api.type.Type<com.viaversion.viaversion.api.minecraft.item.Item> {
        val entry: kotlin.collections.MutableMap.MutableEntry<com.viaversion.viaversion.api.protocol.version.ProtocolVersion, ItemTypes> = ViaFabricPlusProtocol.Companion.ITEM_TYPES.ceilingEntry(targetVersion)
        return entry.value.serverbound
    }
    
    fun getClientboundItemType(targetVersion: com.viaversion.viaversion.api.protocol.version.ProtocolVersion): com.viaversion.viaversion.api.type.Type<com.viaversion.viaversion.api.minecraft.item.Item> {
        val entry: kotlin.collections.MutableMap.MutableEntry<com.viaversion.viaversion.api.protocol.version.ProtocolVersion, ItemTypes> = ViaFabricPlusProtocol.Companion.ITEM_TYPES.ceilingEntry(targetVersion)
        return entry.value.clientbound
    }
    
    override fun createPacketTypesProvider(): PacketTypesProvider<ClientboundPacket26_3, ClientboundPacket26_3, ServerboundPacket26_3, ServerboundPacket26_3> {
        return SimplePacketTypesProvider<ClientboundPacket26_3, ClientboundPacket26_3, ServerboundPacket26_3, ServerboundPacket26_3>(
        ProtocolUtil.packetTypeMap<ClientboundPacket26_3>(unmappedClientboundPacketType, ClientboundPackets26_3::class.java, ClientboundConfigurationPackets26_3::class.java), 
        ProtocolUtil.packetTypeMap<ClientboundPacket26_3>(mappedClientboundPacketType, ClientboundPackets26_3::class.java, ClientboundConfigurationPackets26_3::class.java), 
        ProtocolUtil.packetTypeMap<ServerboundPacket26_3>(mappedServerboundPacketType, ServerboundPackets26_3::class.java, ServerboundConfigurationPackets1_21_9::class.java), 
        ProtocolUtil.packetTypeMap<ServerboundPacket26_3>(unmappedServerboundPacketType, ServerboundPackets26_3::class.java, ServerboundConfigurationPackets1_21_9::class.java)
        )
    }
    
    @JvmRecord 
    private data class ItemTypes (val clientbound: com.viaversion.viaversion.api.type.Type<com.viaversion.viaversion.api.minecraft.item.Item>, val serverbound: com.viaversion.viaversion.api.type.Type<com.viaversion.viaversion.api.minecraft.item.Item>){        
        companion object {
            fun same(type: com.viaversion.viaversion.api.type.Type<com.viaversion.viaversion.api.minecraft.item.Item>): ItemTypes {
                return ItemTypes(type, type)
            }
            
        }}
    
    companion object {
        val INSTANCE: ViaFabricPlusProtocol = ViaFabricPlusProtocol()
        
        private val ITEM_TYPES: NavigableMap<com.viaversion.viaversion.api.protocol.version.ProtocolVersion, ItemTypes> = TreeMap<com.viaversion.viaversion.api.protocol.version.ProtocolVersion, ItemTypes>()
        
        init {
            ViaFabricPlusProtocol.Companion.ITEM_TYPES.put(LegacyProtocolVersion.b1_8tob1_8_1, ItemTypes(Types1_4_2.NBTLESS_ITEM, Typesb1_8_0_1.CREATIVE_ITEM))
            ViaFabricPlusProtocol.Companion.ITEM_TYPES.put(LegacyProtocolVersion.r1_2_4tor1_2_5, ItemTypes.Companion.same(Types1_2_4.NBT_ITEM))
            ViaFabricPlusProtocol.Companion.ITEM_TYPES.put(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_7_6, ItemTypes.Companion.same(Types1_7_6.ITEM))
            ViaFabricPlusProtocol.Companion.ITEM_TYPES.put(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2, ItemTypes.Companion.same(com.viaversion.viaversion.api.type.Types.ITEM1_8))
            ViaFabricPlusProtocol.Companion.ITEM_TYPES.put(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_13_1, ItemTypes.Companion.same(com.viaversion.viaversion.api.type.Types.ITEM1_13))
            ViaFabricPlusProtocol.Companion.ITEM_TYPES.put(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20, ItemTypes.Companion.same(com.viaversion.viaversion.api.type.Types.ITEM1_13_2))
            ViaFabricPlusProtocol.Companion.ITEM_TYPES.put(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20_3, ItemTypes.Companion.same(com.viaversion.viaversion.api.type.Types.ITEM1_20_2))
            ViaFabricPlusProtocol.Companion.ITEM_TYPES.put(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20_5, ItemTypes.Companion.same(VersionedTypes.V1_20_5.item))
            ViaFabricPlusProtocol.Companion.ITEM_TYPES.put(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21, ItemTypes.Companion.same(VersionedTypes.V1_21.item))
            ViaFabricPlusProtocol.Companion.ITEM_TYPES.put(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_2, ItemTypes.Companion.same(VersionedTypes.V1_21_2.item))
            ViaFabricPlusProtocol.Companion.ITEM_TYPES.put(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_4, ItemTypes.Companion.same(VersionedTypes.V1_21_4.item))
            ViaFabricPlusProtocol.Companion.ITEM_TYPES.put(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_5, ItemTypes(VersionedTypes.V1_21_5.item, VersionedTypes.V1_21_5.lengthPrefixedItem))
            ViaFabricPlusProtocol.Companion.ITEM_TYPES.put(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_7, ItemTypes(VersionedTypes.V1_21_6.item, VersionedTypes.V1_21_6.lengthPrefixedItem))
            ViaFabricPlusProtocol.Companion.ITEM_TYPES.put(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_9, ItemTypes(VersionedTypes.V1_21_9.item, VersionedTypes.V1_21_9.lengthPrefixedItem))
            ViaFabricPlusProtocol.Companion.ITEM_TYPES.put(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_11, ItemTypes(VersionedTypes.V1_21_11.item, VersionedTypes.V1_21_11.lengthPrefixedItem))
            ViaFabricPlusProtocol.Companion.ITEM_TYPES.put(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v26_1, ItemTypes(VersionedTypes.V26_1.item, VersionedTypes.V26_1.lengthPrefixedItem))
            ViaFabricPlusProtocol.Companion.ITEM_TYPES.put(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v26_2, ItemTypes(VersionedTypes.V26_2.item, VersionedTypes.V26_2.lengthPrefixedItem))
            ViaFabricPlusProtocol.Companion.ITEM_TYPES.put(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v26_3, ItemTypes(VersionedTypes.V26_3.item, VersionedTypes.V26_3.lengthPrefixedItem))
            
            kotlin.check(ViaFabricPlusProtocol.Companion.ITEM_TYPES.containsKey(ProtocolTranslationImpl.NATIVE_VERSION)) {"Missing item type for native version"} 
        }
        
    }}
