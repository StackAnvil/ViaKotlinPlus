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
package com.viaversion.viafabricplus.injection.mixin.features.v1_12_2.movement

import org.spongepowered.asm.mixin.Mixin
import com.llamalad7.mixinextras.injector.ModifyExpressionValue
import org.spongepowered.asm.mixin.injection.At

@Mixin(net.minecraft.world.entity.EntityFluidInteraction::class) 
abstract class MixinEntityFluidInteraction {

    @com.llamalad7.mixinextras.expression.Definition(id = "fluidTop", local = [com.llamalad7.mixinextras.sugar.Local(type = kotlin.Double::class, name = ["fluidTop"])]) @com.llamalad7.mixinextras.expression.Definition(id = "box", local = [com.llamalad7.mixinextras.sugar.Local(type = net.minecraft.world.phys.AABB::class, name = ["box"])]) @com.llamalad7.mixinextras.expression.Definition(id = "minY", field = ["Lnet/minecraft/world/phys/AABB;minY:D"]) @com.llamalad7.mixinextras.expression.Expression("fluidTop < box.minY") @ModifyExpressionValue(method = ["update"], at = [At("MIXINEXTRAS:EXPRESSION")]) private fun removeConditional(original: kotlin.Boolean): kotlin.Boolean {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)) {
            return false // Equates to true due to the negation in the original code
        } else {
            return original
        }
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["update"], at = [At(value = "INVOKE", target = "Ljava/lang/Math;max(DD)D")]) private fun adjustHeightCalculation(a: kotlin.Double, b: kotlin.Double): kotlin.Double {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)) {
            return kotlin.math.max(a + 0.4, b)
        } else {
            return kotlin.math.max(a, b)
        }
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["update"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;scale(D)Lnet/minecraft/world/phys/Vec3;")]) private fun dontScaleCurrent(instance: net.minecraft.world.phys.Vec3, scale: kotlin.Double): net.minecraft.world.phys.Vec3 {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)) {
            return instance
        } else {
            return instance.scale(scale)
        }
    }
    
}
