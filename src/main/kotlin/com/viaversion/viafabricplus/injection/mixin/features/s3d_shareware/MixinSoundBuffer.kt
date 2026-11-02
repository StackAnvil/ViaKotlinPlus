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
package com.viaversion.viafabricplus.injection.mixin.features.s3d_shareware

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Final
import javax.sound.sampled.AudioFormat
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.viaversion.viaaprilfools.api.AprilFoolsProtocolVersion

@Mixin(com.mojang.blaze3d.audio.SoundBuffer::class) 
abstract class MixinSoundBuffer {

    @org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField val format: AudioFormat = uninitialized()
    @Inject(method = ["<init>"], at = [At("RETURN")]) private fun modifyBuffer(data: java.nio.ByteBuffer, format: AudioFormat, ci: CallbackInfo) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion() == AprilFoolsProtocolVersion.s3d_shareware) {
            this.`viaFabricPlus$apply8BitSound`(data)
        }
    }
    
    @org.spongepowered.asm.mixin.Unique private fun `viaFabricPlus$apply8BitSound`(byteBuffer: java.nio.ByteBuffer) {
        if (byteBuffer == null) {
            return 
        }
        if (this.format.getChannels() == 1) {
            this.`viaFabricPlus$apply8BitMono`(byteBuffer)
        } else {
            this.`viaFabricPlus$apply8BitStereo`(byteBuffer)
        }
    }
    
    @org.spongepowered.asm.mixin.Unique private fun `viaFabricPlus$apply8BitMono`(byteBuffer: java.nio.ByteBuffer) {
        var short2: kotlin.Short = 0
        var integer3: kotlin.Int = 0
        while (byteBuffer.hasRemaining()) {
            if (integer3 == 0) {
                byteBuffer.mark()
                short2 = (byteBuffer.getShort().toInt() and -0x4).toShort()
                byteBuffer.reset()
                integer3 = 15
            } else {
                --integer3
            }
            byteBuffer.putShort(short2)
        }
        byteBuffer.flip()
    }
    
    @org.spongepowered.asm.mixin.Unique private fun `viaFabricPlus$apply8BitStereo`(byteBuffer: java.nio.ByteBuffer) {
        var short2: kotlin.Short = 0
        var short3: kotlin.Short = 0
        var integer4: kotlin.Int = 0
        while (byteBuffer.hasRemaining()) {
            if (integer4 == 0) {
                byteBuffer.mark()
                short2 = (byteBuffer.getShort().toInt() and -0x4).toShort()
                short3 = (byteBuffer.getShort().toInt() and -0x4).toShort()
                byteBuffer.reset()
                integer4 = 15
            } else {
                --integer4
            }
            byteBuffer.putShort(short2)
            byteBuffer.putShort(short3)
        }
        byteBuffer.flip()
    }
    
}
