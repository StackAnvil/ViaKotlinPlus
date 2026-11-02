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
package com.viaversion.viafabricplus.features.global

import java.util.HashMap
import com.viaversion.viafabricplus.protocoltranslator.impl.ViaFabricPlusMappingDataLoader
import com.viaversion.viafabricplus.features.global.EntityDimensionDiff

object EntityDimensionDiff {
    private val ENTITY_DIMENSIONS: kotlin.collections.MutableMap<net.minecraft.world.entity.EntityType<*>, kotlin.collections.MutableMap<com.viaversion.viaversion.api.protocol.version.ProtocolVersion, net.minecraft.world.entity.EntityDimensions>> = HashMap<net.minecraft.world.entity.EntityType<*>, kotlin.collections.MutableMap<com.viaversion.viaversion.api.protocol.version.ProtocolVersion, net.minecraft.world.entity.EntityDimensions>>()
    
    fun init() {
        // TODO: Find a way to isolate baby (or other conditional dimension changes) across entities with same code patterns
        // to reduce the need of manual mixing into each entity
        val dimensionDiff: com.viaversion.viaversion.libs.gson.JsonObject = ViaFabricPlusMappingDataLoader.INSTANCE.loadData("entity-dimensions.json")!!
        for (entity in dimensionDiff.keySet()) {
            val entityType: net.minecraft.world.entity.EntityType<*> = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getOptional(net.minecraft.resources.Identifier.parse(entity)).orElse(null)
            kotlin.checkNotNull(entityType) {"Unknown entity: " + entity} 
            
            val versions: com.viaversion.viaversion.libs.gson.JsonObject = dimensionDiff.getAsJsonObject(entity)
            val dimensionMap: kotlin.collections.MutableMap<com.viaversion.viaversion.api.protocol.version.ProtocolVersion, net.minecraft.world.entity.EntityDimensions> = HashMap<com.viaversion.viaversion.api.protocol.version.ProtocolVersion, net.minecraft.world.entity.EntityDimensions>()
            for (version in versions.keySet()) {
                val protocolVersion: com.viaversion.viaversion.api.protocol.version.ProtocolVersion = com.viaversion.viaversion.api.protocol.version.ProtocolVersion.getClosest(version)!!
                kotlin.checkNotNull(protocolVersion) {"Unknown protocol version: " + version} 
                
                val dimensionData: com.viaversion.viaversion.libs.gson.JsonObject = versions.getAsJsonObject(version)
                val width: kotlin.Float = dimensionData.get("width").getAsFloat()
                val height: kotlin.Float = dimensionData.get("height").getAsFloat()
                val eyeHeight: kotlin.Float = dimensionData.get("eyeHeight").getAsFloat()
                val fixed: kotlin.Boolean = dimensionData.get("fixed").getAsBoolean()
                val entityDimensions: net.minecraft.world.entity.EntityDimensions = net.minecraft.world.entity.EntityDimensions(width, height, eyeHeight, entityType.dimensions.attachments(), fixed)
                dimensionMap.put(protocolVersion, entityDimensions)
            }
            EntityDimensionDiff.ENTITY_DIMENSIONS.put(entityType, dimensionMap)
        }
        
        com.viaversion.viafabricplus.ViaFabricPlus.api().protocolTranslation().addChangeProtocolVersionListener(java.util.function.BiConsumer {oldVersion: com.viaversion.viaversion.api.protocol.version.ProtocolVersion, newVersion: com.viaversion.viaversion.api.protocol.version.ProtocolVersion -> net.minecraft.client.Minecraft.getInstance().execute(java.lang.Runnable {EntityDimensionDiff.ENTITY_DIMENSIONS.forEach {(entityType: net.minecraft.world.entity.EntityType<*>, dimensionMap: kotlin.collections.MutableMap<com.viaversion.viaversion.api.protocol.version.ProtocolVersion, net.minecraft.world.entity.EntityDimensions>) -> for (entry in dimensionMap.entries) {
            val version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion = entry.key
            val dimensions: net.minecraft.world.entity.EntityDimensions = entry.value
            if (oldVersion.newerThan(version) && newVersion.olderThanOrEqualTo(version)) {
                entityType.dimensions = dimensions
                break
            }
            if (newVersion.newerThanOrEqualTo(version) && oldVersion.olderThanOrEqualTo(version)) {
                entityType.dimensions = dimensions
            }
        }
        } })})
    }
    
}
