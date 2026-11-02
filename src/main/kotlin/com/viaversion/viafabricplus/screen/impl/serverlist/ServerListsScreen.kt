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

import com.viaversion.viafabricplus.screen.base.VFPPopup
import com.viaversion.viafabricplus.screen.impl.serverlist.ServerListsScreen
import com.viaversion.viafabricplus.screen.ScreensImpl
import com.viaversion.viafabricplus.ViaFabricPlusImpl
import com.viaversion.viafabricplus.screen.impl.serverlist.ClassiCubeServerListScreen
import com.viaversion.viafabricplus.screen.impl.serverlist.BetaCraftServerListScreen

class ServerListsScreen : VFPPopup(ServerListsScreen.Companion.TITLE, net.minecraft.client.gui.components.Button.BIG_WIDTH + 2 * ServerListsScreen.Companion.BUTTON_MARGIN, 2 * (net.minecraft.client.gui.components.Button.DEFAULT_HEIGHT + ServerListsScreen.Companion.BUTTON_MARGIN) + ServerListsScreen.Companion.BUTTON_MARGIN) {

    override fun initBody(body: net.minecraft.client.gui.navigation.ScreenRectangle) {
        val screens: ScreensImpl = ViaFabricPlusImpl.impl().screens()
        val x: kotlin.Int = body.left() + (body.width() - net.minecraft.client.gui.components.Button.BIG_WIDTH) / 2
        
                // Opened with the parent of the popup, so going back from a server list skips the popup
        this.addRenderableWidget<net.minecraft.client.gui.components.Button>(net.minecraft.client.gui.components.Button.builder(ClassiCubeServerListScreen.TITLE, net.minecraft.client.gui.components.Button.OnPress {`_`: net.minecraft.client.gui.components.Button -> screens.openClassiCubeServerListScreen(this.prevScreen!!)})
        .pos(x, body.top() + ServerListsScreen.Companion.BUTTON_MARGIN).size(net.minecraft.client.gui.components.Button.BIG_WIDTH, net.minecraft.client.gui.components.Button.DEFAULT_HEIGHT).build())
        this.addRenderableWidget<net.minecraft.client.gui.components.Button>(net.minecraft.client.gui.components.Button.builder(BetaCraftServerListScreen.TITLE, net.minecraft.client.gui.components.Button.OnPress {`_`: net.minecraft.client.gui.components.Button -> screens.openBetaCraftServerListScreen(this.prevScreen!!)})
        .pos(x, body.top() + ServerListsScreen.Companion.BUTTON_MARGIN + net.minecraft.client.gui.components.Button.DEFAULT_HEIGHT + ServerListsScreen.Companion.BUTTON_MARGIN).size(net.minecraft.client.gui.components.Button.BIG_WIDTH, net.minecraft.client.gui.components.Button.DEFAULT_HEIGHT).build())
    }
    
    companion object {
        val TITLE: net.minecraft.network.chat.Component = net.minecraft.network.chat.Component.translatable("screen.viafabricplus.server_lists")
        
        private const val BUTTON_MARGIN: kotlin.Int = 3
        
    }}
