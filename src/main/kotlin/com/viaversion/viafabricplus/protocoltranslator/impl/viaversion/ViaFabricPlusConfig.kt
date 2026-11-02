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
package com.viaversion.viafabricplus.protocoltranslator.impl.viaversion

import com.viaversion.viaversion.configuration.AbstractViaConfig
import com.viaversion.viaversion.util.ConfigSection

class ViaFabricPlusConfig (configFile: java.io.File, logger: java.util.logging.Logger) : AbstractViaConfig(configFile, logger) {

    override fun updateConfig(): kotlin.Boolean {
        var modified: kotlin.Boolean = false
        
        val existingConfig: ConfigSection = originalRootSection()!!
        if (existingConfig == null) {
            // Defaults since 2.9.9, the directory was different back then so when upgrading the file won't be present anyway
            set("fix-infested-block-breaking", false)
            set("shield-blocking", false)
            set("no-delay-shield-blocking", true)
            set("handle-invalid-item-count", true)
            set("chunk-border-fix", true)
            modified = true
        }
        
                // 4.4.3 additions
        if (modified || !existingConfig.contains("send-player-details")) {
            set("send-player-details", false)
            modified = true
        }
        
                // 5.1.3 additions
        if (modified || !existingConfig.contains("backup-nested-items")) {
            set("backup-nested-items", true)
            modified = true
        }
        return super.updateConfig() || modified
    }
    
    override fun getUnsupportedOptions(): kotlin.collections.MutableList<kotlin.String> {
        val unsupported: kotlin.collections.MutableList<kotlin.String> = super.getUnsupportedOptions()
        unsupported.add("simulate-pt")
        unsupported.add("fix-1_21-placement-rotation")
        unsupported.add("team-colour-fix")
        unsupported.add("cancel-swing-in-inventory")
        unsupported.add("use-1_8-hitbox-margin")
        return unsupported
    }
    
    override fun isCheckForUpdates(): kotlin.Boolean {
        return false
    }
    
    override fun isSimulatePlayerTick(): kotlin.Boolean {
        return false
    }
    
    override fun fix1_21PlacementRotation(): kotlin.Boolean {
        return false
    }
    
    override fun is1_13TeamColourFix(): kotlin.Boolean {
        return false
    }
    
    override fun cancelSwingInInventory(): kotlin.Boolean {
        return false
    }
    
    override fun cancelBlockSounds(): kotlin.Boolean {
        return false
    }
    
    override fun use1_8HitboxMargin(): kotlin.Boolean {
        return false
    }
    
}
