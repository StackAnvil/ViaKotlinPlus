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
package com.viaversion.viafabricplus.injection.mixin.features.v1_21_9

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import com.viaversion.viafabricplus.injection.mixin.features.v1_21_9.MixinMth

@Mixin(net.minecraft.util.Mth::class) 
class MixinMth {
private companion object {
    
        @JvmStatic @Inject(method = ["sin"], at = [At("HEAD")], cancellable = true) fun changeSinScaling(i: kotlin.Double, cir: CallbackInfoReturnable<kotlin.Float>) {
            if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_9)) {
                val index: kotlin.Float = i.toFloat() // Convert back as float; the game still passes the same types
                cir.setReturnValue(SIN[(index * 10430.378f).toInt() and 65535])
            }
        }
    
        @JvmStatic @Inject(method = ["cos"], at = [At("HEAD")], cancellable = true) fun changeCosScaling(i: kotlin.Double, cir: CallbackInfoReturnable<kotlin.Float>) {
            if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_9)) {
                val index: kotlin.Float = i.toFloat()
                cir.setReturnValue(SIN[(index * 10430.378f + 16384.0f).toInt() and 65535])
            }
        }
    

        @JvmField
        @org.spongepowered.asm.mixin.Shadow
        @Final
        var SIN: kotlin.FloatArray = uninitialized()
    
    }
}
