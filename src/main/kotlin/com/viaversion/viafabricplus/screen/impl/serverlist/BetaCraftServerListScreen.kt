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

import com.viaversion.viafabricplus.screen.base.VFPTabbedScreen
import de.florianreuth.classic4j.model.betacraft.BCVersionCategory
import com.viaversion.viafabricplus.screen.impl.serverlist.BetaCraftServerListScreen
import com.viaversion.viafabricplus.screen.base.list.VFPListEntry
import com.viaversion.viafabricplus.screen.base.list.VFPTextEntry
import de.florianreuth.classic4j.model.betacraft.BCServerInfo
import de.florianreuth.classic4j.BetaCraftHandler
import de.florianreuth.classic4j.model.betacraft.BCServerList
import com.viaversion.viafabricplus.ViaFabricPlusImpl
import com.viaversion.viafabricplus.screen.base.VFPScreen
import com.viaversion.viafabricplus.util.network.ConnectionUtil
import java.util.HashMap

class BetaCraftServerListScreen : VFPTabbedScreen<BCVersionCategory>(BetaCraftServerListScreen.Companion.TITLE, true) {

    override fun init() {
        // Started before the tabs are built, so they can show the state of the request
        if (BetaCraftServerListScreen.Companion.serverList == null) {
            this.load()
        }
        
        super.init()
        
        this.addFooter(net.minecraft.client.gui.components.Button.builder(net.minecraft.network.chat.Component.translatable("base.viafabricplus.refresh"), net.minecraft.client.gui.components.Button.OnPress {
        `_`: net.minecraft.client.gui.components.Button -> BetaCraftServerListScreen.Companion.serverList = null
        this.load()
        this.showResponse()
        
        }).build())
    }
    
    override fun tabs(): kotlin.collections.MutableList<BCVersionCategory> {
        if (BetaCraftServerListScreen.Companion.serverList == null) {
            return java.util.List.of<BCVersionCategory>(*BCVersionCategory.entries.toTypedArray())
        }
        
                // Categories without servers would only show an empty list
        val categories: kotlin.collections.MutableList<BCVersionCategory> = java.util.Arrays.stream<BCVersionCategory>(BCVersionCategory.entries.toTypedArray())
        .filter {category: BCVersionCategory -> !BetaCraftServerListScreen.Companion.serverList!!.serversOfVersionCategory(category).isEmpty()} 
        .toList()
        return if (categories.isEmpty()) java.util.List.of<BCVersionCategory>(*BCVersionCategory.entries.toTypedArray()) else categories
    }
    
    override fun tabTitle(tab: BCVersionCategory): net.minecraft.network.chat.Component {
        val name: kotlin.String = tab.name
        return net.minecraft.network.chat.Component.nullToEmpty(name.get(0).toString() + name.substring(1).lowercase())
    }
    
    override fun entryHeight(): kotlin.Int {
        return (this.font.lineHeight + 2) * 3 // title is 2
    }
    
    override fun rowWidth(screenWidth: kotlin.Int): kotlin.Int {
        return kotlin.math.min(BetaCraftServerListScreen.Companion.ROW_WIDTH, screenWidth - 20)
    }
    
    override fun entries(tab: BCVersionCategory): kotlin.collections.MutableList<VFPListEntry> {
        if (BetaCraftServerListScreen.Companion.serverList == null) { // The servers are either still loading or the request failed
            return java.util.List.of<VFPListEntry>(VFPTextEntry(if (BetaCraftServerListScreen.Companion.loading) 
            net.minecraft.network.chat.Component.translatable("betacraft.viafabricplus.loading")
            else 
            net.minecraft.network.chat.Component.translatable("base.viafabricplus.something_went_wrong")))
        }
        
        return BetaCraftServerListScreen.Companion.serverList!!.serversOfVersionCategory(tab).stream().map<VFPListEntry> {server: BCServerInfo -> ServerSlot(server)} .toList()
    }
    
    override fun results(query: kotlin.String): kotlin.collections.MutableList<VFPListEntry> {
        if (BetaCraftServerListScreen.Companion.serverList == null) {
            return kotlin.collections.mutableListOf<VFPListEntry>()
        }
        
        return BetaCraftServerListScreen.Companion.serverList!!.servers().stream()
        .filter {server: BCServerInfo -> server.name().lowercase().contains(query) || server.gameVersion().lowercase().contains(query)} 
        .map<VFPListEntry> {server: BCServerInfo -> ServerSlot(server)} 
        .toList()
    }
    
