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
package com.viaversion.viafabricplus.api.settings.base

import com.viaversion.viaversion.api.protocol.version.ProtocolVersionRange

 /**
 * A group of settings.
 * 
 * @see Setting
 */
interface SettingGroup {

     /**
 * The name of the group. Used for displaying the group in the settings menu.
 * 
 * @return The name of the group
 */
    fun name(): net.minecraft.network.chat.Component
    
     /**
 * All settings in the group.
 * 
 * @return All settings in the group
 */
    fun settings(): kotlin.collections.MutableList<com.viaversion.viafabricplus.api.settings.base.Setting>
    
     /**
 * Registers a new setting.
 * 
 * @param key          The translation key of the setting. Requires all settings to be prefixed with this key as well.
 * @param defaultValue The default value of the setting.
 * @return The newly created setting
 */
    fun registerBoolean(key: kotlin.String, defaultValue: kotlin.Boolean): com.viaversion.viafabricplus.api.settings.base.BooleanSetting
    
     /**
 * Registers a new enum setting.
 * 
 * @param key          The translation key of the setting. Requires all settings to be prefixed with this key as well.
 * @param defaultValue The default value of the setting.
 * @param <T>          The type of the enum
 * @return The newly created enum setting
</T> */
    fun <T>registerEnum(key: kotlin.String, defaultValue: T): com.viaversion.viafabricplus.api.settings.base.EnumSetting<T> where T : kotlin.Enum<T>, T : com.viaversion.viafabricplus.api.settings.base.EnumSetting.EnumValue
    
     /**
 * Registers a new versioned boolean setting.
 * 
 * @param key          The translation key of the setting. Requires all settings to be prefixed with this key as well.
 * @param versionRange The version range the setting is valid for.
 * @param defaultValue The default value of the setting.
 * @return The newly created versioned boolean setting
 */
    fun registerVersionedBoolean(key: kotlin.String, versionRange: ProtocolVersionRange, defaultValue: kotlin.Boolean): com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting
    
     /**
 * Registers a new setting. This is useful for custom settings that are not part of the default settings.
 * 
 * @param key     The translation key of the setting. Requires all settings to be prefixed with this key as well.
 * @param setting The setting to register
 * @return The registered setting
 */
    fun register(key: kotlin.String, setting: com.viaversion.viafabricplus.api.settings.base.Setting): com.viaversion.viafabricplus.api.settings.base.Setting
    
     /**
 * Writes the settings to the given JsonObject.
 * 
 * @param object The JsonObject to write to
 */
    fun write(`object`: com.google.gson.JsonObject)
    
     /**
 * Reads the settings from the given JsonObject.
 * 
 * @param object The JsonObject to read from
 */
    fun read(`object`: com.google.gson.JsonObject)
    
}
