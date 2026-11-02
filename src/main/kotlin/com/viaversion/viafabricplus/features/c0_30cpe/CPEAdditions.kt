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
package com.viaversion.viafabricplus.features.c0_30cpe

import net.raphimc.vialegacy.protocol.classic.c0_30cpetoc0_28_30.data.ClassicProtocolExtension
import net.raphimc.vialegacy.protocol.classic.c0_30cpetoc0_28_30.packet.ClientboundPacketsc0_30cpe
import java.util.HashMap
import com.viaversion.viafabricplus.features.c0_30cpe.CPEAdditions
import com.viaversion.viaversion.api.connection.UserConnection
import io.netty.buffer.ByteBuf
import net.raphimc.vialegacy.api.LegacyProtocolVersion

object CPEAdditions {
    val ALLOWED_EXTENSIONS: kotlin.collections.MutableList<ClassicProtocolExtension> = java.util.ArrayList<ClassicProtocolExtension>()
    val CUSTOM_PACKETS: kotlin.collections.MutableMap<kotlin.Int, ClientboundPacketsc0_30cpe> = HashMap<kotlin.Int, ClientboundPacketsc0_30cpe>()
    val EXTENDED_CLASSIC_ITEMS: kotlin.collections.MutableList<net.minecraft.world.item.Item> = java.util.ArrayList<net.minecraft.world.item.Item>()
    
    lateinit var EXT_WEATHER_TYPE: ClientboundPacketsc0_30cpe
    
    private var snowing: kotlin.Boolean = false
    
    fun init() {
        CPEAdditions.allowExtension(ClassicProtocolExtension.ENV_WEATHER_TYPE)
        CPEAdditions.EXT_WEATHER_TYPE = CPEAdditions.createNewPacket(ClassicProtocolExtension.ENV_WEATHER_TYPE, 31, java.util.function.BiConsumer {user: UserConnection, buf: ByteBuf -> buf.readByte()})
    }
    
    fun postInit() {
        CPEAdditions.EXTENDED_CLASSIC_ITEMS.add(net.minecraft.world.item.Items.COBBLESTONE_SLAB)
        CPEAdditions.EXTENDED_CLASSIC_ITEMS.add(net.minecraft.world.item.Items.DEAD_BUSH)
        CPEAdditions.EXTENDED_CLASSIC_ITEMS.add(net.minecraft.world.item.Items.SANDSTONE)
        CPEAdditions.EXTENDED_CLASSIC_ITEMS.add(net.minecraft.world.item.Items.SNOW)
        CPEAdditions.EXTENDED_CLASSIC_ITEMS.add(net.minecraft.world.item.Items.TORCH)
        CPEAdditions.EXTENDED_CLASSIC_ITEMS.add(net.minecraft.world.item.Items.WOOL.brown())
        CPEAdditions.EXTENDED_CLASSIC_ITEMS.add(net.minecraft.world.item.Items.ICE)
        CPEAdditions.EXTENDED_CLASSIC_ITEMS.add(net.minecraft.world.item.Items.CHISELED_QUARTZ_BLOCK)
        CPEAdditions.EXTENDED_CLASSIC_ITEMS.add(net.minecraft.world.item.Items.NETHER_QUARTZ_ORE)
        CPEAdditions.EXTENDED_CLASSIC_ITEMS.add(net.minecraft.world.item.Items.QUARTZ_PILLAR)
        CPEAdditions.EXTENDED_CLASSIC_ITEMS.add(net.minecraft.world.item.Items.JUKEBOX)
        CPEAdditions.EXTENDED_CLASSIC_ITEMS.add(net.minecraft.world.item.Items.STONE_BRICKS)
    }
    
    fun isSnowing(): kotlin.Boolean {
        return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion() == LegacyProtocolVersion.c0_30cpe && CPEAdditions.snowing
    }
    
    fun setSnowing(snowing: kotlin.Boolean) {
        CPEAdditions.snowing = snowing
    }
    
    fun allowExtension(classicProtocolExtension: ClassicProtocolExtension) {
        CPEAdditions.ALLOWED_EXTENSIONS.add(classicProtocolExtension)
    }
    
    fun createNewPacket(classicProtocolExtension: ClassicProtocolExtension, packetId: kotlin.Int, packetSplitter: java.util.function.BiConsumer<UserConnection, ByteBuf>): ClientboundPacketsc0_30cpe {
        val packet: ClientboundPacketsc0_30cpe = net.lenni0451.reflect.Enums.newInstance<ClientboundPacketsc0_30cpe>(ClientboundPacketsc0_30cpe::class.java, classicProtocolExtension.getName(), ClassicProtocolExtension.entries.size, kotlin.arrayOf<java.lang.Class<*>>(kotlin.Int::class.javaPrimitiveType!!, java.util.function.BiConsumer::class.java), kotlin.arrayOf<kotlin.Any>(packetId, packetSplitter))
        net.lenni0451.reflect.Enums.addEnumInstance<ClientboundPacketsc0_30cpe>(ClientboundPacketsc0_30cpe::class.java, packet)
        CPEAdditions.CUSTOM_PACKETS.put(packetId, packet)
        
        return packet
    }
    
}