     // Guarded against the screen being rebuilt while the request is still running, which would send it again
    private fun load() {
        if (BetaCraftServerListScreen.Companion.loading) {
            return 
        }
        
        BetaCraftServerListScreen.Companion.loading = true
        BetaCraftHandler.requestServerList(java.util.function.Consumer {
        response: BCServerList -> BetaCraftServerListScreen.Companion.serverList = response
        BetaCraftServerListScreen.Companion.loading = false
        net.minecraft.client.Minecraft.getInstance().execute(java.lang.Runnable {this.showResponse()})
        
        }, java.util.function.Consumer {
        throwable: Throwable -> BetaCraftServerListScreen.Companion.loading = false
        ViaFabricPlusImpl.impl().logger().error("Error while loading BetaCraft servers!", throwable)
        VFPScreen.showToast(net.minecraft.network.chat.Component.translatable("base.viafabricplus.something_went_wrong"))
        net.minecraft.client.Minecraft.getInstance().execute(java.lang.Runnable {this.showResponse()})
        
        })
    }
    
     // Releasing and rebuilding in the same tick keeps the slots of the previous list from being drawn with released icons
    private fun showResponse() {
        com.viaversion.viafabricplus.screen.impl.serverlist.BetaCraftServerListScreen.ServerSlot.Companion.releaseIcons()
        this.rebuildWidgets() // Also replaces the loading text of the tabs
    }
    
    class ServerSlot (server: BCServerInfo) : VFPListEntry() {
    
        private val server: BCServerInfo
        private val icon: net.minecraft.resources.Identifier
        private val name: net.minecraft.network.chat.Component
        
        init {
            this.server = server
            this.icon = com.viaversion.viafabricplus.screen.impl.serverlist.BetaCraftServerListScreen.ServerSlot.Companion.icon(server)
            this.name = net.minecraft.network.chat.Component.literal(server.name()).append(net.minecraft.network.chat.Component.literal(" [" + server.gameVersion() + "]").withStyle(net.minecraft.ChatFormatting.DARK_GRAY))
        }
        
        override fun getNarration(): net.minecraft.network.chat.Component {
            return this.name
        }
        
        override fun mappedMouseClicked() {
            ConnectionUtil.connect(server.name(), server.socket())
        }
        
        override fun mappedRender(context: net.minecraft.client.gui.GuiGraphicsExtractor, entryWidth: kotlin.Int, entryHeight: kotlin.Int) {
            context.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, this.icon, VFPListEntry.SLOT_MARGIN, (entryHeight - com.viaversion.viafabricplus.screen.impl.serverlist.BetaCraftServerListScreen.ServerSlot.Companion.ICON_SIZE) / 2, 0f, 0f, com.viaversion.viafabricplus.screen.impl.serverlist.BetaCraftServerListScreen.ServerSlot.Companion.ICON_SIZE, com.viaversion.viafabricplus.screen.impl.serverlist.BetaCraftServerListScreen.ServerSlot.Companion.ICON_SIZE, com.viaversion.viafabricplus.screen.impl.serverlist.BetaCraftServerListScreen.ServerSlot.Companion.ICON_SIZE, com.viaversion.viafabricplus.screen.impl.serverlist.BetaCraftServerListScreen.ServerSlot.Companion.ICON_SIZE)
            
            val font: net.minecraft.client.gui.Font = net.minecraft.client.Minecraft.getInstance().font
            val nameY: kotlin.Int = entryHeight / 2 - font.lineHeight - 1
            val detailsY: kotlin.Int = entryHeight / 2 + 1
            
            val players: kotlin.String = this.server.playerCount().toString() + "/" + this.server.playerLimit()
            var detailsRight: kotlin.Int = entryWidth - font.width(players) - VFPListEntry.SLOT_MARGIN
            context.text(font, players, detailsRight, nameY, -1)
            
            if (this.server.onlineMode()) {
                val onlineMode: net.minecraft.network.chat.Component = net.minecraft.network.chat.Component.translatable("base.viafabricplus.online_mode").withStyle(net.minecraft.ChatFormatting.GREEN)
                val onlineModeX: kotlin.Int = entryWidth - font.width(onlineMode) - VFPListEntry.SLOT_MARGIN
                context.text(font, onlineMode, onlineModeX, detailsY, -1)
                detailsRight = kotlin.math.min(detailsRight, onlineModeX)
            }
            
