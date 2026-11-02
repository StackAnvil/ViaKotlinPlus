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
package com.viaversion.viafabricplus.features.v1_8

import com.viaversion.viafabricplus.injection.access.v1_8.IAbstractBoat

class PositionInterpolator1_8 (entity: net.minecraft.world.entity.vehicle.boat.AbstractBoat) : net.minecraft.world.entity.AbstractInterpolationHandler(entity, net.minecraft.world.entity.LinearInterpolationHandler.DEFAULT_INTERPOLATION_STEPS) {

    private val boatEntity: net.minecraft.world.entity.vehicle.boat.AbstractBoat
    private val interpolationData: net.minecraft.core.PositionAndRotation.Mutable = net.minecraft.core.PositionAndRotation.Mutable()
    
    init {
        this.boatEntity = entity
    }
    
    public override fun interpolationData(): net.minecraft.core.PositionAndRotation.Mutable {
        return this.interpolationData
    }
    
    override fun interpolateTo(position: net.minecraft.world.entity.PositionPath, yaw: kotlin.Float, pitch: kotlin.Float) {
        val pos: net.minecraft.world.phys.Vec3 = position.endPosition()
        val mixinBoatEntity: IAbstractBoat = this.boatEntity as IAbstractBoat
        if ( /*interpolate &&*/boatEntity.isVehicle() && com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_7_6)) {
            boatEntity.xo = pos.x
            boatEntity.yo = pos.y
            boatEntity.zo = pos.z
            mixinBoatEntity.`viaFabricPlus$setBoatInterpolationSteps`(0)
            boatEntity.setPos(pos)
            boatEntity.setRot(yaw, pitch)
            boatEntity.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO)
            mixinBoatEntity.`viaFabricPlus$setBoatVelocity`(net.minecraft.world.phys.Vec3.ZERO)
        } else {
            if (!boatEntity.isVehicle()) {
                mixinBoatEntity.`viaFabricPlus$setBoatInterpolationSteps`(8)
            } else {
                if (boatEntity.distanceToSqr(pos.x, pos.y, pos.z) <= 1) {
                    return 
                }
                mixinBoatEntity.`viaFabricPlus$setBoatInterpolationSteps`(3)
            }
            
            this.interpolationData.set(pos, yaw, pitch)
            boatEntity.setDeltaMovement(mixinBoatEntity.`viaFabricPlus$getBoatVelocity`())
        }
    }
    
    override fun startInterpolating(position: net.minecraft.world.entity.PositionPath, yRot: kotlin.Float, xRot: kotlin.Float) {
    }
    
    override fun doInterpolate() {
    }
    
    override fun interpolate() {
    }
    
    override fun hasActiveInterpolation(): kotlin.Boolean {
        return false
    }
    
    override fun cancel() {
    }
    
}
