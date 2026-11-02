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

import com.viaversion.viafabricplus.screen.base.VFPPopup
import com.viaversion.viafabricplus.screen.impl.ReportIssuesScreen
import java.util.LinkedHashMap
import com.viaversion.viafabricplus.screen.base.VFPScreen
import com.viaversion.viaversion.util.DumpUtil
import com.viaversion.viafabricplus.ViaFabricPlusImpl

class ReportIssuesScreen : VFPPopup(net.minecraft.network.chat.Component.translatable("screen.viafabricplus.report_issues"), net.minecraft.client.gui.components.Button.BIG_WIDTH + 2 * ReportIssuesScreen.Companion.BUTTON_MARGIN, 4 * (net.minecraft.client.gui.components.Button.DEFAULT_HEIGHT + ReportIssuesScreen.Companion.BUTTON_MARGIN) + ReportIssuesScreen.Companion.BUTTON_MARGIN) {

    private val actions: kotlin.collections.MutableMap<kotlin.String, java.lang.Runnable> = LinkedHashMap<kotlin.String, java.lang.Runnable>()
    
    init {
        actions.put("report.viafabricplus.bug_report", java.lang.Runnable {
        com.mojang.blaze3d.Blaze3D.openUri(java.net.URI.create("https://github.com/ViaVersion/ViaFabricPlus/issues/new?assignees=&labels=bug&projects=&template=bug_report.yml"))
        VFPScreen.showToast(net.minecraft.network.chat.Component.translatable("report.viafabricplus.bug_report.response"))
        
        })
        actions.put("report.viafabricplus.feature_request", java.lang.Runnable {
        com.mojang.blaze3d.Blaze3D.openUri(java.net.URI.create("https://github.com/ViaVersion/ViaFabricPlus/issues/new?assignees=&labels=enhancement&projects=&template=feature_request.yml"))
        VFPScreen.showToast(net.minecraft.network.chat.Component.translatable("report.viafabricplus.feature_request.response"))
        
        })
        actions.put("report.viafabricplus.create_via_dump", java.lang.Runnable {DumpUtil.postDump(minecraft.getUser().getProfileId()).whenComplete(java.util.function.BiConsumer {
        s: kotlin.String, throwable: Throwable -> if (throwable != null) {
            VFPScreen.showToast(net.minecraft.network.chat.Component.translatable("report.viafabricplus.create_via_dump.failed"))
            ViaFabricPlusImpl.impl().logger().error("Failed to create a dump", throwable)
            return@BiConsumer 
        }
        VFPScreen.showToast(net.minecraft.network.chat.Component.translatable("report.viafabricplus.create_via_dump.success"))
        minecraft.keyboardHandler.setClipboard(s)
        
        })})
        actions.put("report.viafabricplus.open_logs", java.lang.Runnable {
        com.mojang.blaze3d.Blaze3D.openPath(minecraft.gameDirectory.toPath().resolve("logs"))
        VFPScreen.showToast(net.minecraft.network.chat.Component.translatable("report.viafabricplus.open_logs.response"))
        
        })
    }
    
    override fun initBody(body: net.minecraft.client.gui.navigation.ScreenRectangle) {
        var i: kotlin.Int = 0
        for (entry in actions.entries) {
            this.addRenderableWidget<net.minecraft.client.gui.components.Button>(net.minecraft.client.gui.components.Button.builder(net.minecraft.network.chat.Component.translatable(entry.key), net.minecraft.client.gui.components.Button.OnPress {`_`: net.minecraft.client.gui.components.Button -> entry.value.run()})
            .pos(body.left() + (body.width() - net.minecraft.client.gui.components.Button.BIG_WIDTH) / 2, body.top() + ReportIssuesScreen.Companion.BUTTON_MARGIN + i * (net.minecraft.client.gui.components.Button.DEFAULT_HEIGHT + ReportIssuesScreen.Companion.BUTTON_MARGIN))
            .size(net.minecraft.client.gui.components.Button.BIG_WIDTH, net.minecraft.client.gui.components.Button.DEFAULT_HEIGHT).build())
            i++
        }
    }
    
    companion object {
        private const val BUTTON_MARGIN: kotlin.Int = 3
        
    }}
