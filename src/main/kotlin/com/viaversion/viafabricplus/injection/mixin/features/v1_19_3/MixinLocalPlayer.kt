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
package com.viaversion.viafabricplus.injection.mixin.features.v1_19_3

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At

@Mixin(net.minecraft.client.player.LocalPlayer::class) 
abstract class MixinLocalPlayer {

    @org.spongepowered.asm.mixin.injection.Redirect(method = ["updateAutoJump"], at = [At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;invSqrt(F)F")]) private fun useFastInverseSqrt(x: kotlin.Float): kotlin.Float {
        var x: kotlin.Float = x
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_19_3)) {
            x = java.lang.Float.intBitsToFloat(1597463007 - (java.lang.Float.floatToIntBits(x) shr 1))
            return x * (1.5f - (0.5f * x) * x * x)
        } else {
            return net.minecraft.util.Mth.invSqrt(x)
        }
    }
    
    @com.llamalad7.mixinextras.injector.v2.WrapWithCondition(method = ["sendChanges"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;sendIsSprintingIfNeeded()V")]) private fun removeSprintingPacket(instance: net.minecraft.client.player.LocalPlayer): kotlin.Boolean {
        return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_19_3)
    }
    
}
