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
package com.viaversion.viafabricplus.util

import com.viaversion.viafabricplus.screen.base.VFPScreen

object ChatUtil {
    val PREFIX: kotlin.String = net.minecraft.ChatFormatting.WHITE.toString() + "[" + net.minecraft.ChatFormatting.BLUE + "ViaFabricPlus" + net.minecraft.ChatFormatting.WHITE + "]"
    val PREFIX_TEXT: net.minecraft.network.chat.Component = net.minecraft.network.chat.Component.literal("[").withStyle(net.minecraft.ChatFormatting.WHITE).append(net.minecraft.network.chat.Component.literal("ViaFabricPlus").withColor(VFPScreen.ACCENT_COLOR)).append("]")
    
    fun prefixText(message: kotlin.String): net.minecraft.network.chat.Component {
        return com.viaversion.viafabricplus.util.ChatUtil.prefixText(net.minecraft.network.chat.Component.nullToEmpty(message))
    }
    
    fun prefixText(message: net.minecraft.network.chat.Component): net.minecraft.network.chat.Component {
        return net.minecraft.network.chat.Component.empty().append(com.viaversion.viafabricplus.util.ChatUtil.PREFIX_TEXT).append(" ").append(message)
    }
    
    fun sendPrefixedMessage(message: net.minecraft.network.chat.Component) {
        if (net.minecraft.client.Minecraft.getInstance().isSameThread()) {
            net.minecraft.client.Minecraft.getInstance().gui.hud.getChat().addClientSystemMessage(com.viaversion.viafabricplus.util.ChatUtil.prefixText(message))
        } else {
            net.minecraft.client.Minecraft.getInstance().execute(java.lang.Runnable {com.viaversion.viafabricplus.util.ChatUtil.sendPrefixedMessage(message)})
        }
    }
    
}
