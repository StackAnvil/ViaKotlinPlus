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
package com.viaversion.viafabricplus.injection.mixin.features.v1_21_4.movement

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.objectweb.asm.Opcodes

@Mixin(net.minecraft.world.entity.LivingEntity::class) 
abstract class MixinLivingEntity (type: net.minecraft.world.entity.EntityType<*>, level: net.minecraft.world.level.Level) : net.minecraft.world.entity.Entity(type, level) {

    @field:org.spongepowered.asm.mixin.Shadow 
    @JvmField var xxa: kotlin.Float = 0f
    
    @field:org.spongepowered.asm.mixin.Shadow 
    @JvmField var zza: kotlin.Float = 0f
    
    @Inject(method = ["aiStep"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isFallFlying()Z")]) private fun moveMovementSpeedFactors(ci: CallbackInfo) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_4)) {
            this.xxa *= 0.98f
            this.zza *= 0.98f
        }
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["aiStep"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;is(Ljava/lang/Object;)Z")]) private fun useEuclideanDistanceCalculation(instance: net.minecraft.world.entity.LivingEntity, o: kotlin.Any): kotlin.Boolean {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_4)) {
            return false
        } else {
            return instance.`is`(o as net.minecraft.world.entity.EntityType<*>)
        }
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["travelFallFlying"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;onClimbable()Z")]) private fun dontStopGlidingWhenClimbing(instance: net.minecraft.world.entity.LivingEntity): kotlin.Boolean {
        return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_4) && instance.onClimbable()
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["handleRelativeFrictionAndCalculateMovement"], at = [At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;wasInPowderSnow:Z", opcode = Opcodes.GETFIELD)]) private fun dontCheckLastTick(instance: net.minecraft.world.entity.LivingEntity): kotlin.Boolean {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_4)) {
            return this.getInBlockState().`is`(net.minecraft.world.level.block.Blocks.POWDER_SNOW)
        } else {
            return instance.wasInPowderSnow
        }
    }
    
}
