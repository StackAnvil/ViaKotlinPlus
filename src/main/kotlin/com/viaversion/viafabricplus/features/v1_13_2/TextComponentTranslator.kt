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
package com.viaversion.viafabricplus.features.v1_13_2

import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslationImpl
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper
import com.viaversion.viaversion.protocols.v1_13_2to1_14.packet.ClientboundPackets1_14
import com.viaversion.viafabricplus.ViaFabricPlusImpl

object TextComponentTranslator {
    private val DUMMY_USER_CONNECTION: UserConnection = com.viaversion.viafabricplus.ViaFabricPlus.api().conversions().createDummyUserConnection(ProtocolTranslationImpl.NATIVE_VERSION, com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_14)
    
     /**
 * Converts a ViaVersion 1.14 text component to a native text component, both in JSON format
 * 
 * @param component The ViaVersion 1.14 text component
 * @return The ViaVersion text component for the native version
 */
    fun via1_14toViaLatest(component: com.viaversion.viaversion.libs.gson.JsonElement): com.viaversion.nbt.tag.Tag? {
        try {
            val openScreen: PacketWrapper = PacketWrapper.create(ClientboundPackets1_14.OPEN_SCREEN, com.viaversion.viafabricplus.features.v1_13_2.TextComponentTranslator.DUMMY_USER_CONNECTION)
            openScreen.write<kotlin.Int>(com.viaversion.viaversion.api.type.Types.VAR_INT, 1) // window id
            openScreen.write<kotlin.Int>(com.viaversion.viaversion.api.type.Types.VAR_INT, 0) // type id
            openScreen.write<com.viaversion.viaversion.libs.gson.JsonElement>(com.viaversion.viaversion.api.type.Types.COMPONENT, component) // title
            
            openScreen.resetReader()
            openScreen.user().getProtocolInfo().getPipeline().transform(com.viaversion.viaversion.api.protocol.packet.Direction.CLIENTBOUND, com.viaversion.viaversion.api.protocol.packet.State.PLAY, openScreen)
            
            openScreen.read<kotlin.Int>(com.viaversion.viaversion.api.type.Types.VAR_INT) // window id
            openScreen.read<kotlin.Int>(com.viaversion.viaversion.api.type.Types.VAR_INT) // type id
            return openScreen.read<com.viaversion.nbt.tag.Tag>(com.viaversion.viaversion.api.type.Types.TAG) // title
        }catch (t: Throwable) {
            ViaFabricPlusImpl.impl().logger().error("Error converting ViaVersion 1.14 text component to native text component", t)
            return null
        }
    }
    
}
