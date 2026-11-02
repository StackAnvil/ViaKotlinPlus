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
package com.viaversion.viafabricplus.screen.impl.serverlist

import com.viaversion.viafabricplus.screen.base.VFPScreen
import com.viaversion.viafabricplus.screen.impl.serverlist.ClassiCubeServerListScreen
import com.viaversion.viafabricplus.screen.impl.serverlist.ClassiCubeServerListScreen.SlotList
import com.viaversion.viafabricplus.features.global.ClassiCubeAccount
import de.florianreuth.classic4j.api.LoginProcessHandler
import de.florianreuth.classic4j.model.classicube.account.CCAccount
import com.viaversion.viafabricplus.ViaFabricPlusImpl
import de.florianreuth.classic4j.ClassiCubeHandler
import de.florianreuth.classic4j.model.classicube.server.CCServerList
import de.florianreuth.classic4j.model.classicube.server.CCServerInfo
import com.viaversion.viafabricplus.screen.base.list.VFPList
import com.viaversion.viafabricplus.screen.base.list.VFPTextEntry
import com.viaversion.viafabricplus.screen.base.list.VFPListEntry
import com.viaversion.viafabricplus.protocoltranslator.impl.provider.vialegacy.ViaFabricPlusClassicMPPassProvider
import com.viaversion.viafabricplus.util.network.ConnectionUtil
import net.raphimc.vialegacy.api.LegacyProtocolVersion

class ClassiCubeServerListScreen : VFPScreen(ClassiCubeServerListScreen.Companion.TITLE, true) {

    private lateinit var list: SlotList
    
    override fun init() {
        if (ClassiCubeServerListScreen.Companion.loggedIn() && ClassiCubeServerListScreen.Companion.serverList == null) {
            this.load()
        }
        
        this.addSearchBar(java.util.function.Consumer {query: kotlin.String -> this.search(query)})
        
        this.list = this.addRenderableWidget<SlotList>(SlotList(this.minecraft, this.width, this.height, VFPScreen.CONTENT_TOP, VFPScreen.FOOTER_HEIGHT, 
        (this.font.lineHeight + 2) * 3 /* title is 2 */))
        
        this.addFooter(net.minecraft.client.gui.components.Button.builder(net.minecraft.network.chat.Component.translatable("base.viafabricplus.logout"), net.minecraft.client.gui.components.Button.OnPress {
        `_`: net.minecraft.client.gui.components.Button -> ClassiCubeAccount.set(null)
        ClassiCubeServerListScreen.Companion.serverList = null
        this.onClose()
        
        }).build())
        
        super.init()
    }
    
    private fun search(query: kotlin.String) {
        this.list.showServers(query)
    }
    
     // Guarded against the screen being rebuilt while the request is still running, which would send it again
    private fun load() {
        if (ClassiCubeServerListScreen.Companion.loading) {
            return 
        }
        
        ClassiCubeServerListScreen.Companion.loading = true
        if (ClassiCubeAccount.authenticated()) {
            this.requestServers()
            return 
        }
        
                // Without a session the API omits the addresses of the servers, so a restored account logs in again first
        ClassiCubeAccount.authenticate(null, object : LoginProcessHandler {
        
            override fun handleSuccessfulLogin(account: CCAccount) {
                requestServers()
            }
            
            override fun handleMfa(account: CCAccount) {
                ClassiCubeServerListScreen.Companion.loading = false
                VFPScreen.showToast(net.minecraft.network.chat.Component.translatable("classic4j_library.viafabricplus.error.logincode"))
                ViaFabricPlusImpl.impl().screens().classiCubeMFAScreen().open(prevScreen)
            }
            
            override fun handleException(throwable: Throwable) {
                fail(throwable)
            }
        })
    }
    
    private fun requestServers() {
        ClassiCubeHandler.requestServerList(ClassiCubeAccount.get(), java.util.function.Consumer {
        response: CCServerList -> ClassiCubeServerListScreen.Companion.serverList = java.util.ArrayList<CCServerInfo>(response.servers())
        ClassiCubeServerListScreen.Companion.loading = false
        net.minecraft.client.Minecraft.getInstance().execute(java.lang.Runnable {this.rebuildWidgets()})
        
        }, java.util.function.Consumer {throwable: Throwable -> this.fail(throwable)})
    }
    
    private fun fail(throwable: Throwable) {
        ClassiCubeServerListScreen.Companion.loading = false
        ViaFabricPlusImpl.impl().logger().error("Error while loading ClassiCube servers!", throwable)
        VFPScreen.showToast(net.minecraft.network.chat.Component.translatable("base.viafabricplus.something_went_wrong"))
        net.minecraft.client.Minecraft.getInstance().execute(java.lang.Runnable {this.rebuildWidgets()}) // Replaces the loading text of the list
    }
    
    class SlotList (minecraftClient: net.minecraft.client.Minecraft, width: kotlin.Int, height: kotlin.Int, top: kotlin.Int, bottom: kotlin.Int, entryHeight: kotlin.Int) : VFPList(minecraftClient, width, height, top, bottom, entryHeight) {
        private var searching: kotlin.Boolean = false
        
        init {
            this.showServers("")
        }
        
