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
package com.viaversion.viafabricplus.settings.impl

import com.viaversion.viafabricplus.settings.base.SettingGroupImpl
import com.viaversion.viaversion.api.protocol.version.ProtocolVersionRange

class AdvancedSettingsImpl : SettingGroupImpl("advanced"), com.viaversion.viafabricplus.api.settings.impl.AdvancedSettings {

    private val queueConfigPackets: com.viaversion.viafabricplus.api.settings.base.BooleanSetting = registerBoolean("queue_config_packets", true)
    private val printNetworkingErrorsToLogs: com.viaversion.viafabricplus.api.settings.base.BooleanSetting = registerBoolean("print_networking_errors_to_logs", true)
    private val ignoreFabricSyncErrors: com.viaversion.viafabricplus.api.settings.base.BooleanSetting = registerBoolean("ignore_fabric_sync_errors", false)
    private val filterNonExistingGlyphs: com.viaversion.viafabricplus.api.settings.base.BooleanSetting = registerBoolean("filter_non_existing_glyphs", true)
    private val removeServerDescriptionSanitize: com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting = registerVersionedBoolean("remove_server_description_sanitize", ProtocolVersionRange.andOlder(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_11), true)
    private val dontCreatePacketErrorCrashReports: com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting = registerVersionedBoolean("dont_create_packet_error_crash_reports", ProtocolVersionRange.andOlder(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20_3), true)
    private val disableSequencing: com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting = registerVersionedBoolean("disable_sequencing", ProtocolVersionRange.andOlder(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_18_2), true)
    
    override fun queueConfigPackets(): com.viaversion.viafabricplus.api.settings.base.BooleanSetting {
        return this.queueConfigPackets
    }
    
    override fun printNetworkingErrorsToLogs(): com.viaversion.viafabricplus.api.settings.base.BooleanSetting {
        return this.printNetworkingErrorsToLogs
    }
    
    override fun ignoreFabricSyncErrors(): com.viaversion.viafabricplus.api.settings.base.BooleanSetting {
        return this.ignoreFabricSyncErrors
    }
    
    override fun filterNonExistingGlyphs(): com.viaversion.viafabricplus.api.settings.base.BooleanSetting {
        return this.filterNonExistingGlyphs
    }
    
    override fun removeServerDescriptionSanitize(): com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting {
        return this.removeServerDescriptionSanitize
    }
    
    override fun dontCreatePacketErrorCrashReports(): com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting {
        return this.dontCreatePacketErrorCrashReports
    }
    
    override fun disableSequencing(): com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting {
        return this.disableSequencing
    }
    
}
