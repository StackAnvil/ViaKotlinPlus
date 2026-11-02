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
import com.viaversion.viafabricplus.features.global.ResourcePackHeaderDiff

object ResourcePackHeaderDiff {
    private val GAME_VERSION_DIFF: kotlin.collections.MutableMap<com.viaversion.viaversion.api.protocol.version.ProtocolVersion, net.minecraft.WorldVersion> = HashMap<com.viaversion.viaversion.api.protocol.version.ProtocolVersion, net.minecraft.WorldVersion>()
    
    fun init() {
        val diff: com.viaversion.viaversion.libs.gson.JsonObject = ViaFabricPlusMappingDataLoader.INSTANCE.loadData("resource-pack-headers.json")!!
        for (string in diff.keySet()) {
            ResourcePackHeaderDiff.fill(string, diff.getAsJsonObject(string))
        }
    }
    
    private fun fill(name: kotlin.String, data: com.viaversion.viaversion.libs.gson.JsonObject) {
        val version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion = com.viaversion.viaversion.api.protocol.version.ProtocolVersion.getProtocol(data.get("version").getAsInt())
        kotlin.check(version.isKnown()) {"Unknown protocol version: " + version.getOriginalVersion()} 
        
        val packFormat: com.viaversion.viaversion.libs.gson.JsonElement = data.get("pack_format")
        if (packFormat.isJsonObject()) {
            val major: kotlin.Int = packFormat.getAsJsonObject().get("major").getAsInt()
            val minor: kotlin.Int = packFormat.getAsJsonObject().get("minor").getAsInt()
            ResourcePackHeaderDiff.registerVersion(version, major, minor, name, name)
        } else {
            ResourcePackHeaderDiff.registerVersion(version, packFormat.getAsInt(), -1, name, name)
        }
    }
    
    fun get(version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion): net.minecraft.WorldVersion {
        if (!ResourcePackHeaderDiff.GAME_VERSION_DIFF.containsKey(version)) {
            return net.minecraft.SharedConstants.getCurrentVersion()
        } else {
            return ResourcePackHeaderDiff.GAME_VERSION_DIFF.get(version)!!
        }
    }
    
    private fun registerVersion(version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion, majorVersion: kotlin.Int, minorVersion: kotlin.Int, name: kotlin.String, id: kotlin.String) {
        ResourcePackHeaderDiff.GAME_VERSION_DIFF.put(version, object : net.minecraft.WorldVersion {
        
            override fun dataVersion(): net.minecraft.world.level.storage.DataVersion? {
                return null
            }
            
            override fun id(): kotlin.String {
                return id
            }
            
            override fun name(): kotlin.String {
                return name
            }
            
            override fun protocolVersion(): kotlin.Int {
                return version.getOriginalVersion()
            }
            
            override fun packVersion(type: net.minecraft.server.packs.PackType): net.minecraft.server.packs.metadata.pack.PackFormat {
                if (type == net.minecraft.server.packs.PackType.CLIENT_RESOURCES) {
                    return net.minecraft.server.packs.metadata.pack.PackFormat.of(majorVersion, minorVersion)
                }
                throw java.lang.UnsupportedOperationException()
            }
            
            override fun buildTime(): java.util.Date? {
                return null
            }
            
            override fun stable(): kotlin.Boolean {
                return true
            }
        })
    }
    
}
