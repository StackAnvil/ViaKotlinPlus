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

abstract class VFPListEntry : net.minecraft.client.gui.components.ObjectSelectionList.Entry<VFPListEntry>() {

    open fun mappedRender(context: net.minecraft.client.gui.GuiGraphicsExtractor, entryWidth: kotlin.Int, entryHeight: kotlin.Int) {
        // To be overridden
    }
    
    open fun mappedMouseClicked() {
        // To be overridden
    }
    
    override fun mouseClicked(click: net.minecraft.client.input.MouseButtonEvent, doubled: kotlin.Boolean): kotlin.Boolean {
        mappedMouseClicked()
        net.minecraft.client.gui.components.AbstractWidget.playButtonClickSound(net.minecraft.client.Minecraft.getInstance().getSoundManager())
        return super.mouseClicked(click, doubled)
    }
    
    fun renderScrollableText(context: net.minecraft.client.gui.GuiGraphicsExtractor, text: net.minecraft.network.chat.Component, offset: kotlin.Int) {
        val font: net.minecraft.client.gui.Font = net.minecraft.client.Minecraft.getInstance().font
        
        val textY: kotlin.Int = getContentHeight() / 2 - font.lineHeight / 2
        val fontWidth: kotlin.Int = font.width(text)
        if (fontWidth > (getContentWidth() - offset)) {
            val time: kotlin.Double = net.minecraft.util.Util.getMillis().toDouble() / 1000.0
            val interpolateEnd: kotlin.Double = (fontWidth - (getContentWidth() - offset - (VFPListEntry.Companion.SCISSORS_OFFSET + VFPListEntry.Companion.SLOT_MARGIN))).toDouble()
            
            val interpolatedValue: kotlin.Double = kotlin.math.sin((java.lang.Math.PI / 2) * kotlin.math.cos(java.lang.Math.PI * 2 * time / kotlin.math.max(interpolateEnd * 0.5, 3.0))) / 2.0 + 0.5
            
            context.enableScissor(0, 0, getContentWidth() - offset - VFPListEntry.Companion.SCISSORS_OFFSET, getContentHeight())
            context.text(font, text, VFPListEntry.Companion.SLOT_MARGIN - net.minecraft.util.Mth.lerp(interpolatedValue, 0.0, interpolateEnd).toInt(), textY, -1)
            context.disableScissor()
        } else {
            context.text(font, text, VFPListEntry.Companion.SLOT_MARGIN, textY, -1)
        }
    }
    
    override fun extractContent(graphics: net.minecraft.client.gui.GuiGraphicsExtractor, mouseX: kotlin.Int, mouseY: kotlin.Int, hovered: kotlin.Boolean, deltaTicks: kotlin.Float) {
        val matrices: Matrix3x2fStack = graphics.pose()
        
                // The content is translated to the entry, so everything below renders in the entry's own space
        matrices.pushMatrix()
        matrices.translate(getContentX().toFloat(), getContentY().toFloat())
        graphics.fill(0, 0, getContentWidth(), getContentHeight(), kotlin.Int.Companion.MIN_VALUE)
        mappedRender(graphics, getContentWidth(), getContentHeight())
        matrices.popMatrix()
    }
    
    companion object {
        private const val SCISSORS_OFFSET: kotlin.Int = 4
        const val SLOT_MARGIN: kotlin.Int = 3
        
    }}
