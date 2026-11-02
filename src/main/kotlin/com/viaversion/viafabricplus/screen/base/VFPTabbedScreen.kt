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
import java.util.HashMap
import com.viaversion.viafabricplus.screen.base.VFPTabBar
import com.viaversion.viafabricplus.screen.base.VFPTabbedScreen.ListTab
import com.viaversion.viafabricplus.screen.base.list.VFPListEntry
import com.viaversion.viafabricplus.screen.base.VFPTabbedScreen.TabList
import com.viaversion.viafabricplus.screen.base.list.VFPList

abstract class VFPTabbedScreen<T> protected constructor(title: net.minecraft.network.chat.Component, backButton: kotlin.Boolean) : VFPScreen(title, backButton) {

    private val scrollAmounts: kotlin.collections.MutableMap<T, kotlin.Double> = HashMap<T, kotlin.Double>()
    private val tabManager: net.minecraft.client.gui.components.tabs.TabManager = net.minecraft.client.gui.components.tabs.TabManager(java.util.function.Consumer {widget: net.minecraft.client.gui.components.AbstractWidget -> this.addRenderableWidget(widget)}, java.util.function.Consumer {widget: net.minecraft.client.gui.components.AbstractWidget -> this.removeWidget(widget)}, java.util.function.Consumer {tab: net.minecraft.client.gui.components.tabs.Tab -> this.onTabSelected(tab)}, java.util.function.Consumer {`_`: net.minecraft.client.gui.components.tabs.Tab -> })
    
    private lateinit var tabBar: VFPTabBar
    private lateinit var searchBox: net.minecraft.client.gui.components.EditBox
    private lateinit var searchTab: ListTab
    private lateinit var tabKeys: kotlin.collections.MutableList<T>
    private lateinit var tabWidgets: com.google.common.collect.ImmutableList<net.minecraft.client.gui.components.tabs.Tab>
    private lateinit var currentTab: net.minecraft.client.gui.components.tabs.Tab
    private var lastTab: T? = null
    
    protected abstract fun tabs(): kotlin.collections.MutableList<T>
    
    protected abstract fun tabTitle(tab: T): net.minecraft.network.chat.Component
    
    protected abstract fun entryHeight(): kotlin.Int
    
    protected abstract fun rowWidth(screenWidth: kotlin.Int): kotlin.Int
    
    protected abstract fun entries(tab: T): kotlin.collections.MutableList<VFPListEntry>
    
    protected abstract fun results(query: kotlin.String): kotlin.collections.MutableList<VFPListEntry>
    
    protected open fun initialTab(): T {
        val tabs: kotlin.collections.MutableList<T> = this.tabs()
        val last = this.lastTab
        return if (last != null && tabs.contains(last)) last else tabs.first()
    }
    
    protected open fun listBottomMargin(): kotlin.Int {
        return VFPScreen.FOOTER_HEIGHT
    }
    
    override fun init() {
        this.tabKeys = this.tabs()
        val listTabs: com.google.common.collect.ImmutableList.Builder<net.minecraft.client.gui.components.tabs.Tab> = com.google.common.collect.ImmutableList.builder<net.minecraft.client.gui.components.tabs.Tab>()
        val tabButtons: com.google.common.collect.ImmutableList.Builder<net.minecraft.client.gui.components.TabButton> = com.google.common.collect.ImmutableList.builder<net.minecraft.client.gui.components.TabButton>()
        for (tab in this.tabKeys) {
            val listTab: ListTab = ListTab(tab)
            listTabs.add(listTab)
            tabButtons.add(net.minecraft.client.gui.components.tabs.MenuTabBar.MenuTabButton(this.tabManager, listTab, 0, net.minecraft.client.gui.components.tabs.MenuTabBar.HEIGHT))
        }
        this.tabWidgets = listTabs.build()
        this.tabBar = this.addRenderableWidget<VFPTabBar>(VFPTabBar(VFPScreen.CONTENT_TOP, this.width, net.minecraft.client.gui.components.tabs.MenuTabBar.HEIGHT, this.tabManager, tabButtons.build(), this.tabWidgets))
        
                // The search results are held by a tab which isn't part of the tab bar, so selecting them
        // deselects all other tabs without any further handling
        this.searchTab = ListTab(null)
        
        this.searchBox = this.addSearchBar(java.util.function.Consumer {query: kotlin.String -> this.search(query)})
        
        val initialTab: kotlin.Int = kotlin.math.max(0, this.tabKeys.indexOf(this.initialTab()))
        this.currentTab = this.tabWidgets.get(initialTab)
        this.tabBar.selectTab(initialTab, false)
        
        this.tabBar.arrangeElements(this.width)
        val listTop: kotlin.Int = this.tabBar.getRectangle().bottom()
        this.tabManager.setTabArea(net.minecraft.client.gui.navigation.ScreenRectangle(0, listTop, this.width, this.height - this.listBottomMargin() - listTop))
        
        super.init()
    }
    