                        // Long names and addresses are cut off instead of overlapping the player count
            context.enableScissor(com.viaversion.viafabricplus.screen.impl.serverlist.BetaCraftServerListScreen.ServerSlot.Companion.TEXT_OFFSET, 0, detailsRight - VFPListEntry.SLOT_MARGIN, entryHeight)
            context.text(font, this.name, com.viaversion.viafabricplus.screen.impl.serverlist.BetaCraftServerListScreen.ServerSlot.Companion.TEXT_OFFSET, nameY, -1)
            context.text(font, this.server.socket(), com.viaversion.viafabricplus.screen.impl.serverlist.BetaCraftServerListScreen.ServerSlot.Companion.TEXT_OFFSET, detailsY, com.viaversion.viafabricplus.screen.impl.serverlist.BetaCraftServerListScreen.ServerSlot.Companion.DETAILS_COLOR)
            context.disableScissor()
        }
        companion object {
            private const val ICON_SIZE: kotlin.Int = 24
            private val TEXT_OFFSET: kotlin.Int = VFPListEntry.SLOT_MARGIN + com.viaversion.viafabricplus.screen.impl.serverlist.BetaCraftServerListScreen.ServerSlot.Companion.ICON_SIZE + VFPListEntry.SLOT_MARGIN
            private const val DETAILS_COLOR: kotlin.Int = -0x555556
            
             // The icons are sent as raw PNG data, so they have to be uploaded as textures at once
            private val ICONS: kotlin.collections.MutableMap<kotlin.String, net.minecraft.resources.Identifier> = HashMap<kotlin.String, net.minecraft.resources.Identifier>()
            
            private fun icon(server: BCServerInfo): net.minecraft.resources.Identifier {
                return com.viaversion.viafabricplus.screen.impl.serverlist.BetaCraftServerListScreen.ServerSlot.Companion.ICONS.computeIfAbsent(server.socket()) {`_`: kotlin.String -> com.viaversion.viafabricplus.screen.impl.serverlist.BetaCraftServerListScreen.ServerSlot.Companion.uploadIcon(server)} 
            }
            
             // The servers of a refreshed list may carry changed icons or be gone entirely, so the old textures are dropped
            fun releaseIcons() {
                for (texture in com.viaversion.viafabricplus.screen.impl.serverlist.BetaCraftServerListScreen.ServerSlot.Companion.ICONS.values) {
                    if (texture !== net.minecraft.client.gui.screens.FaviconTexture.MISSING_LOCATION) { // Only the icons we uploaded ourselves are ours to free
                        net.minecraft.client.Minecraft.getInstance().getTextureManager().release(texture)
                    }
                }
                com.viaversion.viafabricplus.screen.impl.serverlist.BetaCraftServerListScreen.ServerSlot.Companion.ICONS.clear()
            }
            
            private fun uploadIcon(server: BCServerInfo): net.minecraft.resources.Identifier {
                val icon: java.util.Optional<ByteArray> = server.icon()
                if (icon.isEmpty()) {
                    return net.minecraft.client.gui.screens.FaviconTexture.MISSING_LOCATION
                }
                
                            // The icons are not limited to the 64x64 of vanilla server icons, so FaviconTexture rejects them
                val texture: net.minecraft.resources.Identifier = net.minecraft.resources.Identifier.fromNamespaceAndPath("viafabricplus", "betacraft/" + net.minecraft.util.Util.sanitizeName(server.socket(), net.minecraft.CharPredicate {c: Char -> net.minecraft.resources.Identifier.validPathChar(c)}) + "/icon")
                var image: com.mojang.blaze3d.platform.NativeImage? = null
                try {
                    image = com.mojang.blaze3d.platform.NativeImage.read(icon.get())
                    net.minecraft.client.Minecraft.getInstance().getTextureManager().register(texture, net.minecraft.client.renderer.texture.DynamicTexture(java.util.function.Supplier {"BetaCraft server icon " + server.socket()}, image))
                    return texture
                }catch (throwable: Throwable) {
                    if (image != null) {
                        image.close()
                    }
                    
                    ViaFabricPlusImpl.impl().logger().warn("Failed to read the icon of the BetaCraft server {}", server.name(), throwable)
                    return net.minecraft.client.gui.screens.FaviconTexture.MISSING_LOCATION
                }
            }
            
        }}
    
    companion object {
        val TITLE: net.minecraft.network.chat.Component = net.minecraft.network.chat.Component.nullToEmpty("BetaCraft")
        private const val ROW_WIDTH: kotlin.Int = 360
        
        private var serverList: BCServerList? = null
        private var loading: kotlin.Boolean = false
        
    }}
