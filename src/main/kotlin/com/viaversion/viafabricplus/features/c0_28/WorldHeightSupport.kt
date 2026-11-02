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
package com.viaversion.viafabricplus.features.c0_28

import com.viaversion.viaversion.api.protocol.remapper.PacketHandler
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper
import net.raphimc.vialegacy.api.LegacyProtocolVersion
import com.viaversion.viafabricplus.features.c0_28.WorldHeightSupport
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_17
import net.raphimc.vialegacy.protocol.classic.c0_28_30toa1_0_15.provider.ClassicWorldHeightProvider
import com.viaversion.viaversion.api.Via
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection
import java.util.BitSet
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers
import net.raphimc.vialegacy.protocol.classic.c0_28_30toa1_0_15.model.ClassicLevel
import net.raphimc.vialegacy.protocol.classic.c0_28_30toa1_0_15.storage.ClassicLevelStorage
import com.viaversion.viaversion.api.connection.UserConnection

object WorldHeightSupport {
    fun handleJoinGame(parentHandler: PacketHandler): PacketHandler {
        return PacketHandler {
        wrapper: PacketWrapper -> parentHandler.handle(wrapper)
        if (wrapper.isCancelled()) {
            return@PacketHandler 
        }
        
        if (wrapper.user().getProtocolInfo().serverProtocolVersion().olderThanOrEqualTo(LegacyProtocolVersion.c0_28toc0_30)) {
            for (dimension in wrapper.get<com.viaversion.nbt.tag.CompoundTag>(com.viaversion.viaversion.api.type.Types.NAMED_COMPOUND_TAG, 0).getCompoundTag("minecraft:dimension_type").getListTag<com.viaversion.nbt.tag.CompoundTag>("value", com.viaversion.nbt.tag.CompoundTag::class.java)) {
                WorldHeightSupport.changeDimensionTagHeight(wrapper.user(), dimension.getCompoundTag("element"))
            }
            WorldHeightSupport.changeDimensionTagHeight(wrapper.user(), wrapper.get<com.viaversion.nbt.tag.CompoundTag>(com.viaversion.viaversion.api.type.Types.NAMED_COMPOUND_TAG, 1))
        }
        
        }
    }
    
    fun handleRespawn(parentHandler: PacketHandler): PacketHandler {
        return PacketHandler {
        wrapper: PacketWrapper -> parentHandler.handle(wrapper)
        if (wrapper.isCancelled()) {
            return@PacketHandler 
        }
        
        if (wrapper.user().getProtocolInfo().serverProtocolVersion().olderThanOrEqualTo(LegacyProtocolVersion.c0_28toc0_30)) {
            WorldHeightSupport.changeDimensionTagHeight(wrapper.user(), wrapper.get<com.viaversion.nbt.tag.CompoundTag>(com.viaversion.viaversion.api.type.Types.NAMED_COMPOUND_TAG, 0))
        }
        
        }
    }
    
    fun handleChunkData(parentHandler: PacketHandler): PacketHandler {
        return PacketHandler {
        wrapper: PacketWrapper -> parentHandler.handle(wrapper)
        if (wrapper.isCancelled()) {
            return@PacketHandler 
        }
        
        if (wrapper.user().getProtocolInfo().serverProtocolVersion().olderThanOrEqualTo(LegacyProtocolVersion.c0_28toc0_30)) {
            wrapper.resetReader()
            val chunk: com.viaversion.viaversion.api.minecraft.chunks.Chunk = wrapper.read<com.viaversion.viaversion.api.minecraft.chunks.Chunk>(ChunkType1_17(16))
            wrapper.write<com.viaversion.viaversion.api.minecraft.chunks.Chunk>(ChunkType1_17(chunk.getSections().size), chunk)
            
            val heightProvider: ClassicWorldHeightProvider = Via.getManager().getProviders().get<ClassicWorldHeightProvider>(ClassicWorldHeightProvider::class.java)!!
            if (chunk.getSections().size < heightProvider.getMaxChunkSectionCount(wrapper.user())) { // Increase available sections to match new world height
                val newArray: kotlin.Array<ChunkSection> = kotlin.arrayOfNulls<ChunkSection>(heightProvider.getMaxChunkSectionCount(wrapper.user()).toInt()) as kotlin.Array<ChunkSection>
                java.lang.System.arraycopy(chunk.getSections(), 0, newArray, 0, chunk.getSections().size)
                chunk.setSections(newArray)
            }
            
            val chunkMask: BitSet = BitSet()
            for (i in chunk.getSections().indices) {
                if (chunk.getSections()[i] != null) chunkMask.set(i)
            }
            chunk.setChunkMask(chunkMask)
            
            val newBiomeData: IntArray = IntArray(chunk.getSections().size * 4 * 4 * 4)
            java.lang.System.arraycopy(chunk.getBiomeData(), 0, newBiomeData, 0, chunk.getBiomeData()!!.size)
            for (i in 64 ..< chunk.getSections().size * 4) { // copy top layer of old biome data all the way to max world height
                java.lang.System.arraycopy(chunk.getBiomeData(), chunk.getBiomeData()!!.size - 16, newBiomeData, i * 16, 16)
            }
            chunk.setBiomeData(newBiomeData)
            
            chunk.setHeightMap(com.viaversion.nbt.tag.CompoundTag()) // rip heightmap :(
        }
        
        }
    }
    
