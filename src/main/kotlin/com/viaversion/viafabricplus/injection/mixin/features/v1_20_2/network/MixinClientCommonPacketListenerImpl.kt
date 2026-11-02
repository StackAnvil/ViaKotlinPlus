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
package com.viaversion.viafabricplus.injection.mixin.features.v1_20_2.network

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl::class) 
abstract class MixinClientCommonPacketListenerImpl {

    @org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField var connection: net.minecraft.network.Connection = uninitialized()
    @org.spongepowered.asm.mixin.Shadow abstract fun send(packet: net.minecraft.network.protocol.Packet<*>)
    
    @Inject(method = ["handleResourcePackPush"], at = [At("HEAD")], cancellable = true) private fun validateUrlInNetworkThread(packet: net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket, ci: CallbackInfo) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20_2)) {
            if (com.viaversion.viafabricplus.injection.mixin.features.v1_20_2.network.MixinClientCommonPacketListenerImpl.Companion.parseResourcePackUrl(packet.url()) == null) {
                this.connection.send(net.minecraft.network.protocol.common.ServerboundResourcePackPacket(packet.id(), net.minecraft.network.protocol.common.ServerboundResourcePackPacket.Action.INVALID_URL))
                ci.cancel()
            }
        }
    }
    
    private companion object {
        @JvmStatic @org.spongepowered.asm.mixin.Shadow fun parseResourcePackUrl(urlString: kotlin.String): java.net.URL {
            return uninitialized()
        }
        
    }}
