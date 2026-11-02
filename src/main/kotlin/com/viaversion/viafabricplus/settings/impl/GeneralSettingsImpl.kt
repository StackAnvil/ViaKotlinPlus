 /*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2019-2026 the original authors
 *                         - Florian Reuth <git@florianreuth.de>
 *                         - Raphael Koppensteiner
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

class GeneralSettingsImpl : SettingGroupImpl("general"), com.viaversion.viafabricplus.api.settings.impl.GeneralSettings {

    private val multiplayerScreenButtonOrientation: com.viaversion.viafabricplus.api.settings.base.EnumSetting<com.viaversion.viafabricplus.api.settings.impl.Orientation> = registerEnum<com.viaversion.viafabricplus.api.settings.impl.Orientation>("multiplayer_screen_button_orientation", com.viaversion.viafabricplus.api.settings.impl.Orientation.RIGHT_TOP)
    private val addServerScreenButtonOrientation: com.viaversion.viafabricplus.api.settings.base.EnumSetting<com.viaversion.viafabricplus.api.settings.impl.Orientation> = registerEnum<com.viaversion.viafabricplus.api.settings.impl.Orientation>("add_server_screen_button_orientation", com.viaversion.viafabricplus.api.settings.impl.Orientation.RIGHT_TOP)
    private val directConnectScreenButtonOrientation: com.viaversion.viafabricplus.api.settings.base.EnumSetting<com.viaversion.viafabricplus.api.settings.impl.Orientation> = registerEnum<com.viaversion.viafabricplus.api.settings.impl.Orientation>("direct_connect_screen_button_orientation", com.viaversion.viafabricplus.api.settings.impl.Orientation.RIGHT_TOP)
    private val removeNotAvailableItemsFromCreativeTab: com.viaversion.viafabricplus.api.settings.base.EnumSetting<com.viaversion.viafabricplus.api.settings.impl.ItemFilter> = registerEnum<com.viaversion.viafabricplus.api.settings.impl.ItemFilter>("filter_creative_tabs", com.viaversion.viafabricplus.api.settings.impl.ItemFilter.VANILLA_AND_MODDED)
    private val saveSelectedProtocolVersion: com.viaversion.viafabricplus.api.settings.base.BooleanSetting = registerBoolean("save_selected_protocol_version", true)
    private val showAdvertisedServerVersion: com.viaversion.viafabricplus.api.settings.base.BooleanSetting = registerBoolean("show_advertised_server_version", true)
    private val ignorePacketTranslationErrors: com.viaversion.viafabricplus.api.settings.base.EnumSetting<com.viaversion.viafabricplus.api.settings.impl.PacketTranslationError> = registerEnum<com.viaversion.viafabricplus.api.settings.impl.PacketTranslationError>("ignore_packet_translation_errors", com.viaversion.viafabricplus.api.settings.impl.PacketTranslationError.KICK)
    private val loadSkinsAndSkullsInLegacyVersions: com.viaversion.viafabricplus.api.settings.base.BooleanSetting = registerBoolean("load_skins_and_skulls_in_legacy_versions", true)
    private val betaCraftAuthentication: com.viaversion.viafabricplus.api.settings.base.BooleanSetting = registerBoolean("beta_craft_authentication", true)
    
    override fun multiplayerScreenButtonOrientation(): com.viaversion.viafabricplus.api.settings.base.EnumSetting<com.viaversion.viafabricplus.api.settings.impl.Orientation> {
        return this.multiplayerScreenButtonOrientation
    }
    
    override fun addServerScreenButtonOrientation(): com.viaversion.viafabricplus.api.settings.base.EnumSetting<com.viaversion.viafabricplus.api.settings.impl.Orientation> {
        return this.addServerScreenButtonOrientation
    }
    
    override fun directConnectScreenButtonOrientation(): com.viaversion.viafabricplus.api.settings.base.EnumSetting<com.viaversion.viafabricplus.api.settings.impl.Orientation> {
        return this.directConnectScreenButtonOrientation
    }
    
    override fun removeNotAvailableItemsFromCreativeTab(): com.viaversion.viafabricplus.api.settings.base.EnumSetting<com.viaversion.viafabricplus.api.settings.impl.ItemFilter> {
        return this.removeNotAvailableItemsFromCreativeTab
    }
    
    override fun saveSelectedProtocolVersion(): com.viaversion.viafabricplus.api.settings.base.BooleanSetting {
        return this.saveSelectedProtocolVersion
    }
    
    override fun showAdvertisedServerVersion(): com.viaversion.viafabricplus.api.settings.base.BooleanSetting {
        return this.showAdvertisedServerVersion
    }
    
    override fun ignorePacketTranslationErrors(): com.viaversion.viafabricplus.api.settings.base.EnumSetting<com.viaversion.viafabricplus.api.settings.impl.PacketTranslationError> {
        return this.ignorePacketTranslationErrors
    }
    
    override fun loadSkinsAndSkullsInLegacyVersions(): com.viaversion.viafabricplus.api.settings.base.BooleanSetting {
        return this.loadSkinsAndSkullsInLegacyVersions
    }
    
    override fun betaCraftAuthentication(): com.viaversion.viafabricplus.api.settings.base.BooleanSetting {
        return this.betaCraftAuthentication
    }
    
}
