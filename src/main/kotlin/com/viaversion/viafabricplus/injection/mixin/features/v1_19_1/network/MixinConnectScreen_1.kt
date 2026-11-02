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
package com.viaversion.viafabricplus.injection.mixin.features.v1_19_1.network

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viafabricplus.injection.access.core.IConnection
import java.security.PrivateKey
import java.util.UUID
import com.viaversion.viaversion.api.minecraft.signature.storage.ChatSession1_19_1
import com.viaversion.viaversion.api.minecraft.ProfileKey
import com.viaversion.viafabricplus.injection.access.v1_19_1.IProfilePublicKey_Data
import com.viaversion.viaversion.api.minecraft.signature.storage.ChatSession1_19_0
import com.viaversion.viafabricplus.ViaFabricPlusImpl

@Mixin(targets = ["net.minecraft.client.gui.screens.ConnectScreen$1"]) 
abstract class MixinConnectScreen_1 {

    @org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField var `val$minecraft`: net.minecraft.client.Minecraft = uninitialized()
    @Inject(method = ["run"], at = [At(value = "INVOKE", target = "Lio/netty/channel/ChannelFuture;syncUninterruptibly()Lio/netty/channel/ChannelFuture;", remap = false, shift = At.Shift.AFTER)]) private fun setupChatSessions(ci: CallbackInfo, @com.llamalad7.mixinextras.sugar.Local clientConnection: net.minecraft.network.Connection) {
        val connection: UserConnection = (clientConnection as IConnection).`viaFabricPlus$getUserConnection`()
        
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().betweenInclusive(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_19, com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_19_1)) {
            val keyPair: net.minecraft.world.entity.player.ProfileKeyPair = net.minecraft.client.Minecraft.getInstance().getProfileKeyPairManager().prepareKeyPair().join().orElse(null)
            if (keyPair != null) {
                val publicKeyData: net.minecraft.world.entity.player.ProfilePublicKey.Data = keyPair.publicKey().data()
                val privateKey: PrivateKey = keyPair.privateKey()
                val expiresAt: kotlin.Long = publicKeyData.expiresAt().toEpochMilli()
                val publicKey: ByteArray = publicKeyData.key().getEncoded()
                val uuid: UUID = this.`val$minecraft`.getUser().getProfileId()
                
                connection.put(ChatSession1_19_1(uuid, privateKey, ProfileKey(expiresAt, publicKey, publicKeyData.keySignature())))
                if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion() === com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_19) {
                    val legacyKeySignature: ByteArray = (publicKeyData as kotlin.Any as IProfilePublicKey_Data).`viafabricplus$getLegacyPublicKeySignature`()
                    if (legacyKeySignature != null) {
                        connection.put(ChatSession1_19_0(uuid, privateKey, ProfileKey(expiresAt, publicKey, legacyKeySignature)))
                    }
                }
            } else {
                ViaFabricPlusImpl.impl().logger().error("Could not get public key signature. Joining servers with enforce-secure-profiles enabled will not work!")
            }
        }
    }
    
}
