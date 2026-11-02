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

import com.viaversion.viaversion.api.protocol.version.ProtocolVersionRange
import com.viaversion.viafabricplus.settings.base.BooleanSettingImpl
import com.viaversion.viafabricplus.ViaFabricPlusImpl

class VersionedBooleanSettingImpl (key: kotlin.String, component: net.minecraft.network.chat.Component, versionRange: ProtocolVersionRange, defaultValue: kotlin.Boolean) : BooleanSettingImpl(key, component, defaultValue), com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting {

    private val versionRange: ProtocolVersionRange
    
    init {
        this.versionRange = versionRange
    }
    
    override fun versionRange(): ProtocolVersionRange {
        return this.versionRange
    }
    
    override fun value(): kotlin.Boolean {
        return super.isActive()
    }
    
    override fun write(`object`: com.google.gson.JsonObject) {
        `object`.addProperty(this.key(), this.value())
    }
    
    override fun isActive(): kotlin.Boolean {
        return this.versionRange.contains(ViaFabricPlusImpl.impl().targetVersion()) && super.isActive()
    }
    
    override fun isActive(version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion): kotlin.Boolean {
        return this.versionRange.contains(version) && super.isActive()
    }
    
}
