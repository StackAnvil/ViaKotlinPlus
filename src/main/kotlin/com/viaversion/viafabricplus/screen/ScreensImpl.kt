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
package com.viaversion.viafabricplus.screen

import com.viaversion.viafabricplus.screen.impl.ViaFabricPlusScreen
import com.viaversion.viafabricplus.screen.impl.SettingsScreen
import com.viaversion.viafabricplus.screen.impl.ReportIssuesScreen
import com.viaversion.viafabricplus.screen.impl.serverlist.ServerListsScreen
import com.viaversion.viafabricplus.screen.impl.serverlist.ClassiCubeLoginScreen
import com.viaversion.viafabricplus.screen.impl.serverlist.ClassiCubeMFAScreen
import com.viaversion.viafabricplus.screen.impl.serverlist.ClassiCubeServerListScreen
import com.viaversion.viafabricplus.screen.impl.serverlist.BetaCraftServerListScreen
import com.viaversion.viafabricplus.features.c0_28.GridItemSelectionScreen

class ScreensImpl : com.viaversion.viafabricplus.api.screen.Screens {

    private val viaFabricPlusScreen: ViaFabricPlusScreen = ViaFabricPlusScreen()
    private val settingsScreen: SettingsScreen = SettingsScreen()
    private val reportIssuesScreen: ReportIssuesScreen = ReportIssuesScreen()
    private val serverListsScreen: ServerListsScreen = ServerListsScreen()
    private val classiCubeLoginScreen: ClassiCubeLoginScreen = ClassiCubeLoginScreen()
    private val classiCubeMFAScreen: ClassiCubeMFAScreen = ClassiCubeMFAScreen()
    private val classiCubeServerListScreen: ClassiCubeServerListScreen = ClassiCubeServerListScreen()
    private val betaCraftServerListScreen: BetaCraftServerListScreen = BetaCraftServerListScreen()
    private val gridItemSelectionScreen: GridItemSelectionScreen = GridItemSelectionScreen()
    
    override fun openViaFabricPlusScreen(parent: net.minecraft.client.gui.screens.Screen) {
        this.viaFabricPlusScreen.open(parent)
    }
    
    override fun openSettingsScreen(parent: net.minecraft.client.gui.screens.Screen) {
        this.settingsScreen.open(parent)
    }
    
    override fun openClassiCubeServerListScreen(parent: net.minecraft.client.gui.screens.Screen) {
        if (ClassiCubeServerListScreen.loggedIn()) {
            this.classiCubeServerListScreen.open(parent)
        } else {
            this.classiCubeLoginScreen.open(parent)
        }
    }
    
    override fun openBetaCraftServerListScreen(parent: net.minecraft.client.gui.screens.Screen) {
        this.betaCraftServerListScreen.open(parent)
    }
    
    fun viaFabricPlusScreen(): ViaFabricPlusScreen {
        return this.viaFabricPlusScreen
    }
    
    fun settingsScreen(): SettingsScreen {
        return this.settingsScreen
    }
    
    fun reportIssuesScreen(): ReportIssuesScreen {
        return this.reportIssuesScreen
    }
    
    fun serverListsScreen(): ServerListsScreen {
        return this.serverListsScreen
    }
    
    fun classiCubeLoginScreen(): ClassiCubeLoginScreen {
        return this.classiCubeLoginScreen
    }
    
    fun classiCubeMFAScreen(): ClassiCubeMFAScreen {
        return this.classiCubeMFAScreen
    }
    
    fun classiCubeServerListScreen(): ClassiCubeServerListScreen {
        return this.classiCubeServerListScreen
    }
    
    fun betaCraftServerListScreen(): BetaCraftServerListScreen {
        return this.betaCraftServerListScreen
    }
    
    fun gridItemSelectionScreen(): GridItemSelectionScreen {
        return this.gridItemSelectionScreen
    }
    
}
