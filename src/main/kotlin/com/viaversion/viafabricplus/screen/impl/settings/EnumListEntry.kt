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
package com.viaversion.viafabricplus.screen.impl.settings

import com.viaversion.viafabricplus.screen.base.list.VFPListEntry
import com.viaversion.viafabricplus.screen.impl.settings.EnumListEntry

class EnumListEntry<T : com.viaversion.viafabricplus.api.settings.base.EnumSetting.EnumValue> (value: com.viaversion.viafabricplus.api.settings.base.EnumSetting<T>) : VFPListEntry() {

    private val value: com.viaversion.viafabricplus.api.settings.base.EnumSetting<T>
    
    init {
        this.value = value
    }
    
    override fun getNarration(): net.minecraft.network.chat.Component {
        return this.value.name()
    }
    
    override fun mappedMouseClicked() {
        val values: kotlin.Array<T> = this.value.values()
        val currentIndex: kotlin.Int = java.util.Arrays.asList<T>(*values).indexOf(this.value.value())
        this.value.setValue(if (currentIndex + 1 == values.size) values[0] else values[currentIndex + 1])
    }
    
    override fun mappedRender(context: net.minecraft.client.gui.GuiGraphicsExtractor, entryWidth: kotlin.Int, entryHeight: kotlin.Int) {
        val textRenderer: net.minecraft.client.gui.Font = net.minecraft.client.Minecraft.getInstance().font
        
        val name: net.minecraft.network.chat.Component = this.value.value().component()
        val offset: kotlin.Int = textRenderer.width(name) + EnumListEntry.Companion.VALUE_MARGIN
        renderScrollableText(context, this.value.name().copy().withStyle(net.minecraft.ChatFormatting.GRAY), offset)
        context.text(textRenderer, name, entryWidth - offset, entryHeight / 2 - textRenderer.lineHeight / 2, -1)
    }
    
    companion object {
        private const val VALUE_MARGIN: kotlin.Int = 2
        
    }}
