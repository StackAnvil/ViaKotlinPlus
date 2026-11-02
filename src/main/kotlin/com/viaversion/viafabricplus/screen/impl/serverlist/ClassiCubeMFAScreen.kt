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
import com.viaversion.viafabricplus.features.global.ClassiCubeAccount
import de.florianreuth.classic4j.api.LoginProcessHandler
import de.florianreuth.classic4j.model.classicube.account.CCAccount
import com.viaversion.viafabricplus.ViaFabricPlusImpl

class ClassiCubeMFAScreen : VFPScreen(net.minecraft.network.chat.Component.translatable("screen.viafabricplus.classicube_mfa"), true) {

    private lateinit var mfaField: net.minecraft.client.gui.components.EditBox
    
    override fun init() {
        super.init()
        this.addRenderableWidget<net.minecraft.client.gui.components.EditBox>(net.minecraft.client.gui.components.EditBox(font, width / 2 - 150, 70 + 10, 300, 20, net.minecraft.network.chat.Component.empty()).also { mfaField = it })
        
        mfaField.setHint(net.minecraft.network.chat.Component.nullToEmpty("MFA"))
        
        this.addRenderableWidget<net.minecraft.client.gui.components.Button>(net.minecraft.client.gui.components.Button.builder(net.minecraft.network.chat.Component.translatable("base.viafabricplus.login"), net.minecraft.client.gui.components.Button.OnPress {`_`: net.minecraft.client.gui.components.Button -> ClassiCubeAccount.authenticate(mfaField.getValue(), object : LoginProcessHandler {
            override fun handleMfa(account: CCAccount) {
                    // Not implemented in this case
            }
            
            override fun handleSuccessfulLogin(account: CCAccount) {
                ViaFabricPlusImpl.impl().screens().classiCubeServerListScreen().open(prevScreen)
            }
            
            override fun handleException(throwable: Throwable) {
                ViaFabricPlusImpl.impl().logger().error("Error while logging in to ClassiCube!", throwable)
                VFPScreen.showToast(net.minecraft.network.chat.Component.nullToEmpty(throwable.message))
            }
        })
        }).pos(width / 2 - net.minecraft.client.gui.components.Button.DEFAULT_WIDTH / 2, mfaField.getY() + (20 * 4) + 5).size(net.minecraft.client.gui.components.Button.DEFAULT_WIDTH, net.minecraft.client.gui.components.Button.DEFAULT_HEIGHT).build())
    }
    
    override fun onClose() {
        // The user wasn't logged in when opening this screen, so they canceled the login process, and the account can safely be unset
        ClassiCubeAccount.set(null)
        super.onClose()
    }
    
    override fun extractRenderState(graphics: net.minecraft.client.gui.GuiGraphicsExtractor, mouseX: kotlin.Int, mouseY: kotlin.Int, a: kotlin.Float) {
        super.extractRenderState(graphics, mouseX, mouseY, a)
        this.renderScreenTitle(graphics)
    }
    
}
