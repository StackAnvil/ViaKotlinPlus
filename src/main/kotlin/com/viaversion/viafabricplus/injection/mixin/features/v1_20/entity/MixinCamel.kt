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
package com.viaversion.viafabricplus.injection.mixin.features.v1_20.entity

import org.spongepowered.asm.mixin.Mixin

@Mixin(net.minecraft.world.entity.animal.camel.Camel::class) 
abstract class MixinCamel (entityType: net.minecraft.world.entity.EntityType<out net.minecraft.world.entity.animal.equine.AbstractHorse>, world: net.minecraft.world.level.Level) : net.minecraft.world.entity.animal.equine.AbstractHorse(entityType, world) {

    override fun onPassengerTurned(passenger: net.minecraft.world.entity.Entity) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20) && this.getControllingPassenger() !== passenger) {
            this.`viaFabricPlus$clampPassengerYaw1_20_1`(passenger)
        }
    }
    
    override fun positionRider(passenger: net.minecraft.world.entity.Entity, positionUpdater: net.minecraft.world.entity.Entity.MoveFunction) {
        super.positionRider(passenger, positionUpdater)
        
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20)) {
            this.`viaFabricPlus$clampPassengerYaw1_20_1`(passenger)
        }
    }
    
    @org.spongepowered.asm.mixin.Unique private fun `viaFabricPlus$clampPassengerYaw1_20_1`(passenger: net.minecraft.world.entity.Entity) {
        passenger.setYBodyRot(this.getYRot())
        val passengerYaw: kotlin.Float = passenger.getYRot()
        
        val deltaDegrees: kotlin.Float = net.minecraft.util.Mth.wrapDegrees(passengerYaw - this.getYRot())
        val clampedDelta: kotlin.Float = net.minecraft.util.Mth.clamp(deltaDegrees, -160.0f, 160.0f)
        passenger.yRotO += clampedDelta - deltaDegrees
        
        val newYaw: kotlin.Float = passengerYaw + clampedDelta - deltaDegrees
        passenger.setYRot(newYaw)
        passenger.setYHeadRot(newYaw)
    }
    
}
