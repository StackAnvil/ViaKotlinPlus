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
package com.viaversion.viafabricplus.features.c0_28

import com.viaversion.viafabricplus.features.c0_28.GridItemSelectionScreen

class GridItemSelectionScreen : net.minecraft.client.gui.screens.Screen(net.minecraft.network.chat.Component.nullToEmpty("Classic item selection")) {

    var itemGrid: kotlin.Array<kotlin.Array<net.minecraft.world.item.Item?>>? = null
    var selectedItem: net.minecraft.world.item.ItemStack? = null
    private var availableWoolColors: kotlin.collections.MutableList<net.minecraft.world.item.Item> = kotlin.collections.mutableListOf<net.minecraft.world.item.Item>()
    private var woolSelectionMode: kotlin.Boolean = false
    private var selectedWoolColor: net.minecraft.world.item.Item? = null
    override fun init() {
        woolSelectionMode = false
        selectedWoolColor = null
                // Not cached with the grid since CPE CustomBlocks support differs per server, not per version
        availableWoolColors = GridItemSelectionScreen.Companion.WOOL_COLORS.stream().filter {item: net.minecraft.world.item.Item -> com.viaversion.viafabricplus.ViaFabricPlus.api().limitations().itemExistsInConnection(item)} .toList()
        if (itemGrid != null) {
            return
        }
        val allowedItems: kotlin.collections.MutableList<net.minecraft.world.item.Item> = java.util.ArrayList<net.minecraft.world.item.Item>()
                // Calculate all visible items
        for (item in net.minecraft.core.registries.BuiltInRegistries.ITEM) {
            if (item === net.minecraft.world.item.Items.AIR || !item.requiredFeatures().contains(net.minecraft.world.flag.FeatureFlags.VANILLA)) {
                continue 
            }
            if (item !== net.minecraft.world.item.Items.WOOL.white() && GridItemSelectionScreen.Companion.WOOL_COLORS.contains(item)) {
                continue 
            }
            if (com.viaversion.viafabricplus.ViaFabricPlus.api().limitations().itemExistsInConnection(item)) {
                allowedItems.add(item)
            }
        }
        
        val grid = kotlin.Array<kotlin.Array<net.minecraft.world.item.Item?>>(net.minecraft.util.Mth.ceil(allowedItems.size / GridItemSelectionScreen.Companion.MAX_ROW_DIVIDER.toDouble())) {
            kotlin.arrayOfNulls<net.minecraft.world.item.Item>(GridItemSelectionScreen.Companion.MAX_ROW_DIVIDER)
        }
        itemGrid = grid
        var x: kotlin.Int = 0
        var y: kotlin.Int = 0
        for (allowedItem in allowedItems) {
            grid[y][x] = allowedItem
            x++
            if (x == GridItemSelectionScreen.Companion.MAX_ROW_DIVIDER) {
                x = 0
                y++
            }
        }
    }
    
    override fun mouseClicked(click: net.minecraft.client.input.MouseButtonEvent, doubled: kotlin.Boolean): kotlin.Boolean {
        if (woolSelectionMode) {
            if (isInsideWoolPanel(click.x(), click.y())) {
                val woolColor = selectedWoolColor
                if (woolColor != null) {
                    selectItem(woolColor)
                }
            } else {
                woolSelectionMode = false
                selectedWoolColor = null
            }
            return true
        }
        
        val item = selectedItem
        if (item != null) {
            if (item.getItem() === net.minecraft.world.item.Items.WOOL.white()) {
                woolSelectionMode = true
                selectedItem = null
                return true
            }
            
            selectItem(item.getItem())
            return true
        }
        return super.mouseClicked(click, doubled)
    }
    
    override fun keyPressed(input: net.minecraft.client.input.KeyEvent): kotlin.Boolean {
        if (minecraft.options.keyInventory.matches(input)) {
            if (woolSelectionMode) {
                woolSelectionMode = false
                selectedWoolColor = null
                return true
            }
            
            this.onClose()
            return true
        }
        if (input.isEscape() && woolSelectionMode) {
            woolSelectionMode = false
            selectedWoolColor = null
            return true
        }
        return super.keyPressed(input)
    }
    
