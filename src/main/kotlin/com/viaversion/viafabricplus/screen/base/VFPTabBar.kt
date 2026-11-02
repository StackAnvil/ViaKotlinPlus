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

import com.viaversion.viafabricplus.screen.base.VFPTabBar

class VFPTabBar (y: kotlin.Int, width: kotlin.Int, height: kotlin.Int, tabManager: net.minecraft.client.gui.components.tabs.TabManager, tabButtons: com.google.common.collect.ImmutableList<net.minecraft.client.gui.components.TabButton>, tabs: com.google.common.collect.ImmutableList<net.minecraft.client.gui.components.tabs.Tab>) : net.minecraft.client.gui.components.tabs.MenuTabBar(0, y, width, height, tabManager, tabButtons, tabs) {

    override fun arrangeElements(width: kotlin.Int) {
        super.arrangeElements(width)
        
                // Vanilla spreads a fixed width over all tabs, which leaves too little room for longer labels with many tabs
        val font: net.minecraft.client.gui.Font = net.minecraft.client.Minecraft.getInstance().font
        val labelWidth: kotlin.Int = this.tabButtons.stream().mapToInt {button: net.minecraft.client.gui.components.TabButton -> font.width(button.getMessage())} .max().orElse(0)
        val tabWidth: kotlin.Int = kotlin.math.min(labelWidth + VFPTabBar.Companion.LABEL_MARGIN * 2, (width - 2 * net.minecraft.client.gui.components.tabs.MenuTabBar.MARGIN) / this.tabButtons.size) and 1.inv() // Even like vanilla
        if (tabWidth > this.tabButtons.first().getWidth()) {
            this.tabButtons.forEach(java.util.function.Consumer {button: net.minecraft.client.gui.components.TabButton -> button.setWidth(tabWidth)})
            this.layout.arrangeElements()
            this.layout.setX(net.minecraft.util.Mth.roundToward((width - this.layout.getWidth()) / 2, 2))
        }
        
        this.layout.setY(this.getY())
    }
    
    companion object {
        private const val LABEL_MARGIN: kotlin.Int = 4
        
    }}
