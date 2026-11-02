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
package com.viaversion.viafabricplus.util.network

import com.viaversion.viafabricplus.util.network.DataCustomPayload
import com.viaversion.viafabricplus.util.network.SyncTasks
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import io.netty.buffer.Unpooled

data class DataCustomPayload(val buf: net.minecraft.network.FriendlyByteBuf) : net.minecraft.network.protocol.common.custom.CustomPacketPayload {

    override fun type(): net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<out net.minecraft.network.protocol.common.custom.CustomPacketPayload> {
        return DataCustomPayload.Companion.ID
    }
    
    companion object {
        val ID: net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<DataCustomPayload> = net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<DataCustomPayload>(net.minecraft.resources.Identifier.parse(SyncTasks.PACKET_SYNC_IDENTIFIER))
        
        fun init() {
            PayloadTypeRegistry.clientboundConfiguration().register<DataCustomPayload>(DataCustomPayload.Companion.ID, net.minecraft.network.protocol.common.custom.CustomPacketPayload.codec<net.minecraft.network.FriendlyByteBuf, DataCustomPayload>(net.minecraft.network.codec.StreamMemberEncoder {value: DataCustomPayload, buf: net.minecraft.network.FriendlyByteBuf -> throw java.lang.UnsupportedOperationException("DataCustomPayload is a read-only packet")
            }, net.minecraft.network.codec.StreamDecoder {buf: net.minecraft.network.FriendlyByteBuf -> DataCustomPayload(net.minecraft.network.FriendlyByteBuf(Unpooled.copiedBuffer(buf.readSlice(buf.readableBytes()))))}))
        }
        
    }}
