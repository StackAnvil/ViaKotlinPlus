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
import org.spongepowered.asm.mixin.injection.At
import com.viaversion.viafabricplus.features.v1_20.EntityRidingOffsetsPre1_20_2

@Mixin(net.minecraft.world.entity.Entity::class) 
abstract class MixinEntity {

    @org.spongepowered.asm.mixin.Shadow protected abstract fun getPassengerAttachmentPoint(passenger: net.minecraft.world.entity.Entity, dimensions: net.minecraft.world.entity.EntityDimensions, scaleFactor: kotlin.Float): net.minecraft.world.phys.Vec3
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["positionRider(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity\$MoveFunction;)V"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getVehicleAttachmentPoint(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/phys/Vec3;")]) private fun use1_20_1RidingOffset(instance: net.minecraft.world.entity.Entity, vehicle: net.minecraft.world.entity.Entity): net.minecraft.world.phys.Vec3 {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20)) {
            return net.minecraft.world.phys.Vec3(0.0, -EntityRidingOffsetsPre1_20_2.getHeightOffset(instance), 0.0)
        } else {
            return instance.getVehicleAttachmentPoint(vehicle)
        }
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["getPassengerRidingPosition"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getPassengerAttachmentPoint(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/EntityDimensions;F)Lnet/minecraft/world/phys/Vec3;")]) private fun getPassengerRidingPos1_20_1(instance: net.minecraft.world.entity.Entity, passenger: net.minecraft.world.entity.Entity, dimensions: net.minecraft.world.entity.EntityDimensions, scale: kotlin.Float): net.minecraft.world.phys.Vec3 {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20)) {
            return EntityRidingOffsetsPre1_20_2.getMountedHeightOffset(instance, passenger).yRot(-instance.getYRot() * (java.lang.Math.PI / 180).toFloat())
        } else {
            return getPassengerAttachmentPoint(passenger, dimensions, scale)
        }
    }
    
}
