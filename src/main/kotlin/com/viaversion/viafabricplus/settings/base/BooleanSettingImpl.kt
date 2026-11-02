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

open class BooleanSettingImpl (key: kotlin.String, component: net.minecraft.network.chat.Component, defaultValue: kotlin.Boolean) : SettingImpl(key, component), com.viaversion.viafabricplus.api.settings.base.BooleanSetting {

    private val defaultValue: kotlin.Boolean
    private var value: kotlin.Boolean = defaultValue
    
    init {
        this.defaultValue = defaultValue
        this.value = defaultValue
    }
    
    override fun isActive(): kotlin.Boolean {
        return this.value
    }
    
    override fun setActive(value: kotlin.Boolean) {
        this.value = value
    }
    
    override fun defaultValue(): kotlin.Boolean {
        return this.defaultValue
    }
    
    override fun write(`object`: com.google.gson.JsonObject) {
        `object`.addProperty(this.key(), this.isActive())
    }
    
    override fun read(`object`: com.google.gson.JsonObject) {
        val value: com.google.gson.JsonElement = this.value(`object`)
        if (value != null) {
            this.setActive(value.getAsBoolean())
        }
    }
    
}
