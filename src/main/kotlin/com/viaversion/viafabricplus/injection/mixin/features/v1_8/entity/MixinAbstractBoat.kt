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
package com.viaversion.viafabricplus.injection.mixin.features.v1_8.entity

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import com.viaversion.viafabricplus.injection.access.v1_8.IAbstractBoat
import com.viaversion.viafabricplus.features.v1_8.PositionInterpolator1_8
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import net.raphimc.vialegacy.api.LegacyProtocolVersion
import com.viaversion.viafabricplus.features.v1_20.EntityRidingOffsetsPre1_20_2

@Mixin(net.minecraft.world.entity.vehicle.boat.AbstractBoat::class) 
abstract class MixinAbstractBoat (entityType: net.minecraft.world.entity.EntityType<*>, world: net.minecraft.world.level.Level) : net.minecraft.world.entity.vehicle.VehicleEntity(entityType, world), IAbstractBoat {

    @field:org.spongepowered.asm.mixin.Shadow 
    @JvmField var status: net.minecraft.world.entity.vehicle.boat.AbstractBoat.Status = uninitialized()
    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$speedMultiplier`: kotlin.Double = 0.07
    
    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$boatInterpolationSteps`: kotlin.Int = 0
    
    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$boatVelocity`: net.minecraft.world.phys.Vec3 = net.minecraft.world.phys.Vec3.ZERO
    
    @org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$positionInterpolator`: PositionInterpolator1_8 = uninitialized()
    @Inject(method = ["push"], at = [At("HEAD")], cancellable = true) private fun pushAwayFrom1_8(entity: net.minecraft.world.entity.Entity, ci: CallbackInfo) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8)) {
            ci.cancel()
            super.push(entity)
        }
    }
    
    @Inject(method = ["createInterpolationHandler"], at = [At("HEAD")], cancellable = true) private fun replaceInterpolation(cir: CallbackInfoReturnable<net.minecraft.world.entity.InterpolationHandler>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8)) {
            this.`viaFabricPlus$positionInterpolator` = PositionInterpolator1_8(this as kotlin.Any as net.minecraft.world.entity.vehicle.boat.AbstractBoat)
            cir.setReturnValue(this.`viaFabricPlus$positionInterpolator`)
        }
    }
    
    override fun lerpMotion(clientVelocity: net.minecraft.world.phys.Vec3) {
        super.lerpMotion(clientVelocity)
        
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8)) {
            this.`viaFabricPlus$boatVelocity` = clientVelocity
        }
    }
    
    @Inject(method = ["tick"], at = [At("HEAD")], cancellable = true) private fun tick1_8(ci: CallbackInfo) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8)) {
            ci.cancel()
            super.tick()
            
            if (this.getHurtTime() > 0) {
                this.setHurtTime(this.getHurtTime() - 1)
            }
            if (this.getDamage() > 0) {
                this.setDamage(this.getDamage() - 1)
            }
            this.xo = this.getX()
            this.yo = this.getY()
            this.zo = this.getZ()
            
            val yPartitions: kotlin.Int = 5
            var percentSubmerged: kotlin.Double = 0.0
            for (partitionIndex in 0 ..< yPartitions) {
                val minY: kotlin.Double = this.getBoundingBox().minY + this.getBoundingBox().getYsize() * partitionIndex / yPartitions - 0.125
                val maxY: kotlin.Double = this.getBoundingBox().minY + this.getBoundingBox().getYsize() * (partitionIndex + 1) / yPartitions - 0.125
                val box: net.minecraft.world.phys.AABB = net.minecraft.world.phys.AABB(this.getBoundingBox().minX, minY, this.getBoundingBox().minZ, this.getBoundingBox().maxX, maxY, this.getBoundingBox().maxZ)
                if (net.minecraft.core.BlockPos.betweenClosedStream(box).anyMatch {pos: net.minecraft.core.BlockPos -> this.level().getFluidState(pos).`is`(net.minecraft.tags.FluidTags.WATER)} ) {
                    percentSubmerged += 1.0 / yPartitions
                }
            }
            
            val oldHorizontalSpeed: kotlin.Double = this.getDeltaMovement().horizontalDistance()
            if (oldHorizontalSpeed > (if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_7_6)) 0.2625 else 0.2975)) {
                val rx: kotlin.Double = kotlin.math.cos(this.getYRot() * java.lang.Math.PI / 180)
                val rz: kotlin.Double = kotlin.math.sin(this.getYRot() * java.lang.Math.PI / 180)
                var i: kotlin.Int = 0
                while (i < 1 + oldHorizontalSpeed * 60) {
                    val dForward: kotlin.Double = (this.random.nextFloat() * 2 - 1).toDouble()
                    val dSideways: kotlin.Double = (this.random.nextInt(2) * 2 - 1) * 0.7
                    if (this.random.nextBoolean()) {
                        val x: kotlin.Double = this.getX() - rx * dForward * 0.8 + rz * dSideways
                        val z: kotlin.Double = this.getZ() - rz * dForward * 0.8 - rx * dSideways
                        this.level().addParticle(net.minecraft.core.particles.ParticleTypes.SPLASH, x, this.getY() - 0.125, z, this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z)
                    } else {
                        val x: kotlin.Double = this.getX() + rx + rz * dForward * 0.7
                        val z: kotlin.Double = this.getZ() + rz - rx * dForward * 0.7
                        this.level().addParticle(net.minecraft.core.particles.ParticleTypes.SPLASH, x, this.getY() - 0.125, z, this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z)
                    }
                    i++}
            }
            
            if (this.level().isClientSide() && !this.isVehicle()) {
                if (this.`viaFabricPlus$boatInterpolationSteps` > 0) {
                    val data: net.minecraft.core.PositionAndRotation = `viaFabricPlus$positionInterpolator`.interpolationData()
                    val newX: kotlin.Double = this.getX() + (data.position().x - this.getX()) / this.`viaFabricPlus$boatInterpolationSteps`
                    val newY: kotlin.Double = this.getY() + (data.position().y - this.getY()) / this.`viaFabricPlus$boatInterpolationSteps`
                    val newZ: kotlin.Double = this.getZ() + (data.position().z - this.getZ()) / this.`viaFabricPlus$boatInterpolationSteps`
                    val newYaw: kotlin.Double = (this.getYRot() + net.minecraft.util.Mth.wrapDegrees(data.yRot() - this.getYRot()) / this.`viaFabricPlus$boatInterpolationSteps`).toDouble()
                    val newPitch: kotlin.Double = (this.getXRot() + (data.xRot() - this.getXRot()) / this.`viaFabricPlus$boatInterpolationSteps`).toDouble()
                    this.`viaFabricPlus$boatInterpolationSteps`--
                    this.setPos(newX, newY, newZ)
                    this.setRot(newYaw.toFloat(), newPitch.toFloat())
                } else {
                    this.setPos(this.getX() + this.getDeltaMovement().x, this.getY() + this.getDeltaMovement().y, this.getZ() + this.getDeltaMovement().z)
                    if (this.onGround()) {
                        this.setDeltaMovement(this.getDeltaMovement().scale(0.5))
                    }
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.99, 0.95, 0.99))
                }
            } else {
                if (percentSubmerged < 1) {
                    val normalizedDistanceFromMiddle: kotlin.Double = percentSubmerged * 2 - 1
                    this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.04 * normalizedDistanceFromMiddle, 0.0))
                } else {
                    if (this.getDeltaMovement().y < 0) {
                        this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.5, 1.0))
                    }
                    this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.007, 0.0))
                }
                
                if (this.getControllingPassenger() != null) {
                    val passenger: net.minecraft.world.entity.LivingEntity = this.getControllingPassenger()
                    if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_5_2)) {
                        val xAcceleration: kotlin.Double = passenger.getDeltaMovement().x * this.`viaFabricPlus$speedMultiplier`
                        val zAcceleration: kotlin.Double = passenger.getDeltaMovement().z * this.`viaFabricPlus$speedMultiplier`
                        this.setDeltaMovement(this.getDeltaMovement().add(xAcceleration, 0.0, zAcceleration))
                    } else if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
                        if (passenger.zza > 0) {
                            val xAcceleration: kotlin.Double = -kotlin.math.sin(passenger.getYRot() * java.lang.Math.PI / 180) * this.`viaFabricPlus$speedMultiplier` * 0.05
                            val zAcceleration: kotlin.Double = kotlin.math.cos(passenger.getYRot() * java.lang.Math.PI / 180) * this.`viaFabricPlus$speedMultiplier` * 0.05
                            this.setDeltaMovement(this.getDeltaMovement().add(xAcceleration, 0.0, zAcceleration))
                        }
                    } else {
                        val boatAngle: kotlin.Float = passenger.getYRot() - passenger.xxa * 90f
                        val xAcceleration: kotlin.Double = -kotlin.math.sin(boatAngle * java.lang.Math.PI / 180) * this.`viaFabricPlus$speedMultiplier` * passenger.zza * 0.05
                        val zAcceleration: kotlin.Double = kotlin.math.cos(boatAngle * java.lang.Math.PI / 180) * this.`viaFabricPlus$speedMultiplier` * passenger.zza * 0.05
                        this.setDeltaMovement(this.getDeltaMovement().add(xAcceleration, 0.0, zAcceleration))
                    }
                }
                
                var newHorizontalSpeed: kotlin.Double = this.getDeltaMovement().horizontalDistance()
                if (newHorizontalSpeed > 0.35) {
                    val multiplier: kotlin.Double = 0.35 / newHorizontalSpeed
                    this.setDeltaMovement(this.getDeltaMovement().multiply(multiplier, 1.0, multiplier))
                    newHorizontalSpeed = 0.35
                }
                
                if (newHorizontalSpeed > oldHorizontalSpeed && this.`viaFabricPlus$speedMultiplier` < 0.35) {
                    this.`viaFabricPlus$speedMultiplier` += (0.35 - this.`viaFabricPlus$speedMultiplier`) / 35
                    if (this.`viaFabricPlus$speedMultiplier` > 0.35) {
                        this.`viaFabricPlus$speedMultiplier` = 0.35
                    }
                } else {
                    this.`viaFabricPlus$speedMultiplier` -= (this.`viaFabricPlus$speedMultiplier` - 0.07) / 35
                    if (this.`viaFabricPlus$speedMultiplier` < 0.07) {
                        this.`viaFabricPlus$speedMultiplier` = 0.07
                    }
                }
                
                if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThan(LegacyProtocolVersion.r1_6_4)) {
                    for (i in 0 .. 3) {
                        val dx: kotlin.Int = net.minecraft.util.Mth.floor(this.getX() + ((i % 2) - 0.5) * 0.8)
                        val dz: kotlin.Int = net.minecraft.util.Mth.floor(this.getZ() + ((i / 2) - 0.5) * 0.8)
                        for (ddy in 0 .. 1) {
                            val dy: kotlin.Int = net.minecraft.util.Mth.floor(this.getY()) + ddy
                            val pos: net.minecraft.core.BlockPos = net.minecraft.core.BlockPos(dx, dy, dz)
                            val block: net.minecraft.world.level.block.Block = this.level().getBlockState(pos).getBlock()
                            if (block === net.minecraft.world.level.block.Blocks.SNOW) {
                                this.level().setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState())
                                this.horizontalCollision = false
                            } else if (block === net.minecraft.world.level.block.Blocks.LILY_PAD) {
                                this.level().destroyBlock(pos, true)
                                this.horizontalCollision = false
                            }
                        }
                    }
                }
                
                if (this.onGround()) {
                    this.setDeltaMovement(this.getDeltaMovement().scale(0.5))
                }
                
                this.move(net.minecraft.world.entity.MoverType.SELF, this.getDeltaMovement())
                
                if (!this.horizontalCollision || oldHorizontalSpeed <= 0.2975) {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.99, 0.95, 0.99))
                }
                
                this.setXRot(0f)
                val deltaX: kotlin.Double = this.xo - this.getX()
                val deltaZ: kotlin.Double = this.zo - this.getZ()
                if (deltaX * deltaX + deltaZ * deltaZ > 0.001) {
                    val yawDelta: kotlin.Double = net.minecraft.util.Mth.clamp(net.minecraft.util.Mth.wrapDegrees((net.minecraft.util.Mth.atan2(deltaZ, deltaX) * 180 / java.lang.Math.PI) - this.getYRot()), -20.0, 20.0)
                    this.setYRot((this.getYRot() + yawDelta).toFloat())
                }
            }
        }
    }
    
    @Inject(method = ["onPassengerTurned"], at = [At("HEAD")], cancellable = true) private fun onPassengerLookAround1_8(passenger: net.minecraft.world.entity.Entity, ci: CallbackInfo) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8)) {
            ci.cancel()
            super.onPassengerTurned(passenger)
        }
    }
    
    @Inject(method = ["checkFallDamage"], at = [At("HEAD")], cancellable = true) private fun fall1_8(ya: kotlin.Double, onGround: kotlin.Boolean, onState: net.minecraft.world.level.block.state.BlockState, pos: net.minecraft.core.BlockPos, ci: CallbackInfo) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
            ci.cancel()
            super.checkFallDamage(ya, onGround, onState, pos)
        } else if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8)) {
            this.status = net.minecraft.world.entity.vehicle.boat.AbstractBoat.Status.ON_LAND
        }
    }
    
    @Inject(method = ["canAddPassenger"], at = [At("HEAD")], cancellable = true) private fun canAddPassenger1_8(passenger: net.minecraft.world.entity.Entity, cir: CallbackInfoReturnable<kotlin.Boolean>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8)) {
            cir.setReturnValue(super.canAddPassenger(passenger))
        }
    }
    
    @Inject(method = ["positionRider"], at = [At(value = "HEAD")], cancellable = true) private fun updatePassengerPosition1_8(passenger: net.minecraft.world.entity.Entity, moveFunction: net.minecraft.world.entity.Entity.MoveFunction, ci: CallbackInfo) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8)) {
            val newPosition: net.minecraft.world.phys.Vec3 = EntityRidingOffsetsPre1_20_2.getMountedHeightOffset(this, passenger).add(this.position())
            moveFunction.accept(passenger, newPosition.x, newPosition.y + EntityRidingOffsetsPre1_20_2.getHeightOffset(passenger), newPosition.z)
            ci.cancel()
        }
    }
    
    override fun `viaFabricPlus$setBoatInterpolationSteps`(steps: kotlin.Int) {
        `viaFabricPlus$boatInterpolationSteps` = steps
    }
    
    override fun `viaFabricPlus$getBoatVelocity`(): net.minecraft.world.phys.Vec3 {
        return `viaFabricPlus$boatVelocity`
    }
    
    override fun `viaFabricPlus$setBoatVelocity`(velocity: net.minecraft.world.phys.Vec3) {
        `viaFabricPlus$boatVelocity` = velocity
    }
    
}
