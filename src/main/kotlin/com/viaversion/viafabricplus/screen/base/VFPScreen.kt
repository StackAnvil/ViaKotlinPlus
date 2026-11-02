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
import org.joml.Matrix3x2fStack

abstract class VFPScreen (title: net.minecraft.network.chat.Component, backButton: kotlin.Boolean) : net.minecraft.client.gui.screens.Screen(title) {

    private val backButton: kotlin.Boolean
    var prevScreen: net.minecraft.client.gui.screens.Screen? = null
    
    init {
        this.backButton = backButton
    }
    
    fun open(prevScreen: net.minecraft.client.gui.screens.Screen?) {
        this.prevScreen = prevScreen
        VFPScreen.Companion.setScreen(this)
    }
    
    fun get(prevScreen: net.minecraft.client.gui.screens.Screen): net.minecraft.client.gui.screens.Screen {
        this.prevScreen = prevScreen
        return this
    }
    
    override fun init() {
        if (this.backButton) {
            this.addRenderableWidget<net.minecraft.client.gui.components.Button>(net.minecraft.client.gui.components.Button.builder(net.minecraft.network.chat.CommonComponents.GUI_BACK, net.minecraft.client.gui.components.Button.OnPress {`_`: net.minecraft.client.gui.components.Button -> this.onClose()}).pos(5, 5).size(VFPScreen.Companion.BACK_BUTTON_WIDTH, net.minecraft.client.gui.components.Button.DEFAULT_HEIGHT).build())
        }
    }
    
    protected fun addSearchBar(responder: java.util.function.Consumer<kotlin.String>): net.minecraft.client.gui.components.EditBox {
        val searchWidth: kotlin.Int = kotlin.math.min(VFPScreen.Companion.SEARCH_WIDTH, this.width - 20)
        
        val searchBar: net.minecraft.client.gui.components.EditBox = this.addRenderableWidget<net.minecraft.client.gui.components.EditBox>(net.minecraft.client.gui.components.EditBox(this.font, (this.width - searchWidth) / 2, VFPScreen.Companion.SEARCH_TOP, searchWidth, VFPScreen.Companion.SEARCH_HEIGHT, VFPScreen.Companion.SEARCH_TITLE))
        searchBar.setHint(VFPScreen.Companion.SEARCH_HINT)
        searchBar.setResponder(java.util.function.Consumer {query: kotlin.String -> responder.accept(query.trim {it <= ' '} .lowercase())})
        return searchBar
    }
    
    protected fun addFooter(vararg buttons: net.minecraft.client.gui.components.Button) {
        val buttonWidth: kotlin.Int = kotlin.math.min(VFPScreen.Companion.BUTTON_WIDTH, (this.width - (buttons.size + 1) * VFPScreen.Companion.BUTTON_MARGIN) / buttons.size)
        
        val footer: net.minecraft.client.gui.layouts.LinearLayout = net.minecraft.client.gui.layouts.LinearLayout.horizontal().spacing(VFPScreen.Companion.BUTTON_MARGIN)
        for (button in buttons) {
            button.setWidth(buttonWidth)
            footer.addChild<net.minecraft.client.gui.components.Button>(button)
        }
        
        footer.arrangeElements()
        net.minecraft.client.gui.layouts.FrameLayout.centerInRectangle(footer, 0, this.height - VFPScreen.Companion.FOOTER_HEIGHT, this.width, VFPScreen.Companion.FOOTER_HEIGHT)
        footer.visitWidgets(java.util.function.Consumer {widget: net.minecraft.client.gui.components.AbstractWidget -> this.addRenderableWidget(widget)})
    }
    
    override fun extractRenderState(graphics: net.minecraft.client.gui.GuiGraphicsExtractor, mouseX: kotlin.Int, mouseY: kotlin.Int, a: kotlin.Float) {
        super.extractRenderState(graphics, mouseX, mouseY, a)
        this.renderTitle(graphics)
    }
    
    override fun onClose() {
        val previous = this.prevScreen
        if (previous is VFPScreen) {
            previous.open(previous.prevScreen) // Support recursive opening
        } else {
            net.minecraft.client.Minecraft.getInstance().gui.setScreen(previous)
        }
    }
    
    open fun renderTitle(context: net.minecraft.client.gui.GuiGraphicsExtractor) {
        val matrices: Matrix3x2fStack = context.pose()
        
        matrices.pushMatrix()
        matrices.scale(2f, 2f)
        context.centeredText(font, "ViaFabricPlus", width / 4, VFPScreen.Companion.TITLE_Y, VFPScreen.Companion.ACCENT_COLOR)
        matrices.popMatrix()
    }
    
    fun renderScreenTitle(context: net.minecraft.client.gui.GuiGraphicsExtractor) {
        context.centeredText(this.font, this.title, this.width / 2, VFPScreen.Companion.SCREEN_TITLE_Y, -1)
    }
    
    companion object {
        const val ACCENT_COLOR: kotlin.Int = -0xa75901
        
        val SEARCH_TITLE: net.minecraft.network.chat.Component = net.minecraft.network.chat.Component.translatable("gui.selectWorld.search")
        
        const val SEARCH_TOP: kotlin.Int = 36 // Below the title
        const val SEARCH_HEIGHT: kotlin.Int = 20
        const val SEARCH_MARGIN: kotlin.Int = 4
        
         // Everything a screen puts below the search bar starts here
        val CONTENT_TOP: kotlin.Int = VFPScreen.Companion.SEARCH_TOP + VFPScreen.Companion.SEARCH_HEIGHT + VFPScreen.Companion.SEARCH_MARGIN
        
        const val FOOTER_HEIGHT: kotlin.Int = 30
        
        private val SEARCH_HINT: net.minecraft.network.chat.Component = VFPScreen.Companion.SEARCH_TITLE.copy().setStyle(net.minecraft.client.gui.components.EditBox.SEARCH_HINT_STYLE)
        private const val SEARCH_WIDTH: kotlin.Int = 260
        
        private val TOAST_TITLE: net.minecraft.network.chat.Component = net.minecraft.network.chat.Component.nullToEmpty("ViaFabricPlus")
        private val TOAST_ID: net.minecraft.client.gui.components.toasts.SystemToast.SystemToastId = net.minecraft.client.gui.components.toasts.SystemToast.SystemToastId()
        
        private const val TITLE_Y: kotlin.Int = 6 // Inside the doubled matrix of the title
        private const val SCREEN_TITLE_Y: kotlin.Int = 70 // Below the doubled title, above the content of the plain screens
        private const val BACK_BUTTON_WIDTH: kotlin.Int = 60
        private const val BUTTON_WIDTH: kotlin.Int = 98
        private const val BUTTON_MARGIN: kotlin.Int = 4
        
        fun setScreen(screen: net.minecraft.client.gui.screens.Screen) {
            net.minecraft.client.Minecraft.getInstance().execute(java.lang.Runnable {net.minecraft.client.Minecraft.getInstance().gui.setScreen(screen)})
        }
        
        fun showToast(message: net.minecraft.network.chat.Component) {
            net.minecraft.client.Minecraft.getInstance().execute(java.lang.Runnable {net.minecraft.client.gui.components.toasts.SystemToast.addOrUpdate(net.minecraft.client.Minecraft.getInstance().gui.toastManager(), VFPScreen.Companion.TOAST_ID, VFPScreen.Companion.TOAST_TITLE, message)})
        }
        
    }}
