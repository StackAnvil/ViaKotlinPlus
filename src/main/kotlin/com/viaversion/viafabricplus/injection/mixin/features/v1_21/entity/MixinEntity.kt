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
package com.viaversion.viafabricplus.injection.mixin.features.v1_21.entity

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.ModifyConstant

@Mixin(net.minecraft.world.entity.Entity::class) 
abstract class MixinEntity {

    @org.spongepowered.asm.mixin.Shadow 
    @JvmField val level: net.minecraft.world.level.Level = uninitialized()
    @org.spongepowered.asm.mixin.Shadow abstract fun level(): net.minecraft.world.level.Level
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["move"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;lengthSqr()D", ordinal = 1)], slice = [org.spongepowered.asm.mixin.injection.Slice(from = At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;collide(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"))]) private fun allowSmallValues(instance: net.minecraft.world.phys.Vec3): kotlin.Double {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21)) {
            return kotlin.Double.Companion.MAX_VALUE
        } else {
            return instance.lengthSqr()
        }
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["setXRot"], at = [At(value = "INVOKE", target = "Ljava/lang/Math;clamp(FFF)F", remap = false)]) private fun dontClampPitch(value: kotlin.Float, min: kotlin.Float, max: kotlin.Float): kotlin.Float {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21)) {
            return value
        } else {
            return java.lang.Math.clamp(value, min, max)
        }
    }
    
    @ModifyConstant(method = ["checkInsideBlocks(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/entity/InsideBlockEffectApplier\$StepBasedCollector;Lit/unimi/dsi/fastutil/longs/LongSet;I)I"], constant = [org.spongepowered.asm.mixin.injection.Constant(doubleValue = 9.999999747378752E-6)]) private fun fixBlockCollisionMargin(constant: kotlin.Double): kotlin.Double {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_19_1)) {
            return 1E-3
        } else if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21)) {
            return 1E-7
        } else {
            return constant
        }
    }
    
}
