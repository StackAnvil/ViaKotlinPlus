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
import com.viaversion.viafabricplus.screen.impl.settings.VersionedBooleanListEntry

class VersionedBooleanListEntry (value: com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting) : VFPListEntry() {

    private val value: com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting
    
    init {
        this.value = value
    }
    
    override fun getNarration(): net.minecraft.network.chat.Component {
        return this.value.name()
    }
    
    override fun mappedMouseClicked() {
        this.value.setActive(!this.value.value())
    }
    
    override fun mappedRender(context: net.minecraft.client.gui.GuiGraphicsExtractor, entryWidth: kotlin.Int, entryHeight: kotlin.Int) {
        val textRenderer: net.minecraft.client.gui.Font = net.minecraft.client.Minecraft.getInstance().font
        
        val text: net.minecraft.network.chat.Component = if (this.value.value()) net.minecraft.network.chat.Component.translatable("base.viafabricplus.on") else net.minecraft.network.chat.Component.translatable("base.viafabricplus.off")
        
        val offset: kotlin.Int = textRenderer.width(text) + VersionedBooleanListEntry.Companion.STATE_MARGIN
        renderScrollableText(context, net.minecraft.network.chat.Component.nullToEmpty(net.minecraft.ChatFormatting.GRAY.toString() + this.value.name().getString() + " " + net.minecraft.ChatFormatting.RESET + this.value.versionRange().toString()), offset)
        context.text(textRenderer, text, entryWidth - offset, entryHeight / 2 - textRenderer.lineHeight / 2, if (this.value.value()) net.minecraft.util.CommonColors.GREEN else net.minecraft.util.CommonColors.RED)
    }
    
    companion object {
        private const val STATE_MARGIN: kotlin.Int = 2
        
    }}
