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

import com.viaversion.viafabricplus.screen.base.VFPTabbedScreen
import com.viaversion.viafabricplus.screen.impl.protocol.ProtocolCategory
import com.viaversion.viafabricplus.screen.impl.protocol.AbstractProtocolSelectionScreen
import com.viaversion.viafabricplus.screen.base.list.VFPListEntry
import com.viaversion.viafabricplus.settings.SettingsImpl
import com.viaversion.viafabricplus.ViaFabricPlusImpl
import com.viaversion.viafabricplus.screen.impl.protocol.ProtocolSlot
import com.viaversion.viafabricplus.screen.impl.protocol.ProtocolVersionMetadata

abstract class AbstractProtocolSelectionScreen protected constructor(title: net.minecraft.network.chat.Component, backButton: kotlin.Boolean) : VFPTabbedScreen<ProtocolCategory>(title, backButton) {

    abstract fun select(version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion)
    
    abstract fun selected(version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion): kotlin.Boolean
    
    abstract fun selectable(): kotlin.Boolean
    
    override fun tabs(): kotlin.collections.MutableList<ProtocolCategory> {
        return java.util.List.of<ProtocolCategory>(*ProtocolCategory.entries.toTypedArray())
    }
    
    override fun tabTitle(tab: ProtocolCategory): net.minecraft.network.chat.Component {
        return tab.title()
    }
    
    override fun entryHeight(): kotlin.Int {
        return AbstractProtocolSelectionScreen.Companion.SLOT_HEIGHT
    }
    
    override fun rowWidth(screenWidth: kotlin.Int): kotlin.Int {
        return kotlin.math.min(AbstractProtocolSelectionScreen.Companion.ROW_WIDTH, screenWidth - 20)
    }
    
    override fun entries(tab: ProtocolCategory): kotlin.collections.MutableList<VFPListEntry> {
        val entries: kotlin.collections.MutableList<VFPListEntry> = java.util.ArrayList<VFPListEntry>()
        if (tab == ProtocolCategory.FAVORITES) {
            val settings: SettingsImpl = ViaFabricPlusImpl.impl().settings()
            for (version in com.viaversion.viaversion.api.protocol.version.ProtocolVersion.getReversedProtocols()) {
                if (settings.isFavorite(version)) {
                    entries.add(ProtocolSlot(version, this))
                }
            }
            return entries
        }
        
        if (tab == ProtocolCategory.MODERN) {
            // Auto Detect is no Minecraft version, it is pinned to the modern tab as it is not limited to a single category
            entries.add(ProtocolSlot(com.viaversion.viafabricplus.api.protocoltranslator.ProtocolTranslation.AUTO_DETECT_VERSION, this))
        }
        
        for (version in com.viaversion.viaversion.api.protocol.version.ProtocolVersion.getReversedProtocols()) {
            if (version !== com.viaversion.viafabricplus.api.protocoltranslator.ProtocolTranslation.AUTO_DETECT_VERSION && ProtocolCategory.of(version) == tab) {
                entries.add(ProtocolSlot(version, this))
            }
        }
        return entries
    }
    
    override fun results(query: kotlin.String): kotlin.collections.MutableList<VFPListEntry> {
        return com.viaversion.viaversion.api.protocol.version.ProtocolVersion.getReversedProtocols().stream()
        .filter {version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion -> AbstractProtocolSelectionScreen.Companion.matches(version, query)} 
        .map<VFPListEntry> {version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion -> ProtocolSlot(version, this)} 
        .toList()
    }
    
    override fun initialTab(): ProtocolCategory {
        // Open the tab holding the selected version so it doesn't have to be searched for
        for (version in com.viaversion.viaversion.api.protocol.version.ProtocolVersion.getReversedProtocols()) {
            if (this.selected(version)) {
                if (ViaFabricPlusImpl.impl().settings().isFavorite(version)) {
                    return ProtocolCategory.FAVORITES
                }
                
                                // Auto Detect is no Minecraft version and therefore has no category of its own
                return if (version === com.viaversion.viafabricplus.api.protocoltranslator.ProtocolTranslation.AUTO_DETECT_VERSION) ProtocolCategory.MODERN else ProtocolCategory.of(version)
            }
        }
        
        return ProtocolCategory.MODERN
    }
    
    fun toggleFavorite(version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion) {
        ViaFabricPlusImpl.impl().settings().toggleFavorite(version)
        this.refreshTab(ProtocolCategory.FAVORITES)
    }
    
    companion object {
        private const val SLOT_HEIGHT: kotlin.Int = 32
        private const val ROW_WIDTH: kotlin.Int = 260
        
        private fun matches(version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion, query: kotlin.String): kotlin.Boolean {
            if (version.getName().lowercase().contains(query)) {
                return true
            }
                    // Ranges like 1.7.6-1.7.10 are matched by every version they include as well
            for (includedVersion in version.getIncludedVersions()) {
                if (includedVersion.lowercase().contains(query)) {
                    return true
                }
            }
            
            val metadata: ProtocolVersionMetadata = ProtocolVersionMetadata.of(version)
            return metadata != null && metadata.title != null && metadata.title.getString().lowercase().contains(query)
        }
        
    }}
