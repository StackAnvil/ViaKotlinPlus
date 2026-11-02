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
package com.viaversion.viafabricplus.injection.mixin.core.integration

import org.spongepowered.asm.mixin.Mixin
import io.netty.channel.SimpleChannelInboundHandler
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import io.netty.channel.ChannelHandlerContext
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.viaversion.viafabricplus.ViaFabricPlusImpl
import java.net.SocketException
import com.viaversion.viaversion.platform.ViaChannelInitializer

@Mixin(net.minecraft.network.Connection::class) 
abstract class MixinConnection : SimpleChannelInboundHandler<net.minecraft.network.protocol.Packet<*>>() {

    @Inject(method = ["exceptionCaught"], at = [At("HEAD")]) private fun printNetworkingErrors(ctx: ChannelHandlerContext, cause: Throwable, ci: CallbackInfo) {
        if (ViaFabricPlusImpl.impl().advanced().printNetworkingErrorsToLogs().isActive()) {
            if (cause is SocketException || cause is java.net.ConnectException) {
                // Thrown when server is not reachable
                return 
            }
            ViaFabricPlusImpl.impl().logger().error("An exception occurred while handling a packet", cause)
        }
    }
    
    @Throws(java.lang.Exception::class) override fun userEventTriggered(ctx: ChannelHandlerContext, evt: kotlin.Any) {
        // Krypton mixins are badly implemented and override parts of the network pipeline. They rejected
        // to make the mixins mod compatible and instead added this bad API which mods now have to use for no reason.
        if (evt.javaClass.getName() == "me.steinborn.krypton.mod.shared.misc.KryptonPipelineEvent") {
            if (evt.toString() == "COMPRESSION_ENABLED") {
                ViaChannelInitializer.reorderPipeline(ctx.pipeline(), net.minecraft.network.HandlerNames.COMPRESS, net.minecraft.network.HandlerNames.DECOMPRESS)
                ViaFabricPlusImpl.impl().logger().warn("ViaFabricPlus has detected that the Krypton mod is installed. Please note that Krypton is mostly snake oil on the client side, and it is not recommended to use it.")
                return 
            }
        }
        super.userEventTriggered(ctx, evt)
    }
    
}
