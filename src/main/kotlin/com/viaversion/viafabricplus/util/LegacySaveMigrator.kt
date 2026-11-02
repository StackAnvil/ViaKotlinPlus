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
package com.viaversion.viafabricplus.util

import com.viaversion.viafabricplus.util.LegacySaveMigrator
import com.viaversion.viafabricplus.util.JsonSave
import java.io.IOException
import com.viaversion.viafabricplus.ViaFabricPlusImpl

object LegacySaveMigrator {
    private const val VERSION_KEY: kotlin.String = "selected_protocol_version"
    private const val LEGACY_VERSION_KEY: kotlin.String = "selected-protocol-version"
    
    private val LEGACY_GROUPS: kotlin.collections.MutableSet<kotlin.String> = kotlin.collections.mutableSetOf<kotlin.String>("authentication", "debug")
    private val RENAMED_SETTINGS: kotlin.collections.MutableMap<kotlin.String, kotlin.String> = java.util.Map.of<kotlin.String, kotlin.String>("beta_craft_authentication", "use_beta_craft_authentication")
    
    private const val DISABLED: kotlin.String = "disabled"
    
    private const val LEGACY_ACCOUNTS_FILE: kotlin.String = "accounts.json"
    private const val CLASSICUBE_FILE: kotlin.String = "classicube.json"
    private const val CLASSICUBE_KEY: kotlin.String = "classicube"
    
    fun isLegacySettings(`object`: com.google.gson.JsonObject): kotlin.Boolean {
        return `object`.has(LegacySaveMigrator.LEGACY_VERSION_KEY) || LegacySaveMigrator.LEGACY_GROUPS.stream().anyMatch {memberName: kotlin.String -> `object`.has(memberName)} 
    }
    
    fun migrateSettings(legacy: com.google.gson.JsonObject, current: com.google.gson.JsonObject): com.google.gson.JsonObject {
        for (group in current.entrySet()) {
            if (!group.value.isJsonObject()) { // The selected version is no group
                continue 
            }
            
            val settings: com.google.gson.JsonObject = group.value.getAsJsonObject()
            for (key in java.util.List.copyOf<kotlin.String>(settings.keySet())) {
                val value: com.google.gson.JsonPrimitive = LegacySaveMigrator.legacyValue(legacy, LegacySaveMigrator.RENAMED_SETTINGS.getOrDefault(key, key))!!
                if (value != null) {
                    settings.add(key, LegacySaveMigrator.migrateValue(settings.get(key).getAsJsonPrimitive(), value))
                }
            }
        }
        
        if (legacy.has(LegacySaveMigrator.LEGACY_VERSION_KEY)) {
            current.add(LegacySaveMigrator.VERSION_KEY, legacy.get(LegacySaveMigrator.LEGACY_VERSION_KEY))
        }
        return current
    }
    
    fun migrateClassiCubeAccount(directory: java.nio.file.Path) {
        val legacyPath: java.nio.file.Path = directory.resolve(LegacySaveMigrator.LEGACY_ACCOUNTS_FILE)
        val path: java.nio.file.Path = directory.resolve(LegacySaveMigrator.CLASSICUBE_FILE)
        if (!java.nio.file.Files.exists(legacyPath) || java.nio.file.Files.exists(path)) {
            return 
        }
        
        JsonSave.read(legacyPath, java.util.function.Consumer {
        legacy: com.google.gson.JsonObject -> if (legacy.has(LegacySaveMigrator.CLASSICUBE_KEY) && legacy.get(LegacySaveMigrator.CLASSICUBE_KEY).isJsonObject()) {
            JsonSave.write(path, java.util.function.Supplier {legacy.getAsJsonObject(LegacySaveMigrator.CLASSICUBE_KEY)})
        }
        
                    // Dropping the old save keeps a later logout from being undone by migrating it a second time
        try {
            java.nio.file.Files.delete(legacyPath)
        }catch (e: IOException) {
            ViaFabricPlusImpl.impl().logger().error("Failed to delete the migrated {}!", LegacySaveMigrator.LEGACY_ACCOUNTS_FILE, e)
        }
        
        })
    }
    
     // The old groups don't line up with the current ones, so a setting is searched for in all of them
    private fun legacyValue(legacy: com.google.gson.JsonObject, key: kotlin.String): com.google.gson.JsonPrimitive? {
        for (group in legacy.entrySet()) {
            if (!group.value.isJsonObject()) {
                continue 
            }
            
            val value: com.google.gson.JsonElement = group.value.getAsJsonObject().get(key)
            if (value != null && value.isJsonPrimitive()) {
                return value.getAsJsonPrimitive()
            }
        }
        return null
    }
    
     // The current value tells the type apart, enums are the only settings which aren't stored as a boolean
    private fun migrateValue(current: com.google.gson.JsonPrimitive, legacy: com.google.gson.JsonPrimitive): com.google.gson.JsonPrimitive {
        if (!current.isBoolean()) {
            return com.google.gson.JsonPrimitive(legacy.getAsString().uppercase())
        }
        
                // Only the tri-state booleans were written as a string, "auto" and "enabled" both become on
        return if (legacy.isBoolean()) legacy else com.google.gson.JsonPrimitive(LegacySaveMigrator.DISABLED != legacy.getAsString())
    }
    
}
