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
package com.viaversion.viafabricplus.screen.base

import com.viaversion.viafabricplus.screen.base.VFPScreen
import com.viaversion.viafabricplus.screen.base.VFPPopup

abstract class VFPPopup protected constructor(title: net.minecraft.network.chat.Component, bodyWidth: kotlin.Int, bodyHeight: kotlin.Int) : VFPScreen(title, false) {

    private val bodyWidth: kotlin.Int
    private val bodyHeight: kotlin.Int
    
    private var panel: net.minecraft.client.gui.navigation.ScreenRectangle = net.minecraft.client.gui.navigation.ScreenRectangle.empty()
    
    init {
        this.bodyWidth = bodyWidth
        this.bodyHeight = bodyHeight
    }
    
    override fun init() {
        val previous = this.prevScreen
        if (previous != null) {
            previous.init(this.width, this.height)
        }
        
        val panelWidth: kotlin.Int = kotlin.math.min(this.bodyWidth, this.width - 2 * net.minecraft.client.gui.components.PopupScreen.BG_BORDER_WITH_SPACING)
        val panelHeight: kotlin.Int = kotlin.math.min(VFPPopup.Companion.TITLE_HEIGHT + this.bodyHeight, this.height - 2 * net.minecraft.client.gui.components.PopupScreen.BG_BORDER_WITH_SPACING)
        this.panel = net.minecraft.client.gui.navigation.ScreenRectangle((this.width - panelWidth) / 2, (this.height - panelHeight) / 2, panelWidth, panelHeight)
        
        this.initBody(net.minecraft.client.gui.navigation.ScreenRectangle(this.panel.left(), this.panel.top() + VFPPopup.Companion.TITLE_HEIGHT, panelWidth, panelHeight - VFPPopup.Companion.TITLE_HEIGHT))
        
        val closeButton: com.mojang.realmsclient.RealmsMainScreen.CrossButton = this.addRenderableWidget<com.mojang.realmsclient.RealmsMainScreen.CrossButton>(com.mojang.realmsclient.RealmsMainScreen.CrossButton(net.minecraft.client.gui.components.Button.OnPress {`_`: net.minecraft.client.gui.components.Button -> this.onClose()}, net.minecraft.network.chat.CommonComponents.GUI_BACK))
        val closeMargin: kotlin.Int = (VFPPopup.Companion.TITLE_HEIGHT - closeButton.getHeight()) / 2
        closeButton.setPosition(this.panel.right() - closeButton.getWidth() - closeMargin, this.panel.top() + closeMargin)
        
        super.init()
    }
    
    protected abstract fun initBody(body: net.minecraft.client.gui.navigation.ScreenRectangle)
    
    override fun repositionElements() {
        val previous = this.prevScreen
        if (previous != null) {
            previous.resize(this.width, this.height)
        }
        
        super.repositionElements()
    }
    
    override fun added() {
        super.added()
        
        val previous = this.prevScreen
        if (previous != null) {
            previous.clearFocus()
        }
    }
    
    override fun extractBackground(graphics: net.minecraft.client.gui.GuiGraphicsExtractor, mouseX: kotlin.Int, mouseY: kotlin.Int, a: kotlin.Float) {
        val previous = this.prevScreen
        if (previous == null) {
            super.extractBackground(graphics, mouseX, mouseY, a)
        } else {
            // Keeps the screen the popup was opened from visible behind it, dimmed by the transparent background
            previous.extractBackground(graphics, mouseX, mouseY, a)
            graphics.nextStratum()
            previous.extractRenderState(graphics, -1, -1, a)
            graphics.nextStratum()
            this.extractTransparentBackground(graphics)
        }
        
        val x: kotlin.Int = this.panel.left() - net.minecraft.client.gui.components.PopupScreen.BG_BORDER_WITH_SPACING
        val y: kotlin.Int = this.panel.top() - net.minecraft.client.gui.components.PopupScreen.BG_BORDER_WITH_SPACING
        val width: kotlin.Int = this.panel.width() + 2 * net.minecraft.client.gui.components.PopupScreen.BG_BORDER_WITH_SPACING
        val height: kotlin.Int = this.panel.height() + 2 * net.minecraft.client.gui.components.PopupScreen.BG_BORDER_WITH_SPACING
        graphics.blitSprite(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, net.minecraft.client.gui.components.PopupScreen.BACKGROUND_SPRITE, x, y, width, height)
    }
    
    override fun renderTitle(context: net.minecraft.client.gui.GuiGraphicsExtractor) {
        val x: kotlin.Int = this.panel.left() + this.panel.width() / 2
        val y: kotlin.Int = this.panel.top() + (VFPPopup.Companion.TITLE_HEIGHT - this.font.lineHeight) / 2
        context.centeredText(this.font, this.title, x, y, VFPScreen.ACCENT_COLOR)
    }
    
    companion object {
        private const val TITLE_HEIGHT: kotlin.Int = 20
        
    }}
