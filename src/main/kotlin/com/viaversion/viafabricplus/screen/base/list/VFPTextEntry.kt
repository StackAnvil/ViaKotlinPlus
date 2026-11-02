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
package com.viaversion.viafabricplus.screen.base.list

import com.viaversion.viafabricplus.screen.base.list.VFPListEntry
import org.joml.Matrix3x2fStack

class VFPTextEntry (text: net.minecraft.network.chat.Component) : VFPListEntry() {

    private val text: net.minecraft.network.chat.Component
    
    init {
        this.text = text
    }
    
    override fun getNarration(): net.minecraft.network.chat.Component {
        return this.text
    }
    
    override fun extractContent(context: net.minecraft.client.gui.GuiGraphicsExtractor, mouseX: kotlin.Int, mouseY: kotlin.Int, hovered: kotlin.Boolean, deltaTicks: kotlin.Float) {
        val matrices: Matrix3x2fStack = context.pose()
        
                // The text isn't clickable, so it's rendered without the background of a normal entry
        matrices.pushMatrix()
        matrices.translate(getContentX().toFloat(), getContentY().toFloat())
        mappedRender(context, getContentWidth(), getContentHeight())
        matrices.popMatrix()
    }
    
    override fun mappedRender(context: net.minecraft.client.gui.GuiGraphicsExtractor, entryWidth: kotlin.Int, entryHeight: kotlin.Int) {
        val font: net.minecraft.client.gui.Font = net.minecraft.client.Minecraft.getInstance().font
        
        context.centeredText(font, this.text, entryWidth / 2, entryHeight / 2 - font.lineHeight / 2, -1)
    }
    
}
