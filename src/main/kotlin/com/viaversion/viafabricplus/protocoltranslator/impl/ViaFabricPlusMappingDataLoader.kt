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
package com.viaversion.viafabricplus.protocoltranslator.impl

import com.viaversion.viaversion.api.data.MappingDataLoader
import com.viaversion.viafabricplus.protocoltranslator.impl.ViaFabricPlusMappingDataLoader
import java.util.HashMap

class ViaFabricPlusMappingDataLoader private constructor() : MappingDataLoader(ViaFabricPlusMappingDataLoader::class.java, "assets/viafabricplus/data/") {

    init {
        val materialsData: com.viaversion.viaversion.libs.gson.JsonObject = this.loadData("materials-1.19.4.json")!!
        for (entry in materialsData.getAsJsonObject("materials").entrySet()) {
            val materialData: com.viaversion.viaversion.libs.gson.JsonObject = entry.value.getAsJsonObject()
            ViaFabricPlusMappingDataLoader.Companion.MATERIALS.put(entry.key, com.viaversion.viafabricplus.protocoltranslator.impl.ViaFabricPlusMappingDataLoader.Material(
            materialData.get("blocksMovement").getAsBoolean(), 
            materialData.get("burnable").getAsBoolean(), 
            materialData.get("liquid").getAsBoolean(), 
            materialData.get("blocksLight").getAsBoolean(), 
            materialData.get("replaceable").getAsBoolean(), 
            materialData.get("solid").getAsBoolean()
            ))
        }
        for (blockEntry in materialsData.getAsJsonObject("blocks").entrySet()) {
            val blockMaterials: kotlin.collections.MutableMap<com.viaversion.viaversion.api.protocol.version.ProtocolVersion, kotlin.String> = HashMap<com.viaversion.viaversion.api.protocol.version.ProtocolVersion, kotlin.String>()
            for (entry in blockEntry.value.getAsJsonObject().entrySet()) {
                val version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion = com.viaversion.viaversion.api.protocol.version.ProtocolVersion.getClosest(entry.key)!!
                kotlin.checkNotNull(version) {"Unknown protocol version: " + entry.key} 
                blockMaterials.put(version, entry.value.getAsString())
            }
            ViaFabricPlusMappingDataLoader.Companion.BLOCK_MATERIALS.put(blockEntry.key, blockMaterials)
        }
    }
    
    @JvmRecord 
    data class Material (val blocksMovement: kotlin.Boolean, val burnable: kotlin.Boolean, val liquid: kotlin.Boolean, val blocksLight: kotlin.Boolean, 
    val replaceable: kotlin.Boolean, val solid: kotlin.Boolean){        
    }
    
    companion object {
        val MATERIALS: kotlin.collections.MutableMap<kotlin.String, com.viaversion.viafabricplus.protocoltranslator.impl.ViaFabricPlusMappingDataLoader.Material> = HashMap<kotlin.String, com.viaversion.viafabricplus.protocoltranslator.impl.ViaFabricPlusMappingDataLoader.Material>()
        val BLOCK_MATERIALS: kotlin.collections.MutableMap<kotlin.String, kotlin.collections.MutableMap<com.viaversion.viaversion.api.protocol.version.ProtocolVersion, kotlin.String>> = HashMap<kotlin.String, kotlin.collections.MutableMap<com.viaversion.viaversion.api.protocol.version.ProtocolVersion, kotlin.String>>()
        
        val INSTANCE: ViaFabricPlusMappingDataLoader = ViaFabricPlusMappingDataLoader()
        
        fun getBlockMaterial(block: net.minecraft.world.level.block.Block): kotlin.String {
            return ViaFabricPlusMappingDataLoader.Companion.getBlockMaterial(block, com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion())!!
        }
        
        fun getBlockMaterial(block: net.minecraft.world.level.block.Block, version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion): kotlin.String? {
            var version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion = version
            if (version.newerThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_19_4)) {
                version = com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_19_4
            }
            
            val materials: kotlin.collections.MutableMap<com.viaversion.viaversion.api.protocol.version.ProtocolVersion, kotlin.String> = ViaFabricPlusMappingDataLoader.Companion.BLOCK_MATERIALS.get(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).toString())!!
            if (materials == null) {
                return null
            }
            
            for (materialEntry in materials.entries) {
                if (version.olderThanOrEqualTo(materialEntry.key)) {
                    return materialEntry.value
                }
            }
            return null
        }
        
    }}
