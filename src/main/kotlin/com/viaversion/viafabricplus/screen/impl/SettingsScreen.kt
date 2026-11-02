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
package com.viaversion.viafabricplus.screen.impl

import com.viaversion.viafabricplus.screen.base.VFPTabbedScreen
import com.viaversion.viafabricplus.ViaFabricPlusImpl
import com.viaversion.viafabricplus.screen.impl.SettingsScreen
import com.viaversion.viafabricplus.screen.base.list.VFPListEntry
import com.viaversion.viafabricplus.screen.impl.settings.VersionedBooleanListEntry
import com.viaversion.viafabricplus.screen.impl.settings.BooleanListEntry
import com.viaversion.viafabricplus.screen.impl.settings.EnumListEntry

class SettingsScreen : VFPTabbedScreen<com.viaversion.viafabricplus.api.settings.base.SettingGroup>(net.minecraft.network.chat.Component.translatable("screen.viafabricplus.settings"), true) {

    override fun tabs(): kotlin.collections.MutableList<com.viaversion.viafabricplus.api.settings.base.SettingGroup> {
        return ViaFabricPlusImpl.impl().settings().groups()
    }
    
    override fun tabTitle(tab: com.viaversion.viafabricplus.api.settings.base.SettingGroup): net.minecraft.network.chat.Component {
        return tab.name()
    }
    
    override fun entryHeight(): kotlin.Int {
        return (this.font.lineHeight + 2) * 2
    }
    
    override fun rowWidth(screenWidth: kotlin.Int): kotlin.Int {
        return kotlin.math.min(SettingsScreen.Companion.ROW_WIDTH, screenWidth - 20)
    }
    
    override fun listBottomMargin(): kotlin.Int {
        return SettingsScreen.Companion.LIST_BOTTOM_MARGIN
    }
    
    override fun entries(tab: com.viaversion.viafabricplus.api.settings.base.SettingGroup): kotlin.collections.MutableList<VFPListEntry> {
        return tab.settings().stream()
        .map<VFPListEntry> {setting: com.viaversion.viafabricplus.api.settings.base.Setting -> SettingsScreen.Companion.entry(setting)} 
        .filter {obj: VFPListEntry -> java.util.Objects.nonNull(obj)} 
        .toList()
    }
    
    override fun results(query: kotlin.String): kotlin.collections.MutableList<VFPListEntry> {
        return this.tabs().stream()
        .flatMap<com.viaversion.viafabricplus.api.settings.base.Setting> {group: com.viaversion.viafabricplus.api.settings.base.SettingGroup -> group.settings().stream()} 
        .filter {setting: com.viaversion.viafabricplus.api.settings.base.Setting -> setting.name().getString().lowercase().contains(query)} 
        .map<VFPListEntry> {setting: com.viaversion.viafabricplus.api.settings.base.Setting -> SettingsScreen.Companion.entry(setting)} 
        .filter {obj: VFPListEntry -> java.util.Objects.nonNull(obj)} 
        .toList()
    }
    
    companion object {
        private const val ROW_WIDTH: kotlin.Int = 360
        private const val LIST_BOTTOM_MARGIN: kotlin.Int = 5 // The screen has no buttons below the list
        
        private fun entry(setting: com.viaversion.viafabricplus.api.settings.base.Setting): VFPListEntry? {
            return when (setting) {
                is com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting -> VersionedBooleanListEntry(setting)
                is com.viaversion.viafabricplus.api.settings.base.BooleanSetting -> BooleanListEntry(setting)
                is com.viaversion.viafabricplus.api.settings.base.EnumSetting<*> -> EnumListEntry(setting)
                else -> {
                    ViaFabricPlusImpl.impl().logger().warn("Unknown setting type: {}", setting.javaClass.name)
                    null
                }
            }
        }
        
    }}
