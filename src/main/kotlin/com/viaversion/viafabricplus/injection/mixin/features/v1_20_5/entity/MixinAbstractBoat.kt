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
package com.viaversion.viafabricplus.injection.mixin.features.v1_20_5.entity

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At

@Mixin(net.minecraft.world.entity.vehicle.boat.AbstractBoat::class) 
abstract class MixinAbstractBoat (entityType: net.minecraft.world.entity.EntityType<*>, world: net.minecraft.world.level.Level) : net.minecraft.world.entity.vehicle.VehicleEntity(entityType, world) {

    @org.spongepowered.asm.mixin.injection.Redirect(method = ["interact"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/VehicleEntity;interact(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/InteractionResult;")]) private fun makeNotLeashable(instance: net.minecraft.world.entity.vehicle.VehicleEntity, player: net.minecraft.world.entity.player.Player, interactionHand: net.minecraft.world.InteractionHand, vec3: net.minecraft.world.phys.Vec3): net.minecraft.world.InteractionResult {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20_5)) {
            return net.minecraft.world.InteractionResult.PASS
        } else {
            return super.interact(player, interactionHand, vec3)
        }
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["floatBoat"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;noCollision(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Z")]) private fun alwaysUpdatePosition(instance: net.minecraft.world.level.Level, entity: net.minecraft.world.entity.Entity, box: net.minecraft.world.phys.AABB): kotlin.Boolean {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20_5)) {
            return true
        } else {
            return instance.noCollision(entity, box)
        }
    }
    
}
