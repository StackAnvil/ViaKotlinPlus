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
package com.viaversion.viafabricplus.injection.mixin.features.v26_1.movement

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.objectweb.asm.Opcodes
import com.llamalad7.mixinextras.injector.ModifyExpressionValue

@Mixin(net.minecraft.world.entity.Entity::class) 
abstract class MixinEntity {

    @field:org.spongepowered.asm.mixin.Shadow 
    @JvmField var verticalCollision: kotlin.Boolean = false
    
    @org.spongepowered.asm.mixin.Shadow abstract fun getDeltaMovement(): net.minecraft.world.phys.Vec3
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["move"], at = [At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;horizontalCollision:Z", ordinal = 2, opcode = Opcodes.GETFIELD)]) private fun removeVerticalCheck(instance: net.minecraft.world.entity.Entity): kotlin.Boolean {
        return instance.horizontalCollision || (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v26_1) && this.verticalCollision)
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["restituteMovementAfterCollisions"], at = [At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;verticalCollisionBelow:Z", opcode = Opcodes.GETFIELD)]) private fun fixBelowCollisionCheck(instance: net.minecraft.world.entity.Entity): kotlin.Boolean {
        return instance.verticalCollisionBelow || com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v26_1)
    }
    
    @com.llamalad7.mixinextras.expression.Definition(id = "y", field = ["Lnet/minecraft/world/phys/Vec3;y:D"]) @com.llamalad7.mixinextras.expression.Definition(id = "currentMovement", local = [com.llamalad7.mixinextras.sugar.Local(type = net.minecraft.world.phys.Vec3::class, name = ["currentMovement"])]) @com.llamalad7.mixinextras.expression.Expression("-currentMovement.y <= ?") @ModifyExpressionValue(method = ["restituteMovementAfterCollisions"], at = [At("MIXINEXTRAS:EXPRESSION")]) private fun fixGravityCheck(original: kotlin.Boolean, @com.llamalad7.mixinextras.sugar.Local(name = ["currentMovement"]) currentMovement: net.minecraft.world.phys.Vec3): kotlin.Boolean {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v26_1)) {
            return !(currentMovement.y < 0.0)
        } else {
            return original
        }
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["restituteMovementAfterCollisions"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;with(Lnet/minecraft/core/Direction\$Axis;D)Lnet/minecraft/world/phys/Vec3;", ordinal = 2)]) private fun fixRestitution(instance: net.minecraft.world.phys.Vec3, axis: net.minecraft.core.Direction.Axis, value: kotlin.Double, @com.llamalad7.mixinextras.sugar.Local(name = ["restitution"]) restitution: kotlin.Double): net.minecraft.world.phys.Vec3 {
        return instance.with(axis, if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v26_1)) -this.getDeltaMovement().y * restitution else value)
    }
    
}
