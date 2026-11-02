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
import net.raphimc.vialegacy.api.LegacyProtocolVersion

class VisualSettingsImpl : SettingGroupImpl("visual"), com.viaversion.viafabricplus.api.settings.impl.VisualSettings {

    private val hideDownloadTerrainScreenTransitionEffects: com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting = registerVersionedBoolean("hide_download_terrain_screen_transition_effects", ProtocolVersionRange.andOlder(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20_5), false)
    private val forceUnicodeFontForNonAsciiLanguages: com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting = registerVersionedBoolean("force_unicode_font_for_non_ascii_languages", ProtocolVersionRange.andOlder(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2), false)
    private val sneakInstantly: com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting = registerVersionedBoolean("sneak_instantly", ProtocolVersionRange.andOlder(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2), true)
    private val enableLegacyTablist: com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting = registerVersionedBoolean("enable_legacy_tablist", ProtocolVersionRange.andOlder(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_7_6), false)
    private val replaceHurtSoundWithOOFSound: com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting = registerVersionedBoolean("replace_hurt_sound_with_oof_sound", ProtocolVersionRange.andOlder(LegacyProtocolVersion.b1_8tob1_8_1), true)
    private val hideModernHUDElements: com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting = registerVersionedBoolean("hide_modern_hud_elements", ProtocolVersionRange.andOlder(LegacyProtocolVersion.b1_7tob1_7_3), false)
    private val replaceCreativeInventory: com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting = registerVersionedBoolean("replace_creative_inventory_with_classic_inventory", ProtocolVersionRange.andOlder(LegacyProtocolVersion.c0_28toc0_30), true)
    private val oldWalkingAnimation: com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting = registerVersionedBoolean("old_walking_animation", ProtocolVersionRange.andOlder(LegacyProtocolVersion.c0_28toc0_30), true)
    private val legacyPaneOutlines: com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting = registerVersionedBoolean("legacy_pane_outlines", ProtocolVersionRange.andOlder(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2), true)
    private val legacyCropOutlines: com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting = registerVersionedBoolean("legacy_crop_outlines", ProtocolVersionRange.andOlder(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8), true)
    private val disableServerPinging: com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting = registerVersionedBoolean("disable_server_pinging", ProtocolVersionRange.andOlder(LegacyProtocolVersion.b1_7tob1_7_3), true)
    private val allowItemUsageWhileMining: com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting = registerVersionedBoolean("allow_item_usage_while_mining", ProtocolVersionRange.andOlder(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8), false)
    
    override fun hideDownloadTerrainScreenTransitionEffects(): com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting {
        return this.hideDownloadTerrainScreenTransitionEffects
    }
    
    override fun forceUnicodeFontForNonAsciiLanguages(): com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting {
        return this.forceUnicodeFontForNonAsciiLanguages
    }
    
    override fun sneakInstantly(): com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting {
        return this.sneakInstantly
    }
    
    override fun enableLegacyTablist(): com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting {
        return this.enableLegacyTablist
    }
    
    override fun replaceHurtSoundWithOOFSound(): com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting {
        return this.replaceHurtSoundWithOOFSound
    }
    
    override fun hideModernHUDElements(): com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting {
        return this.hideModernHUDElements
    }
    
    override fun replaceCreativeInventory(): com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting {
        return this.replaceCreativeInventory
    }
    
    override fun oldWalkingAnimation(): com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting {
        return this.oldWalkingAnimation
    }
    
    override fun legacyPaneOutlines(): com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting {
        return this.legacyPaneOutlines
    }
    
    override fun legacyCropOutlines(): com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting {
        return this.legacyCropOutlines
    }
    
    override fun disableServerPinging(): com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting {
        return this.disableServerPinging
    }
    
    override fun allowItemUsageWhileMining(): com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting {
        return this.allowItemUsageWhileMining
    }
    
}
