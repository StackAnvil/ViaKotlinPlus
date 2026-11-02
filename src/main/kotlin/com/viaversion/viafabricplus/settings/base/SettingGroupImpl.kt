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
package com.viaversion.viafabricplus.settings.base

import com.viaversion.viafabricplus.settings.base.BooleanSettingImpl
import com.viaversion.viafabricplus.settings.base.EnumSettingImpl
import com.viaversion.viaversion.api.protocol.version.ProtocolVersionRange
import com.viaversion.viafabricplus.settings.base.VersionedBooleanSettingImpl

open class SettingGroupImpl (key: kotlin.String) : com.viaversion.viafabricplus.api.settings.base.SettingGroup {

    private val settings: kotlin.collections.MutableList<com.viaversion.viafabricplus.api.settings.base.Setting> = java.util.ArrayList<com.viaversion.viafabricplus.api.settings.base.Setting>()
    private val key: kotlin.String
    private val component: net.minecraft.network.chat.Component
    
    init {
        this.key = key
        this.component = net.minecraft.network.chat.Component.translatable(key + "_settings.viafabricplus")
    }
    
    override fun registerBoolean(key: kotlin.String, defaultValue: kotlin.Boolean): com.viaversion.viafabricplus.api.settings.base.BooleanSetting {
        val setting: com.viaversion.viafabricplus.api.settings.base.BooleanSetting = BooleanSettingImpl(key, this.settingsName(key), defaultValue)
        this.settings.add(setting)
        return setting
    }
    
    override fun <T>registerEnum(key: kotlin.String, defaultValue: T): com.viaversion.viafabricplus.api.settings.base.EnumSetting<T> where T : kotlin.Enum<T>, T : com.viaversion.viafabricplus.api.settings.base.EnumSetting.EnumValue {
        val setting: com.viaversion.viafabricplus.api.settings.base.EnumSetting<T> = EnumSettingImpl<T>(key, this.settingsName(key), defaultValue)
        this.settings.add(setting)
        return setting
    }
    
    override fun registerVersionedBoolean(key: kotlin.String, versionRange: ProtocolVersionRange, defaultValue: kotlin.Boolean): com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting {
        val setting: com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting = VersionedBooleanSettingImpl(key, this.settingsName(key), versionRange, defaultValue)
        this.settings.add(setting)
        return setting
    }
    
    override fun register(key: kotlin.String, setting: com.viaversion.viafabricplus.api.settings.base.Setting): com.viaversion.viafabricplus.api.settings.base.Setting {
        this.settings.add(setting)
        return setting
    }
    
    private fun settingsName(key: kotlin.String): net.minecraft.network.chat.Component {
        return net.minecraft.network.chat.Component.translatable(this.key + "_settings.viafabricplus." + key)
    }
    
    override fun write(`object`: com.google.gson.JsonObject) {
        val settings: com.google.gson.JsonObject = com.google.gson.JsonObject()
        for (setting in this.settings) {
            setting.write(settings)
        }
        `object`.add(this.key, settings)
    }
    
    override fun read(`object`: com.google.gson.JsonObject) {
        val settings: com.google.gson.JsonObject = `object`.getAsJsonObject(this.key)
        if (settings != null) {
            for (setting in this.settings) {
                setting.read(settings)
            }
        }
    }
    
    override fun name(): net.minecraft.network.chat.Component {
        return this.component
    }
    
    override fun settings(): kotlin.collections.MutableList<com.viaversion.viafabricplus.api.settings.base.Setting> {
        return java.util.ArrayList<com.viaversion.viafabricplus.api.settings.base.Setting>(this.settings)
    }
    
}