    override fun keyPressed(event: net.minecraft.client.input.KeyEvent): kotlin.Boolean {
        return this.tabBar.keyPressed(event) || super.keyPressed(event)
    }
    
    private fun search(query: kotlin.String) {
        if (query.isEmpty()) {
            this.tabManager.setCurrentTab(this.currentTab, false)
            return 
        }
        
        this.searchTab.list.showResults(query)
        this.tabManager.setCurrentTab(this.searchTab, false)
    }
    
     /**
 * Rebuilds the entries of a tab from [.entries] while keeping its scroll position.
 * 
 * @param tab the tab to rebuild, ignored if not part of [.tabs]
 */
    protected fun refreshTab(tab: T) {
        val index: kotlin.Int = this.tabKeys.indexOf(tab)
        if (index != -1) {
            (this.tabWidgets[index] as VFPTabbedScreen<T>.ListTab).list.refresh()
        }
    }
    
    private fun onTabSelected(tab: net.minecraft.client.gui.components.tabs.Tab) {
        if (tab === this.searchTab) {
            return 
        }
        
        this.currentTab = tab
        
        val index: kotlin.Int = this.tabWidgets.indexOf(tab)
        if (index != -1) {
            this.lastTab = this.tabKeys.get(index) // Reopened when the widgets are rebuilt
        }
        
        this.searchBox.setValue("") // Selecting a tab leaves the search results
    }
    
    private inner class ListTab (tab: T?) : net.minecraft.client.gui.components.tabs.Tab {
    
        private val tab: T?
        private val layout: net.minecraft.client.gui.layouts.FrameLayout = net.minecraft.client.gui.layouts.FrameLayout()
        val list: TabList
        
        init {
            this.tab = tab
            this.list = TabList(minecraft, width, height, listBottomMargin(), entryHeight(), tab)
        }
        
        override fun getTabTitle(): net.minecraft.network.chat.Component {
            val tab = this.tab ?: return VFPScreen.SEARCH_TITLE
            return tabTitle(tab)
        }
        
        override fun getTabExtraNarration(): net.minecraft.network.chat.Component {
            return net.minecraft.network.chat.Component.empty()
        }
        
        override fun visitChildren(childrenConsumer: java.util.function.Consumer<net.minecraft.client.gui.components.AbstractWidget>) {
            childrenConsumer.accept(this.list)
        }
        
        override fun doLayout(screenRectangle: net.minecraft.client.gui.navigation.ScreenRectangle) {
            this.list.updateSizeAndPosition(screenRectangle.width(), screenRectangle.height(), screenRectangle.left(), screenRectangle.top())
        }
        
        override fun getLayout(): net.minecraft.client.gui.layouts.Layout {
            return this.layout
        }
        
    }
    
    private inner class TabList (minecraftClient: net.minecraft.client.Minecraft, width: kotlin.Int, height: kotlin.Int, bottom: kotlin.Int, entryHeight: kotlin.Int, tab: T?) : VFPList(minecraftClient, width, height, VFPScreen.CONTENT_TOP + net.minecraft.client.gui.components.tabs.MenuTabBar.HEIGHT, bottom, entryHeight) {
    
        private val tab: T?
        
        init {
            this.tab = tab
            if (tab != null) {
                entries(tab).forEach(java.util.function.Consumer { entry: VFPListEntry -> this.addEntry(entry) })
                // Needs calling last to have the entries added before setting the scroll amount
                setScrollAmount(scrollAmounts.getOrDefault(tab, 0.0))
            }
        }
        
        fun showResults(query: kotlin.String) {
            this.replaceEntries(results(query))
            this.setScrollAmount(0.0) // Also repositions the new entries
        }
        
        fun refresh() {
            val tab = this.tab ?: return
            this.replaceEntries(entries(tab))
            this.setScrollAmount(this.scrollAmount()) // Clamps to the new size and repositions the new entries
        }
        
        override fun getRowWidth(): kotlin.Int {
            return rowWidth(this.width)
        }
        
        override fun updateSlotAmount(amount: kotlin.Double) {
            val tab = this.tab
            if (tab != null) { // Search results always start at the top
                scrollAmounts.put(tab, amount)
            }
        }
        
    }
    
}
