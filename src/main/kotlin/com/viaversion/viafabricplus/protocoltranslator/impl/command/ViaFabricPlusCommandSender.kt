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
package com.viaversion.viafabricplus.protocoltranslator.impl.command

import com.viaversion.viaversion.api.command.ViaCommandSender
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import java.util.UUID

class ViaFabricPlusCommandSender (source: net.minecraft.commands.SharedSuggestionProvider) : ViaCommandSender {

    private val source: net.minecraft.commands.SharedSuggestionProvider
    
    init {
        this.source = source
    }
    
    override fun hasPermission(s: kotlin.String): kotlin.Boolean {
        return true
    }
    
    override fun sendMessage(s: kotlin.String) {
        (source as FabricClientCommandSource).sendFeedback(net.minecraft.network.chat.Component.nullToEmpty(s.replace("/viaversion", "/viafabricplus"))) // ViaVersion doesn't support changing the root command name, so we have to do it ourselves
    }
    
    override fun getUUID(): UUID {
        return (source as FabricClientCommandSource).getPlayer().getUUID()
    }
    
    override fun getName(): kotlin.String {
        return (source as FabricClientCommandSource).getPlayer().getName().getString()
    }
    
}
