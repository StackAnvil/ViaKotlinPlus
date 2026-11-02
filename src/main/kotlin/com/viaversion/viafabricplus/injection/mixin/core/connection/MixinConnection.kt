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
import io.netty.channel.SimpleChannelInboundHandler
import com.viaversion.viafabricplus.injection.access.core.IConnection
import io.netty.channel.ChannelHandlerContext
import com.viaversion.viaversion.api.connection.UserConnection
import javax.crypto.Cipher
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.viaversion.viaversion.platform.ViaChannelInitializer
import net.raphimc.vialegacy.api.LegacyProtocolVersion
import net.raphimc.vialegacy.netty.PreNettyLengthRemover
import net.raphimc.vialegacy.netty.PreNettyLengthPrepender
import java.net.InetSocketAddress
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import com.viaversion.viafabricplus.injection.access.core.ILocalSampleLogger
import io.netty.channel.ChannelFuture
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslationImpl

@Mixin(net.minecraft.network.Connection::class) 
abstract class MixinConnection : SimpleChannelInboundHandler<net.minecraft.network.protocol.Packet<*>>(), IConnection {

    @org.spongepowered.asm.mixin.Shadow 
    @JvmField var channel: io.netty.channel.Channel = uninitialized()
    @org.spongepowered.asm.mixin.Shadow @Throws(java.lang.Exception::class) abstract override fun channelActive(ctx: ChannelHandlerContext)
    
    @org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$userConnection`: UserConnection = uninitialized()
    @org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$serverVersion`: com.viaversion.viaversion.api.protocol.version.ProtocolVersion = uninitialized()
    @org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$decryptionCipher`: Cipher = uninitialized()
    @Inject(method = ["setupCompression"], at = [At("RETURN")]) private fun reorderCompression(threshold: kotlin.Int, validateDecompressed: kotlin.Boolean, ci: CallbackInfo) {
        // Compression enabled, and elements put into pipeline, move via handlers
        ViaChannelInitializer.reorderPipeline(channel.pipeline(), net.minecraft.network.HandlerNames.COMPRESS, net.minecraft.network.HandlerNames.DECOMPRESS)
    }
    
    @Inject(method = ["setEncryptionKey"], at = [At("HEAD")], cancellable = true) private fun storeDecryptionCipher(decryptCipher: Cipher, encryptCipher: Cipher, ci: CallbackInfo) {
        if (this.`viaFabricPlus$serverVersion` != null /* This happens when opening a lan server and people are joining */ && this.`viaFabricPlus$serverVersion`.olderThanOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
            // Minecraft's encryption code is bad for us, we need to reorder the pipeline
            ci.cancel()
            
                        // Minecraft 1.6.4 supports split encryption/ decryption, which means the server can only enable one side of the encryption
            // So we only enable the encryption side and later enable the decryption side if the 1.7 -> 1.6 protocol
            // tells us to do, therefore, we need to store the cipher instance.
            this.`viaFabricPlus$decryptionCipher` = decryptCipher
            
                        // Enabling the encryption side
            kotlin.checkNotNull(encryptCipher) {"Encryption cipher is null"} 
            
            this.channel.pipeline().addBefore(PreNettyLengthRemover.NAME, net.minecraft.network.HandlerNames.ENCRYPT, net.minecraft.network.CipherEncoder(encryptCipher))
        }
    }
    
    override fun `viaFabricPlus$setupPreNettyDecryption`() {
        kotlin.checkNotNull(this.`viaFabricPlus$decryptionCipher`) {"Decryption cipher is null"} 
        
                // Enabling the decryption side for 1.6.4 if the 1.7 -> 1.6 protocol tells us to do
        this.channel.pipeline().addBefore(PreNettyLengthPrepender.NAME, net.minecraft.network.HandlerNames.DECRYPT, net.minecraft.network.CipherDecoder(this.`viaFabricPlus$decryptionCipher`))
    }
    
    override fun `viaFabricPlus$getUserConnection`(): UserConnection {
        return this.`viaFabricPlus$userConnection`
    }
    
    override fun `viaFabricPlus$setUserConnection`(connection: UserConnection) {
        this.`viaFabricPlus$userConnection` = connection
    }
    
    override fun `viaFabricPlus$getTargetVersion`(): com.viaversion.viaversion.api.protocol.version.ProtocolVersion {
        return this.`viaFabricPlus$serverVersion`
    }
    
    override fun `viaFabricPlus$setTargetVersion`(serverVersion: com.viaversion.viaversion.api.protocol.version.ProtocolVersion) {
        this.`viaFabricPlus$serverVersion` = serverVersion
    }
    
}
