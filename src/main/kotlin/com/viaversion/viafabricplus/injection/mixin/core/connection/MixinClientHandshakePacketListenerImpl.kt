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
package com.viaversion.viafabricplus.injection.mixin.core.connection

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import com.viaversion.viafabricplus.injection.access.core.IConnection
import net.raphimc.vialegacy.api.LegacyProtocolVersion
import net.raphimc.vialegacy.protocol.release.r1_6_4tor1_7_2_5.storage.ProtocolMetadataStorage

@Mixin(net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl::class) 
abstract class MixinClientHandshakePacketListenerImpl {

    @org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField val connection: net.minecraft.network.Connection = uninitialized()
    @Inject(method = ["authenticateServer"], at = [At("HEAD")], cancellable = true) private fun onlyVerifySessionInOnlineMode(digest: kotlin.String, cir: CallbackInfoReturnable<net.minecraft.network.chat.Component>) {
        val mixinClientConnection: IConnection = connection as IConnection
        if (mixinClientConnection.`viaFabricPlus$getTargetVersion`().olderThanOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
            // We are in the 1.7 -> 1.6 protocol, so we need to skip the joinServer call
            // if the server is in offline mode, due to the packet changes <-> networking changes
            // Minecraft's networking code is bad for us.
            if (!mixinClientConnection.`viaFabricPlus$getUserConnection`().get<ProtocolMetadataStorage>(ProtocolMetadataStorage::class.java)!!.isAuthenticate()) {
                cir.setReturnValue(null)
            }
        }
    }
    
}
