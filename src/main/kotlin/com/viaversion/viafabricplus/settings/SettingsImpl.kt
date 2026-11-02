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
package com.viaversion.viafabricplus.settings

import com.viaversion.viafabricplus.settings.impl.GeneralSettingsImpl
import com.viaversion.viafabricplus.settings.impl.VisualSettingsImpl
import com.viaversion.viafabricplus.settings.impl.AdvancedSettingsImpl
import java.util.LinkedHashSet
import com.viaversion.viafabricplus.util.JsonSave
import com.viaversion.viafabricplus.util.LegacySaveMigrator
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslationImpl
import com.viaversion.viafabricplus.settings.base.SettingGroupImpl

class SettingsImpl : com.viaversion.viafabricplus.api.settings.Settings {

    private val groups: kotlin.collections.MutableList<com.viaversion.viafabricplus.api.settings.base.SettingGroup> = java.util.ArrayList<com.viaversion.viafabricplus.api.settings.base.SettingGroup>()
    
    private val general: GeneralSettingsImpl = GeneralSettingsImpl()
    private val visual: VisualSettingsImpl = VisualSettingsImpl()
    private val advanced: AdvancedSettingsImpl = AdvancedSettingsImpl()
    
    private var selectedProtocolVersion: kotlin.String? = null
    private val favoriteProtocolVersions: kotlin.collections.MutableSet<kotlin.String> = LinkedHashSet<kotlin.String>()
    
    init {
        this.groups.add(this.general)
        this.groups.add(this.visual)
        this.groups.add(this.advanced)
    }
    
    fun init() {
        com.google.common.base.Preconditions.checkState(this.selectedProtocolVersion == null, "Settings already initialized!")
        net.fabricmc.loader.api.FabricLoader.getInstance().invokeEntrypoints<com.viaversion.viafabricplus.api.entrypoint.ViaFabricPlusEntrypoint>("viafabricplus", com.viaversion.viafabricplus.api.entrypoint.ViaFabricPlusEntrypoint::class.java, java.util.function.Consumer {obj: com.viaversion.viafabricplus.api.entrypoint.ViaFabricPlusEntrypoint -> obj.onPreSettingsLoading()})
        
        JsonSave.load(com.viaversion.viafabricplus.ViaFabricPlus.api().path().resolve("settings.json"), java.util.function.Consumer {
        jsonObject: com.google.gson.JsonObject -> 
            // Migrating into the current defaults leaves settings the old format didn't know at their default
        val settings: com.google.gson.JsonObject = if (LegacySaveMigrator.isLegacySettings(jsonObject)) 
        LegacySaveMigrator.migrateSettings(jsonObject, this.snapshot())
        else 
        jsonObject
        
        for (group in this.groups) {
            group.read(settings)
        }
        this.selectedProtocolVersion = settings.get("selected_protocol_version").getAsString()
        if (settings.has("favorite_protocol_versions")) {
            val savedNativeVersion: kotlin.String = if (settings.has("native_protocol_version")) settings.get("native_protocol_version").getAsString() else null!!
            for (element in settings.getAsJsonArray("favorite_protocol_versions")) {
                val version: kotlin.String = element.getAsString()
                                    // A favorited native version is meant as the latest one, so it follows the native version across updates
                this.favoriteProtocolVersions.add(if (version == savedNativeVersion) ProtocolTranslationImpl.NATIVE_VERSION.getName() else version)
            }
        }
        
        }, java.util.function.Supplier {this.snapshot()})
        
        net.fabricmc.loader.api.FabricLoader.getInstance().invokeEntrypoints<com.viaversion.viafabricplus.api.entrypoint.ViaFabricPlusEntrypoint>("viafabricplus", com.viaversion.viafabricplus.api.entrypoint.ViaFabricPlusEntrypoint::class.java, java.util.function.Consumer {obj: com.viaversion.viafabricplus.api.entrypoint.ViaFabricPlusEntrypoint -> obj.onPostSettingsLoading()})
    }
    
    private fun snapshot(): com.google.gson.JsonObject {
        val `object`: com.google.gson.JsonObject = com.google.gson.JsonObject()
        for (group in this.groups) {
            group.write(`object`)
        }
        `object`.addProperty("selected_protocol_version", com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().getName())
        
        val favorites: com.google.gson.JsonArray = com.google.gson.JsonArray()
        this.favoriteProtocolVersions.forEach(java.util.function.Consumer {string: kotlin.String -> favorites.add(string)})
        `object`.add("favorite_protocol_versions", favorites)
        `object`.addProperty("native_protocol_version", ProtocolTranslationImpl.NATIVE_VERSION.getName())
        return `object`
    }
    
    fun postInit() {
        // Set target version AFTER protocol loading, so we can reach all versions
        if (this.selectedProtocolVersion != null) {
            if (this.general.saveSelectedProtocolVersion().isActive()) {
                val protocolVersion: com.viaversion.viaversion.api.protocol.version.ProtocolVersion = com.viaversion.viaversion.api.protocol.version.ProtocolVersion.getClosest(this.selectedProtocolVersion)!!
                if (protocolVersion != null) {
                    com.viaversion.viafabricplus.ViaFabricPlus.api().setTargetVersion(protocolVersion)
                }
            } else {
                com.viaversion.viafabricplus.ViaFabricPlus.api().setTargetVersion(ProtocolTranslationImpl.NATIVE_VERSION)
            }
        }
    }
    
    fun isFavorite(version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion): kotlin.Boolean {
        return this.favoriteProtocolVersions.contains(version.getName())
    }
    
    fun toggleFavorite(version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion) {
        if (!this.favoriteProtocolVersions.remove(version.getName())) {
            this.favoriteProtocolVersions.add(version.getName())
        }
    }
    
    override fun general(): GeneralSettingsImpl {
        return this.general
    }
    
    override fun visual(): VisualSettingsImpl {
        return this.visual
    }
    
    override fun advanced(): AdvancedSettingsImpl {
        return this.advanced
    }
    
    override fun register(key: kotlin.String): com.viaversion.viafabricplus.api.settings.base.SettingGroup {
        val group: com.viaversion.viafabricplus.api.settings.base.SettingGroup = SettingGroupImpl(key)
        this.groups.add(group)
        return group
    }
    
    override fun groups(): kotlin.collections.MutableList<com.viaversion.viafabricplus.api.settings.base.SettingGroup> {
        return java.util.ArrayList<com.viaversion.viafabricplus.api.settings.base.SettingGroup>(this.groups)
    }
    
}