    override fun extractRenderState(context: net.minecraft.client.gui.GuiGraphicsExtractor, mouseX: kotlin.Int, mouseY: kotlin.Int, delta: kotlin.Float) {
        super.extractRenderState(context, mouseX, mouseY, delta)
        if (woolSelectionMode) {
            renderWoolColors(context, mouseX, mouseY)
            return 
        }
        
        val halfWidth: kotlin.Int = this.width / 2
        val halfHeight: kotlin.Int = this.height / 2
        
        val boxWidth: kotlin.Int = GridItemSelectionScreen.Companion.ITEM_XY_BOX_DIMENSION_CLASSIC * GridItemSelectionScreen.Companion.MAX_ROW_DIVIDER + GridItemSelectionScreen.Companion.SIDE_OFFSET * 2
        val boxHeight: kotlin.Int = GridItemSelectionScreen.Companion.ITEM_XY_BOX_DIMENSION_CLASSIC * itemGrid!!.size + GridItemSelectionScreen.Companion.SIDE_OFFSET * 2 + GridItemSelectionScreen.Companion.SIDE_OFFSET
        
        val renderX: kotlin.Int = halfWidth - boxWidth / 2
        val renderY: kotlin.Int = halfHeight - boxHeight / 2
        
        context.fill(renderX, renderY, renderX + boxWidth, renderY + boxHeight, kotlin.Int.Companion.MIN_VALUE)
        context.centeredText(font, "Select block", renderX + boxWidth / 2, renderY + GridItemSelectionScreen.Companion.SIDE_OFFSET, -1)
        selectedItem = null
        
        var y: kotlin.Int = GridItemSelectionScreen.Companion.SIDE_OFFSET + GridItemSelectionScreen.Companion.SIDE_OFFSET
        for (items in itemGrid!!) {
            var x: kotlin.Int = GridItemSelectionScreen.Companion.SIDE_OFFSET
            for (item in items) {
                if (item == null) {
                    continue 
                }
                
                if (mouseX > renderX + x && mouseY > renderY + y && mouseX < renderX + x + GridItemSelectionScreen.Companion.ITEM_XY_BOX_DIMENSION_CLASSIC && mouseY < renderY + y + GridItemSelectionScreen.Companion.ITEM_XY_BOX_DIMENSION_CLASSIC) {
                    context.fill(renderX + x, renderY + y, renderX + x + GridItemSelectionScreen.Companion.ITEM_XY_BOX_DIMENSION_CLASSIC, renderY + y + GridItemSelectionScreen.Companion.ITEM_XY_BOX_DIMENSION_CLASSIC, kotlin.Int.Companion.MAX_VALUE)
                    selectedItem = item.getDefaultInstance()
                }
                context.item(item.getDefaultInstance(), renderX + x + GridItemSelectionScreen.Companion.ITEM_XY_BOX_DIMENSION_MODERN / 4, renderY + y + GridItemSelectionScreen.Companion.ITEM_XY_BOX_DIMENSION_MODERN / 4)
                x += GridItemSelectionScreen.Companion.ITEM_XY_BOX_DIMENSION_CLASSIC
            }
            y += GridItemSelectionScreen.Companion.ITEM_XY_BOX_DIMENSION_CLASSIC
        }
    }
    
