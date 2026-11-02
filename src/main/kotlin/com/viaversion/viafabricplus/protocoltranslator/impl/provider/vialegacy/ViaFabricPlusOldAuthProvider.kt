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
package com.viaversion.viafabricplus.protocoltranslator.impl.provider.vialegacy

import net.raphimc.vialegacy.protocol.release.r1_2_4_5tor1_3_1_2.provider.OldAuthProvider
import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslationImpl
import com.viaversion.viafabricplus.ViaFabricPlusImpl

class ViaFabricPlusOldAuthProvider : OldAuthProvider() {

    override fun sendAuthRequest(connection: UserConnection, serverId: kotlin.String) {
        try {
            val client: net.minecraft.client.Minecraft = net.minecraft.client.Minecraft.getInstance()
            client.services().sessionService().joinServer(client.getUser().getProfileId(), client.getUser().getAccessToken(), serverId)
        }catch (e: java.lang.Exception) {
            connection.getChannel()!!.attr<net.minecraft.network.Connection>(ProtocolTranslationImpl.MINECRAFT_CONNECTION_ATTRIBUTE_KEY).get().disconnect(com.viaversion.viafabricplus.util.ChatUtil.prefixText(net.minecraft.network.chat.Component.translatable("betacraft.viafabricplus.failed_to_verify_session")))
            ViaFabricPlusImpl.impl().logger().error("Error occurred while calling join server to verify session", e)
        }
    }
    
}
