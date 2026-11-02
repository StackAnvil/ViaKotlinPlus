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
package com.viaversion.viafabricplus.protocoltranslator.netty

import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viaversion.platform.ViaDecodeHandler
import io.netty.channel.ChannelHandlerContext
import com.viaversion.viaversion.exception.CancelCodecException
import com.viaversion.viafabricplus.ViaFabricPlusImpl

class ViaFabricPlusDecoder (connection: UserConnection) : ViaDecodeHandler(connection) {

    @Throws(java.lang.Exception::class) override fun channelRead(ctx: ChannelHandlerContext, msg: kotlin.Any) {
        try {
            super.channelRead(ctx, msg)
        }catch (e: Throwable) {
            if (e is CancelCodecException) {
                return 
            }
            
            val mode: com.viaversion.viafabricplus.api.settings.impl.PacketTranslationError = ViaFabricPlusImpl.impl().options().ignorePacketTranslationErrors().value()
            if (mode != com.viaversion.viafabricplus.api.settings.impl.PacketTranslationError.KICK) {
                ViaFabricPlusImpl.impl().logger().error("Error occurred while decoding packet in ViaFabricPlus decoder", e)
                if (mode == com.viaversion.viafabricplus.api.settings.impl.PacketTranslationError.CANCEL_AND_NOTIFY) {
                    com.viaversion.viafabricplus.util.ChatUtil.sendPrefixedMessage(net.minecraft.network.chat.Component.translatable("translation.viafabricplus.packet_error").withStyle(net.minecraft.ChatFormatting.RED))
                }
            } else {
                throw e
            }
        }
    }
    
}
