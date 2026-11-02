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
package com.viaversion.viafabricplus.injection.mixin.core.integration.sync_tasks

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.viaversion.viafabricplus.util.network.SyncTasks

@Mixin(value = [net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl::class], priority = 1) 
abstract class MixinClientCommonPacketListenerImpl {

    @Inject(method = ["handleCustomPayload(Lnet/minecraft/network/protocol/common/ClientboundCustomPayloadPacket;)V"], at = [At("HEAD")], cancellable = true) private fun handleSyncTask(packet: net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket, ci: CallbackInfo) {
        val payload = packet.payload()
        if (payload is com.viaversion.viafabricplus.util.network.DataCustomPayload) {
            SyncTasks.handleSyncTask(payload.buf)
            ci.cancel() // Cancel the packet, so it doesn't get processed by the client
        }
    }
    
}