    fun handleUpdateLight(parentHandler: PacketHandler): PacketHandler {
        val classicLightHandler: PacketHandler = object : PacketHandlers() {
            public override fun register() {
                map<kotlin.Int>(com.viaversion.viaversion.api.type.Types.VAR_INT) // x
                map<kotlin.Int>(com.viaversion.viaversion.api.type.Types.VAR_INT) // y
                map<kotlin.Boolean>(com.viaversion.viaversion.api.type.Types.BOOLEAN) // trust edges
                handler(PacketHandler {
                wrapper: PacketWrapper -> wrapper.read<kotlin.Int>(com.viaversion.viaversion.api.type.Types.VAR_INT) // sky light mask
                wrapper.read<kotlin.Int>(com.viaversion.viaversion.api.type.Types.VAR_INT) // block light mask
                val emptySkyLightMask: kotlin.Int = wrapper.read<kotlin.Int>(com.viaversion.viaversion.api.type.Types.VAR_INT) // empty sky light mask
                val emptyBlockLightMask: kotlin.Int = wrapper.read<kotlin.Int>(com.viaversion.viaversion.api.type.Types.VAR_INT) // empty block light mask
                
                val level: ClassicLevel = wrapper.user().get<ClassicLevelStorage>(ClassicLevelStorage::class.java)!!.getClassicLevel()
                val heightProvider: ClassicWorldHeightProvider = Via.getManager().getProviders().get<ClassicWorldHeightProvider>(ClassicWorldHeightProvider::class.java)!!
                
                var sectionYCount: kotlin.Int = level.getSizeY() shr 4
                if (level.getSizeY() % 16 != 0) sectionYCount++
                if (sectionYCount > heightProvider.getMaxChunkSectionCount(wrapper.user())) {
                    sectionYCount = heightProvider.getMaxChunkSectionCount(wrapper.user()).toInt()
                }
                
                val lightArrays: kotlin.collections.MutableList<ByteArray> = java.util.ArrayList<ByteArray>()
                while (wrapper.isReadable(com.viaversion.viaversion.api.type.Types.BYTE_ARRAY_PRIMITIVE, 0)) {
                    lightArrays.add(wrapper.read<ByteArray>(com.viaversion.viaversion.api.type.Types.BYTE_ARRAY_PRIMITIVE))
                }
                
                var skyLightCount: kotlin.Int = 16
                var blockLightCount: kotlin.Int = sectionYCount
                if (lightArrays.size == 16 + 2) {
                    blockLightCount = 0
                } else if (lightArrays.size == 16 + sectionYCount + 2) {
                } else if (lightArrays.size == sectionYCount + sectionYCount + 2) {
                    skyLightCount = sectionYCount
                }
                skyLightCount += 2 // Chunk below 0 and above 255
                
                val skyLightMask: BitSet = BitSet()
                val blockLightMask: BitSet = BitSet()
                skyLightMask.set(0, skyLightCount)
                blockLightMask.set(0, blockLightCount)
                
                wrapper.write<kotlin.LongArray>(com.viaversion.viaversion.api.type.Types.LONG_ARRAY_PRIMITIVE, skyLightMask.toLongArray())
                wrapper.write<kotlin.LongArray>(com.viaversion.viaversion.api.type.Types.LONG_ARRAY_PRIMITIVE, blockLightMask.toLongArray())
                wrapper.write<kotlin.LongArray>(com.viaversion.viaversion.api.type.Types.LONG_ARRAY_PRIMITIVE, kotlin.LongArray(emptySkyLightMask))
                wrapper.write<kotlin.LongArray>(com.viaversion.viaversion.api.type.Types.LONG_ARRAY_PRIMITIVE, kotlin.LongArray(emptyBlockLightMask))
                
                wrapper.write<kotlin.Int>(com.viaversion.viaversion.api.type.Types.VAR_INT, skyLightCount)
                for (i in 0 ..< skyLightCount) {
                    wrapper.write<ByteArray>(com.viaversion.viaversion.api.type.Types.BYTE_ARRAY_PRIMITIVE, lightArrays.removeAt(0))
                }
                wrapper.write<kotlin.Int>(com.viaversion.viaversion.api.type.Types.VAR_INT, blockLightCount)
                for (i in 0 ..< blockLightCount) {
                    wrapper.write<ByteArray>(com.viaversion.viaversion.api.type.Types.BYTE_ARRAY_PRIMITIVE, lightArrays.removeAt(0))
                }
                
                })
            }
        }
        
        return PacketHandler {wrapper: PacketWrapper -> if (wrapper.user().getProtocolInfo().serverProtocolVersion().olderThanOrEqualTo(LegacyProtocolVersion.c0_28toc0_30)) {
            classicLightHandler.handle(wrapper)
        } else {
            parentHandler.handle(wrapper)
        }
        }
    }
    
    private fun changeDimensionTagHeight(connection: UserConnection, tag: com.viaversion.nbt.tag.CompoundTag) {
        tag.putInt("height", Via.getManager().getProviders().get<ClassicWorldHeightProvider>(ClassicWorldHeightProvider::class.java)!!.getMaxChunkSectionCount(connection).toInt() shl 4)
    }
    
}