        fun showServers(query: kotlin.String) {
            this.searching = !query.isEmpty()
            this.clearEntries()
            
            if (ClassiCubeServerListScreen.Companion.serverList == null) { // The servers are either still loading or the request failed
                this.addEntry(VFPTextEntry(if (ClassiCubeServerListScreen.Companion.loading) net.minecraft.network.chat.Component.translatable("classicube.viafabricplus.loading") else net.minecraft.network.chat.Component.translatable("base.viafabricplus.something_went_wrong")))
                return 
            }
            
            ClassiCubeServerListScreen.Companion.serverList!!.stream().filter {server: CCServerInfo -> SlotList.Companion.matches(server, query)} .forEach {server: CCServerInfo -> this.addEntry(com.viaversion.viafabricplus.screen.impl.serverlist.ClassiCubeServerListScreen.ServerSlot(server))} 
            
                        // Needs calling last to have the entries added before setting the scroll amount
            this.setScrollAmount(if (this.searching) 0.0 else SlotList.Companion.scrollAmount)
        }
        
        override fun getRowWidth(): kotlin.Int {
            return kotlin.math.min(ClassiCubeServerListScreen.Companion.ROW_WIDTH, this.width - 20)
        }
        
        override fun updateSlotAmount(amount: kotlin.Double) {
            if (!this.searching) { // Searching always starts at the top
                SlotList.Companion.scrollAmount = amount
            }
        }
        companion object {
            private var scrollAmount: kotlin.Double = 0.0
            
            private fun matches(server: CCServerInfo, query: kotlin.String): kotlin.Boolean {
                return server.name().lowercase().contains(query) || server.software().lowercase().contains(query) || server.ip().lowercase().contains(query)
            }
            
        }}
    
    class ServerSlot (classiCubeServerInfo: CCServerInfo) : VFPListEntry() {
    
        private val classiCubeServerInfo: CCServerInfo
        
        init {
            this.classiCubeServerInfo = classiCubeServerInfo
        }
        
        override fun getNarration(): net.minecraft.network.chat.Component {
            return net.minecraft.network.chat.Component.nullToEmpty(classiCubeServerInfo.name())
        }
        
        override fun mappedMouseClicked() {
            ViaFabricPlusClassicMPPassProvider.classicubeMPPass = classiCubeServerInfo.mpPass()
            ConnectionUtil.connect(classiCubeServerInfo.name(), classiCubeServerInfo.ip() + ":" + classiCubeServerInfo.port(), LegacyProtocolVersion.c0_30cpe)
        }
        
        override fun mappedRender(context: net.minecraft.client.gui.GuiGraphicsExtractor, entryWidth: kotlin.Int, entryHeight: kotlin.Int) {
            context.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, net.minecraft.client.gui.screens.FaviconTexture.MISSING_LOCATION, VFPListEntry.SLOT_MARGIN, (entryHeight - com.viaversion.viafabricplus.screen.impl.serverlist.ClassiCubeServerListScreen.ServerSlot.Companion.ICON_SIZE) / 2, 0f, 0f, com.viaversion.viafabricplus.screen.impl.serverlist.ClassiCubeServerListScreen.ServerSlot.Companion.ICON_SIZE, com.viaversion.viafabricplus.screen.impl.serverlist.ClassiCubeServerListScreen.ServerSlot.Companion.ICON_SIZE, com.viaversion.viafabricplus.screen.impl.serverlist.ClassiCubeServerListScreen.ServerSlot.Companion.ICON_SIZE, com.viaversion.viafabricplus.screen.impl.serverlist.ClassiCubeServerListScreen.ServerSlot.Companion.ICON_SIZE)
            
            val font: net.minecraft.client.gui.Font = net.minecraft.client.Minecraft.getInstance().font
            val nameY: kotlin.Int = entryHeight / 2 - font.lineHeight - 1
            val softwareY: kotlin.Int = entryHeight / 2 + 1
            
            val players: kotlin.String = classiCubeServerInfo.players().toString() + "/" + classiCubeServerInfo.maxPlayers()
            val playersX: kotlin.Int = entryWidth - font.width(players) - VFPListEntry.SLOT_MARGIN
            context.text(font, players, playersX, nameY, -1)
            
                        // Long names and software names are cut off instead of overlapping the player count
            context.enableScissor(com.viaversion.viafabricplus.screen.impl.serverlist.ClassiCubeServerListScreen.ServerSlot.Companion.TEXT_OFFSET, 0, playersX - VFPListEntry.SLOT_MARGIN, entryHeight)
            context.text(font, classiCubeServerInfo.name(), com.viaversion.viafabricplus.screen.impl.serverlist.ClassiCubeServerListScreen.ServerSlot.Companion.TEXT_OFFSET, nameY, -1)
                        // The software name carries its own colors
            context.text(font, classiCubeServerInfo.software().replace('&', net.minecraft.ChatFormatting.PREFIX_CODE), com.viaversion.viafabricplus.screen.impl.serverlist.ClassiCubeServerListScreen.ServerSlot.Companion.TEXT_OFFSET, softwareY, -1)
            context.disableScissor()
        }
        companion object {
            private const val ICON_SIZE: kotlin.Int = 24
            private val TEXT_OFFSET: kotlin.Int = VFPListEntry.SLOT_MARGIN + com.viaversion.viafabricplus.screen.impl.serverlist.ClassiCubeServerListScreen.ServerSlot.Companion.ICON_SIZE + VFPListEntry.SLOT_MARGIN
            
        }}
    
    companion object {
        val TITLE: net.minecraft.network.chat.Component = net.minecraft.network.chat.Component.nullToEmpty("ClassiCube")
        private const val ROW_WIDTH: kotlin.Int = 360
        
        private var serverList: kotlin.collections.MutableList<CCServerInfo>? = null
        private var loading: kotlin.Boolean = false
        
        fun loggedIn(): kotlin.Boolean {
            return ClassiCubeAccount.get() != null
        }
        
    }}
