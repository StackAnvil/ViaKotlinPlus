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
package com.viaversion.viafabricplus.injection.mixin.features.v1_13_2.movement

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(net.minecraft.world.entity.Entity::class) 
abstract class MixinEntity {

    @org.spongepowered.asm.mixin.Shadow 
    @JvmField val level: net.minecraft.world.level.Level = uninitialized()
    @org.spongepowered.asm.mixin.Shadow abstract fun getBoundingBox(): net.minecraft.world.phys.AABB
    
    @org.spongepowered.asm.mixin.Shadow abstract fun level(): net.minecraft.world.level.Level
    
    @Inject(method = ["isInLava"], at = [At("RETURN")], cancellable = true) private fun replaceLavaCheck1_13_2(cir: CallbackInfoReturnable<kotlin.Boolean>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_13_2)) {
            val aabb: net.minecraft.world.phys.AABB = this.getBoundingBox().deflate(0.1, 0.4, 0.1)
            cir.setReturnValue(this.level.getBlockStatesIfLoaded(aabb).anyMatch {key: net.minecraft.world.level.block.state.BlockState -> key.getFluidState().`is`(net.minecraft.tags.FluidTags.LAVA)} )
        }
    }
    
    private companion object {
        @JvmStatic @org.spongepowered.asm.mixin.injection.Redirect(method = ["collideWithShapes(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/List;)Lnet/minecraft/world/phys/Vec3;", "checkInsideBlocks(Ljava/util/List;Lnet/minecraft/world/entity/InsideBlockEffectApplier\$StepBasedCollector;)V"], at = [At(value = "INVOKE", target = "Lnet/minecraft/core/Direction;axisStepOrder(Lnet/minecraft/world/phys/Vec3;)Lcom/google/common/collect/ImmutableList;")]) fun alwaysSortYXZ(movement: net.minecraft.world.phys.Vec3): com.google.common.collect.ImmutableList<net.minecraft.core.Direction.Axis> {
            if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_13_2)) {
                return net.minecraft.core.Direction.YXZ_AXIS_ORDER
            } else {
                return net.minecraft.core.Direction.axisStepOrder(movement)
            }
        }
        
        @JvmStatic @org.spongepowered.asm.mixin.injection.Redirect(method = ["move"], at = [At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;equal(DD)Z")]) fun horizontalExactCollisionEqualness(a: kotlin.Double, b: kotlin.Double): kotlin.Boolean {
            if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_13_2)) {
                return a == b
            } else {
                return net.minecraft.util.Mth.equal(a, b)
            }
        }
        
        @JvmStatic @Inject(method = ["getInputVector"], at = [At("HEAD")], cancellable = true) fun getInputVector1_13_2(input: net.minecraft.world.phys.Vec3, speed: kotlin.Float, yRot: kotlin.Float, cir: CallbackInfoReturnable<net.minecraft.world.phys.Vec3>) {
            if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_13_2)) {
                var x: kotlin.Float = input.x.toFloat()
                var z: kotlin.Float = input.z.toFloat()
                
                var length: kotlin.Float = x * x + z * z
                if (length < 1.0E-4f) {
                    cir.setReturnValue(net.minecraft.world.phys.Vec3.ZERO)
                }
                
                length = kotlin.math.max(net.minecraft.util.Mth.sqrt(length), 1.0f)
                
                val scale: kotlin.Float = speed / length
                
                x *= scale
                z *= scale
                
                val sin: kotlin.Float = net.minecraft.util.Mth.sin((yRot * (java.lang.Math.PI / 180.0).toFloat()).toDouble())
                val cos: kotlin.Float = net.minecraft.util.Mth.cos((yRot * (java.lang.Math.PI / 180.0).toFloat()).toDouble())
                
                cir.setReturnValue(net.minecraft.world.phys.Vec3((x * cos - z * sin).toDouble(), input.y, (z * cos + x * sin).toDouble()))
            }
        }
        
    }}
