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
package com.viaversion.viafabricplus.screen.impl

import com.viaversion.viafabricplus.screen.impl.protocol.AbstractProtocolSelectionScreen
import com.viaversion.viafabricplus.screen.ScreensImpl
import com.viaversion.viafabricplus.ViaFabricPlusImpl
import com.viaversion.viafabricplus.screen.impl.serverlist.ServerListsScreen

class ViaFabricPlusScreen : AbstractProtocolSelectionScreen(net.minecraft.network.chat.Component.nullToEmpty("ViaFabricPlus"), true) {

    override fun init() {
        super.init()
        
        val screens: ScreensImpl = ViaFabricPlusImpl.impl().screens()
        
                // The server lists connect to a server, which is not possible while already being connected to one
        val disconnected: kotlin.Boolean = net.minecraft.client.Minecraft.getInstance().getConnection() == null
        
        val serverLists: net.minecraft.client.gui.components.Button = net.minecraft.client.gui.components.Button.builder(ServerListsScreen.TITLE, net.minecraft.client.gui.components.Button.OnPress {`_`: net.minecraft.client.gui.components.Button -> screens.serverListsScreen().open(this)}).build()
        serverLists.active = disconnected
        
        this.addFooter(serverLists, 
        net.minecraft.client.gui.components.Button.builder(net.minecraft.network.chat.Component.translatable("base.viafabricplus.settings"), net.minecraft.client.gui.components.Button.OnPress {`_`: net.minecraft.client.gui.components.Button -> screens.settingsScreen().open(this)}).build(), 
        net.minecraft.client.gui.components.Button.builder(net.minecraft.network.chat.Component.translatable("report.viafabricplus.button"), net.minecraft.client.gui.components.Button.OnPress {`_`: net.minecraft.client.gui.components.Button -> screens.reportIssuesScreen().open(this)}).build())
    }
    
    override fun select(version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion) {
        com.viaversion.viafabricplus.ViaFabricPlus.api().setTargetVersion(version)
    }
    
    override fun selected(version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion): kotlin.Boolean {
        return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion() == version
    }
    
    override fun selectable(): kotlin.Boolean {
        // Setting the target version while connected to a server is not allowed as this will
        // literally break our code away.
        return net.minecraft.client.Minecraft.getInstance().getConnection() == null
    }
    
}
