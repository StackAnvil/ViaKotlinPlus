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

import com.viaversion.viafabricplus.screen.impl.protocol.ProtocolCategory
import net.raphimc.vialegacy.api.LegacyProtocolVersion
import com.viaversion.viaversion.api.protocol.version.VersionType

enum class ProtocolCategory {

    FAVORITES,  // Filled from the settings instead of by #of
    MODERN, 
    LEGACY, 
    BETA, 
    ALPHA, 
    CLASSIC, 
    APRIL_FOOLS;
    
    private val title: net.minecraft.network.chat.Component
    
    init {
        this.title = net.minecraft.network.chat.Component.translatable("protocol_selection.viafabricplus." + name.lowercase())
    }
    
    fun title(): net.minecraft.network.chat.Component {
        return this.title
    }
    
    companion object {
        fun of(version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion): ProtocolCategory {
        // c0.30 CPE is registered as a special version and therefore doesn't report the classic version type
            if (version == LegacyProtocolVersion.c0_30cpe) {
                return ProtocolCategory.CLASSIC
            }
            
            return when (version.getVersionType()){VersionType.CLASSIC -> ProtocolCategory.CLASSIC
                VersionType.ALPHA_INITIAL, VersionType.ALPHA_LATER -> ProtocolCategory.ALPHA
                VersionType.BETA_INITIAL, VersionType.BETA_LATER -> ProtocolCategory.BETA
                VersionType.RELEASE_INITIAL -> ProtocolCategory.LEGACY
                VersionType.RELEASE -> if (version.olderThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_16)) ProtocolCategory.LEGACY else ProtocolCategory.MODERN
                VersionType.SPECIAL -> ProtocolCategory.APRIL_FOOLS}
        }
        
    }}
