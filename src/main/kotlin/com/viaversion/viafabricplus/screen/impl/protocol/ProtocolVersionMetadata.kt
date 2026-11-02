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
package com.viaversion.viafabricplus.screen.impl.protocol

import java.time.LocalDate
import com.viaversion.viafabricplus.screen.impl.protocol.ProtocolVersionMetadata
import java.time.format.DateTimeFormatter
import com.viaversion.viafabricplus.protocoltranslator.impl.ViaFabricPlusMappingDataLoader
import java.util.HashMap

@JvmRecord 
data class ProtocolVersionMetadata (val title: net.minecraft.network.chat.Component, val releaseDate: LocalDate, val icon: net.minecraft.resources.Identifier){

    fun formattedReleaseDate(): net.minecraft.network.chat.Component {
        return net.minecraft.network.chat.Component.nullToEmpty(this.releaseDate.format(ProtocolVersionMetadata.Companion.DATE_FORMAT))
    }
    
    companion object {
        private val UNKNOWN_ICON: net.minecraft.resources.Identifier = net.minecraft.resources.Identifier.withDefaultNamespace("textures/item/barrier.png")
        
        private val BY_VERSION_NAME: kotlin.collections.MutableMap<kotlin.String, ProtocolVersionMetadata> = ProtocolVersionMetadata.Companion.load()
        private val DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(java.time.format.FormatStyle.MEDIUM)
        
        fun of(version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion): ProtocolVersionMetadata {
            return ProtocolVersionMetadata.Companion.BY_VERSION_NAME.get(version.getName())!!
        }
        
        fun icon(version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion): net.minecraft.resources.Identifier {
            return ProtocolVersionMetadata.of(version)?.icon ?: UNKNOWN_ICON
        }
        
        private fun load(): kotlin.collections.MutableMap<kotlin.String, ProtocolVersionMetadata> {
            val data: com.viaversion.viaversion.libs.gson.JsonObject = ViaFabricPlusMappingDataLoader.INSTANCE.loadData("version-metadata.json")!!
            val metadata: kotlin.collections.MutableMap<kotlin.String, ProtocolVersionMetadata> = HashMap<kotlin.String, ProtocolVersionMetadata>(data.size())
            for (versionName in data.keySet()) {
                val entry: com.viaversion.viaversion.libs.gson.JsonObject = data.getAsJsonObject(versionName)
                val title = if (entry.has("key")) net.minecraft.network.chat.Component.translatable("version_title.viafabricplus." + entry.get("key").getAsString()) else null
                // Vanilla textures are blitted directly as item stacks can't be created before a server bound their components
                val icon = net.minecraft.resources.Identifier.withDefaultNamespace("textures/" + entry.get("icon").getAsString() + ".png")
                metadata.put(versionName, ProtocolVersionMetadata(title ?: net.minecraft.network.chat.Component.empty(), LocalDate.parse(entry.get("release_date").getAsString()), icon))
            }
            return metadata
        }
        
    }}
