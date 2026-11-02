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
package com.viaversion.viafabricplus.injection.mixin.features.v1_9_3.network

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import io.netty.buffer.ByteBuf

@Mixin(net.minecraft.network.protocol.game.ServerboundChatPacket::class) 
class MixinServerboundChatPacket {
private companion object {
         // The packet codec is built once during class initialization, so the limit has to be resolved per encode
        @JvmStatic @org.spongepowered.asm.mixin.injection.Redirect(method = ["<clinit>"], at = [At(value = "INVOKE", target = "Lnet/minecraft/network/codec/ByteBufCodecs;stringUtf8(I)Lnet/minecraft/network/codec/StreamCodec;")]) fun modifyChatLength(maxStringLength: kotlin.Int): net.minecraft.network.codec.StreamCodec<ByteBuf, kotlin.String> {
            return net.minecraft.network.codec.StreamCodec.of<ByteBuf, kotlin.String>(
            net.minecraft.network.codec.StreamEncoder {output: ByteBuf, value: kotlin.String -> net.minecraft.network.Utf8String.write(output, value, com.viaversion.viafabricplus.ViaFabricPlus.api().limitations().maxChatLength())}, 
            net.minecraft.network.codec.StreamDecoder {input: ByteBuf -> net.minecraft.network.Utf8String.read(input, com.viaversion.viafabricplus.ViaFabricPlus.api().limitations().maxChatLength())}
            )
        }
    
    }
}
