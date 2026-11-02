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

@Mixin(net.minecraft.world.entity.LivingEntity::class) 
abstract class MixinLivingEntity (type: net.minecraft.world.entity.EntityType<*>, world: net.minecraft.world.level.Level) : net.minecraft.world.entity.Entity(type, world) {

    @org.spongepowered.asm.mixin.injection.Redirect(method = ["getPassengerRidingPosition"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getPassengerAttachmentPoint(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/EntityDimensions;F)Lnet/minecraft/world/phys/Vec3;")]) private fun getPassengerRidingPos1_20_1(instance: net.minecraft.world.entity.LivingEntity, entity: net.minecraft.world.entity.Entity, entityDimensions: net.minecraft.world.entity.EntityDimensions, v: kotlin.Float): net.minecraft.world.phys.Vec3 {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20)) {
            return EntityRidingOffsetsPre1_20_2.getMountedHeightOffset(instance, entity).yRot(-instance.getYRot() * (java.lang.Math.PI / 180).toFloat())
        } else {
            return getPassengerAttachmentPoint(entity, entityDimensions, v)
        }
    }
    
}
