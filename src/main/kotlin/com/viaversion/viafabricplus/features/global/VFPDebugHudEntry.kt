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

import com.viaversion.viaversion.api.connection.UserConnection
import net.raphimc.vialegacy.protocol.release.r1_1tor1_2_1_3.storage.SeedStorage
import net.raphimc.vialegacy.api.LegacyProtocolVersion
import net.raphimc.vialegacy.protocol.classic.c0_30cpetoc0_28_30.storage.ExtensionProtocolMetadataStorage
import com.viaversion.viafabricplus.features.global.VFPDebugHudEntry

class VFPDebugHudEntry : net.minecraft.client.gui.components.debug.DebugScreenEntry {

    override fun display(lines: net.minecraft.client.gui.components.debug.DebugScreenDisplayer, world: net.minecraft.world.level.Level, clientChunk: net.minecraft.world.level.chunk.LevelChunk, chunk: net.minecraft.world.level.chunk.LevelChunk) {
        val information: kotlin.collections.MutableList<kotlin.String> = java.util.ArrayList<kotlin.String>()
        information.add(com.viaversion.viafabricplus.util.ChatUtil.PREFIX + net.minecraft.ChatFormatting.RESET + ' ' + com.viaversion.viafabricplus.ViaFabricPlus.api().version())
        
        val connection: UserConnection? = com.viaversion.viafabricplus.ViaFabricPlus.api().userConnection()
        if (connection == null) {
            lines.addLine(information.first())
            return 
        }
        
        val info: com.viaversion.viaversion.api.connection.ProtocolInfo = connection.getProtocolInfo()
        information.add("P: " + info.getPipeline().pipes().size + " C: " + info.protocolVersion() + " S: " + info.serverProtocolVersion())
        val entityTracker1_7_10: net.raphimc.vialegacy.protocol.release.r1_7_6_10tor1_8.storage.EntityTracker = connection.get<net.raphimc.vialegacy.protocol.release.r1_7_6_10tor1_8.storage.EntityTracker>(net.raphimc.vialegacy.protocol.release.r1_7_6_10tor1_8.storage.EntityTracker::class.java)!!
        if (entityTracker1_7_10 != null) {
            information.add("1.7 Entities: " + entityTracker1_7_10.getTrackedEntities().size + ", Virtual holograms: " + entityTracker1_7_10.getVirtualHolograms().size)
        }
        val seedStorage: SeedStorage = connection.get<SeedStorage>(SeedStorage::class.java)!!
        if (seedStorage != null && connection.getProtocolInfo().serverProtocolVersion().newerThanOrEqualTo(LegacyProtocolVersion.a1_2_0toa1_2_1_1)) {
            information.add("World Seed: " + seedStorage.getSeed())
        }
        val extensionProtocolMetadataStorage: ExtensionProtocolMetadataStorage = connection.get<ExtensionProtocolMetadataStorage>(ExtensionProtocolMetadataStorage::class.java)!!
        if (extensionProtocolMetadataStorage != null) {
            information.add("CPE extensions: " + extensionProtocolMetadataStorage.getExtensionCount())
        }
        
        lines.addToGroup(VFPDebugHudEntry.Companion.ID, information)
    }
    
    override fun isAllowed(reducedDebugInfo: kotlin.Boolean): kotlin.Boolean {
        return true
    }
    
    companion object {
        val ID: net.minecraft.resources.Identifier = net.minecraft.resources.Identifier.fromNamespaceAndPath("viafabricplus", "viafabricplus")
        
    }}