    private fun renderWoolColors(context: net.minecraft.client.gui.GuiGraphicsExtractor, mouseX: kotlin.Int, mouseY: kotlin.Int) {
        val renderX: kotlin.Int = getWoolPanelX()
        val renderY: kotlin.Int = getWoolPanelY()
        val boxWidth: kotlin.Int = getWoolPanelWidth()
        val boxHeight: kotlin.Int = getWoolPanelHeight()
        
        context.fill(renderX, renderY, renderX + boxWidth, renderY + boxHeight, kotlin.Int.Companion.MIN_VALUE)
        context.centeredText(font, "Select wool color", renderX + boxWidth / 2, renderY + GridItemSelectionScreen.Companion.SIDE_OFFSET, -1)
        selectedWoolColor = null
        
        var woolIndex: kotlin.Int = 0
        var y: kotlin.Int = GridItemSelectionScreen.Companion.SIDE_OFFSET + GridItemSelectionScreen.Companion.SIDE_OFFSET
        for (row in 0 ..< net.minecraft.util.Mth.ceil(availableWoolColors.size / GridItemSelectionScreen.Companion.WOOL_PANEL_COLUMNS.toDouble())) {
            var x: kotlin.Int = GridItemSelectionScreen.Companion.SIDE_OFFSET
            for (column in 0 ..< GridItemSelectionScreen.Companion.WOOL_PANEL_COLUMNS) {
                if (woolIndex >= availableWoolColors.size) {
                    break
                }
                
                val item: net.minecraft.world.item.Item = availableWoolColors.get(woolIndex)
                if (mouseX > renderX + x && mouseY > renderY + y && mouseX < renderX + x + GridItemSelectionScreen.Companion.ITEM_XY_BOX_DIMENSION_CLASSIC && mouseY < renderY + y + GridItemSelectionScreen.Companion.ITEM_XY_BOX_DIMENSION_CLASSIC) {
                    context.fill(renderX + x, renderY + y, renderX + x + GridItemSelectionScreen.Companion.ITEM_XY_BOX_DIMENSION_CLASSIC, renderY + y + GridItemSelectionScreen.Companion.ITEM_XY_BOX_DIMENSION_CLASSIC, kotlin.Int.Companion.MAX_VALUE)
                    selectedWoolColor = item
                }
                context.item(item.getDefaultInstance(), renderX + x + GridItemSelectionScreen.Companion.ITEM_XY_BOX_DIMENSION_MODERN / 4, renderY + y + GridItemSelectionScreen.Companion.ITEM_XY_BOX_DIMENSION_MODERN / 4)
                x += GridItemSelectionScreen.Companion.ITEM_XY_BOX_DIMENSION_CLASSIC
                woolIndex++
            }
            y += GridItemSelectionScreen.Companion.ITEM_XY_BOX_DIMENSION_CLASSIC
        }
    }
    
    private fun selectItem(item: net.minecraft.world.item.Item) {
        val stack: net.minecraft.world.item.ItemStack = item.getDefaultInstance()
        this.minecraft.gameMode.handleCreativeModeItemAdd(stack, minecraft.player.getInventory().getSelectedSlot() + 36) // Beta Inventory Tracker
        this.minecraft.player.getInventory().setSelectedItem(stack)
        this.minecraft.player.inventoryMenu.broadcastChanges()
        
        net.minecraft.client.gui.components.AbstractWidget.playButtonClickSound(this.minecraft.getSoundManager())
        this.onClose()
    }
    
    private fun getWoolPanelWidth(): kotlin.Int {
        return GridItemSelectionScreen.Companion.ITEM_XY_BOX_DIMENSION_CLASSIC * GridItemSelectionScreen.Companion.WOOL_PANEL_COLUMNS + GridItemSelectionScreen.Companion.SIDE_OFFSET * 2
    }
    
    private fun getWoolPanelHeight(): kotlin.Int {
        return GridItemSelectionScreen.Companion.ITEM_XY_BOX_DIMENSION_CLASSIC * net.minecraft.util.Mth.ceil(availableWoolColors.size / GridItemSelectionScreen.Companion.WOOL_PANEL_COLUMNS.toDouble()) + GridItemSelectionScreen.Companion.SIDE_OFFSET * 2 + GridItemSelectionScreen.Companion.SIDE_OFFSET
    }
    
    private fun getWoolPanelX(): kotlin.Int {
        return this.width / 2 - getWoolPanelWidth() / 2
    }
    
    private fun getWoolPanelY(): kotlin.Int {
        return this.height / 2 - getWoolPanelHeight() / 2
    }
    
    private fun isInsideWoolPanel(mouseX: kotlin.Double, mouseY: kotlin.Double): kotlin.Boolean {
        val renderX: kotlin.Int = getWoolPanelX()
        val renderY: kotlin.Int = getWoolPanelY()
        return mouseX > renderX && mouseY > renderY && mouseX < renderX + getWoolPanelWidth() && mouseY < renderY + getWoolPanelHeight()
    }
    
    companion object {
        private const val MAX_ROW_DIVIDER: kotlin.Int = 9
        private const val ITEM_XY_BOX_DIMENSION_CLASSIC: kotlin.Int = 25
        private const val SIDE_OFFSET: kotlin.Int = 15
        private const val ITEM_XY_BOX_DIMENSION_MODERN: kotlin.Int = 16
        private const val WOOL_PANEL_COLUMNS: kotlin.Int = 7
        private val WOOL_COLORS: kotlin.collections.MutableList<net.minecraft.world.item.Item> = net.minecraft.world.item.Items.WOOL.asList()
        
    }}
