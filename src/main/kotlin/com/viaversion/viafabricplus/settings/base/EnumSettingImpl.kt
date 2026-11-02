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

import com.viaversion.viafabricplus.settings.base.SettingImpl

class EnumSettingImpl<T> (key: kotlin.String, component: net.minecraft.network.chat.Component, defaultValue: T) : SettingImpl(key, component), com.viaversion.viafabricplus.api.settings.base.EnumSetting<T> where T : kotlin.Enum<T>, T : com.viaversion.viafabricplus.api.settings.base.EnumSetting.EnumValue {

    private val defaultValue: T
    private lateinit var value: T
    
    init {
        this.defaultValue = defaultValue
        this.value = defaultValue
    }
    
    override fun value(): T {
        return this.value
    }
    
    override fun setValue(value: T) {
        this.value = value
    }
    
    override fun defaultValue(): T {
        return this.defaultValue
    }
    
    override fun values(): kotlin.Array<T> {
        return (this.defaultValue() as kotlin.Enum<*>).javaClass.enumConstants as kotlin.Array<T>
    }
    
    override fun write(`object`: com.google.gson.JsonObject) {
        `object`.addProperty(this.key(), this.value().name)
    }
    
    override fun read(`object`: com.google.gson.JsonObject) {
        val value: com.google.gson.JsonElement = this.value(`object`)
        if (value == null) {
            return 
        }
        
        for (constant in values()) {
            if (constant.name == value.getAsString()) {
                this.setValue(constant)
                return 
            }
        }
    }
    
}
