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
package com.viaversion.viafabricplus.features.v1_12_2

import com.viaversion.viafabricplus.features.v1_12_2.UnicodeFontFix1_12_2
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import com.viaversion.viafabricplus.ViaFabricPlusImpl
import com.viaversion.viafabricplus.features.v1_12_2.LanguageUtil

object UnicodeFontFix1_12_2 {
    private var enabled: kotlin.Boolean = false
    private var task: java.lang.Runnable? = null
    
    fun init() {
        com.viaversion.viafabricplus.ViaFabricPlus.api().protocolTranslation().addChangeProtocolVersionListener(java.util.function.BiConsumer {`_`: com.viaversion.viaversion.api.protocol.version.ProtocolVersion, newVersion: com.viaversion.viaversion.api.protocol.version.ProtocolVersion -> UnicodeFontFix1_12_2.updateUnicodeFontOverride(newVersion)
        })
        
        ClientTickEvents.START_CLIENT_TICK.register(ClientTickEvents.StartTick { _: net.minecraft.client.Minecraft ->
            val currentTask = UnicodeFontFix1_12_2.task
            if (currentTask != null) {
                currentTask.run()
                UnicodeFontFix1_12_2.task = null
            }
        })
    }
    
    fun updateUnicodeFontOverride(version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion) {
        val option: net.minecraft.client.OptionInstance<kotlin.Boolean> = net.minecraft.client.Minecraft.getInstance().options.forceUnicodeFont()
        
        if (ViaFabricPlusImpl.impl().visuals().forceUnicodeFontForNonAsciiLanguages().isActive(version)) {
            val language = net.minecraft.locale.Language.getInstance()
            if (language is net.minecraft.client.resources.language.ClientLanguage) {
                UnicodeFontFix1_12_2.enabled = LanguageUtil.isUnicodeFont1_12_2(language.storage)
                UnicodeFontFix1_12_2.task = java.lang.Runnable { option.set(UnicodeFontFix1_12_2.enabled) }
            }
        } else if (UnicodeFontFix1_12_2.enabled) {
            UnicodeFontFix1_12_2.enabled = false
            UnicodeFontFix1_12_2.task = java.lang.Runnable {option.set(false)}
        }
    }
    
}
