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
package com.viaversion.viafabricplus.injection.mixin.features.v26_2.entity

import org.spongepowered.asm.mixin.Mixin
import com.llamalad7.mixinextras.injector.ModifyExpressionValue
import org.spongepowered.asm.mixin.injection.At

@Mixin(net.minecraft.world.entity.player.Player::class) 
abstract class MixinPlayer {

    @ModifyExpressionValue(method = ["getDestroySpeed"], at = [At(value = "INVOKE", target = "Ljava/lang/Math;pow(DD)D")]) private fun changeMiningFatigueCalculation(original: kotlin.Double, @com.llamalad7.mixinextras.sugar.Local(name = ["amplifier"]) amplifier: kotlin.Int): kotlin.Double {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v26_2)) {
            return when (amplifier) { 0 -> 0.3
                1 -> 0.09
                2 -> 0.0027
                else -> 8.1E-4 }
        } else {
            return original
        }
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["tryToStartFallFlying"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isInLiquid()Z")]) private fun allowFallFlyingInLava(instance: net.minecraft.world.entity.player.Player): kotlin.Boolean {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v26_2)) {
            return instance.isInWater()
        } else {
            return instance.isInLiquid()
        }
    }
    
}
