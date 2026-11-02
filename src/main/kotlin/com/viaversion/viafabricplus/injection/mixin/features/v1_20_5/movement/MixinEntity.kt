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
package com.viaversion.viafabricplus.injection.mixin.features.v1_20_5.movement

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
    
    @org.spongepowered.asm.mixin.Shadow abstract fun onGround(): kotlin.Boolean
    
    @org.spongepowered.asm.mixin.Shadow abstract fun maxUpStep(): kotlin.Float
    
    @org.spongepowered.asm.mixin.Shadow abstract fun level(): net.minecraft.world.level.Level
    
    @Inject(method = ["collide(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"], at = [At("HEAD")], cancellable = true) private fun use1_20_6StepCollisionCalculation(movement: net.minecraft.world.phys.Vec3, cir: CallbackInfoReturnable<net.minecraft.world.phys.Vec3>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20_5)) {
            val thiz: net.minecraft.world.entity.Entity = this as kotlin.Any as net.minecraft.world.entity.Entity
            val box: net.minecraft.world.phys.AABB = this.getBoundingBox()
            val collisions: kotlin.collections.MutableList<net.minecraft.world.phys.shapes.VoxelShape> = this.level().getEntityCollisions(thiz, box.expandTowards(movement))
            var adjustedMovement: net.minecraft.world.phys.Vec3 = if (movement.lengthSqr() == 0.0) movement else net.minecraft.world.entity.Entity.collideBoundingBox(thiz, movement, box, this.level(), collisions)
            val changedX: kotlin.Boolean = movement.x != adjustedMovement.x
            val changedY: kotlin.Boolean = movement.y != adjustedMovement.y
            val changedZ: kotlin.Boolean = movement.z != adjustedMovement.z
            val mayTouchGround: kotlin.Boolean = this.onGround() || changedY && movement.y < 0.0
            if (this.maxUpStep() > 0f && mayTouchGround && (changedX || changedZ)) {
                var vec3d2: net.minecraft.world.phys.Vec3 = net.minecraft.world.entity.Entity.collideBoundingBox(thiz, net.minecraft.world.phys.Vec3(movement.x, this.maxUpStep().toDouble(), movement.z), box, this.level(), collisions)
                val vec3d3: net.minecraft.world.phys.Vec3 = net.minecraft.world.entity.Entity.collideBoundingBox(thiz, net.minecraft.world.phys.Vec3(0.0, this.maxUpStep().toDouble(), 0.0), box.expandTowards(movement.x, 0.0, movement.z), this.level(), collisions)
                if (vec3d3.y < this.maxUpStep()) {
                    val vec3d4: net.minecraft.world.phys.Vec3 = net.minecraft.world.entity.Entity.collideBoundingBox(thiz, net.minecraft.world.phys.Vec3(movement.x, 0.0, movement.z), box.move(vec3d3), this.level(), collisions).add(vec3d3)
                    if (vec3d4.horizontalDistanceSqr() > vec3d2.horizontalDistanceSqr()) {
                        vec3d2 = vec3d4
                    }
                }
                
                if (vec3d2.horizontalDistanceSqr() > adjustedMovement.horizontalDistanceSqr()) {
                    adjustedMovement = vec3d2.add(net.minecraft.world.entity.Entity.collideBoundingBox(thiz, net.minecraft.world.phys.Vec3(0.0, -vec3d2.y + (if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_13_2)) 0.0 else movement.y), 0.0), box.move(vec3d2), this.level(), collisions))
                }
            }
            
            cir.setReturnValue(adjustedMovement)
        }
    }
    
}
