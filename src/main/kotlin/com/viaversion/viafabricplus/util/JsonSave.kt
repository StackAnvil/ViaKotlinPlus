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

import com.viaversion.viafabricplus.util.JsonSave
import com.viaversion.viafabricplus.ViaFabricPlusImpl
import java.nio.file.StandardOpenOption

object JsonSave {
    val GSON: com.google.gson.Gson = com.google.gson.GsonBuilder().setPrettyPrinting().create()
    
    fun load(path: java.nio.file.Path, reader: java.util.function.Consumer<com.google.gson.JsonObject>, writer: java.util.function.Supplier<com.google.gson.JsonObject?>) {
        JsonSave.read(path, reader)
        java.lang.Runtime.getRuntime().addShutdownHook(java.lang.Thread(java.lang.Runnable {JsonSave.write(path, writer)}))
    }
    
    fun read(path: java.nio.file.Path, consumer: java.util.function.Consumer<com.google.gson.JsonObject>) {
        if (java.nio.file.Files.exists(path)) {
            try {
                java.nio.file.Files.newBufferedReader(path).use {
                reader -> val `object`: com.google.gson.JsonObject = JsonSave.GSON.fromJson<com.google.gson.JsonObject>(reader, com.google.gson.JsonObject::class.java)
                if (`object` != null) {
                    consumer.accept(`object`)
                } else {
                    ViaFabricPlusImpl.impl().logger().error("The file {} is empty!", path.getFileName())
                }
                
                } }catch (e: java.lang.Exception) {
                ViaFabricPlusImpl.impl().logger().error("Failed to read file: {}!", path.getFileName(), e)
            }
        }
    }
    
    fun write(path: java.nio.file.Path, supplier: java.util.function.Supplier<com.google.gson.JsonObject?>) {
        val `object`: com.google.gson.JsonObject? = supplier.get()
        if (`object` != null) {
            try {
                java.nio.file.Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING).use {writer -> JsonSave.GSON.toJson(`object`, writer)
                } }catch (e: java.lang.Exception) {
                ViaFabricPlusImpl.impl().logger().error("Failed to write file: {}!", path.getFileName(), e)
            }
        }
    }
    
}
