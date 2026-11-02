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
package com.viaversion.viafabricplus.injection.mixin.features.v1_12_2.movement

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import org.spongepowered.asm.mixin.injection.ModifyConstant

@Mixin(net.minecraft.world.entity.LivingEntity::class) 
abstract class MixinLivingEntity (type: net.minecraft.world.entity.EntityType<*>, world: net.minecraft.world.level.Level) : net.minecraft.world.entity.Entity(type, world) {

    @org.spongepowered.asm.mixin.Shadow abstract fun hasEffect(effect: net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect>): kotlin.Boolean
    
    @org.spongepowered.asm.mixin.Shadow protected abstract fun getWaterSlowDown(): kotlin.Float
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["aiStep"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;resetFallDistance()V")]) private fun dontResetLevitationFallDistance(instance: net.minecraft.world.entity.LivingEntity) {
        // Only needed when mods calculate the fall distance on the clientside
        if (this.hasEffect(net.minecraft.world.effect.MobEffects.SLOW_FALLING) || com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)) {
            instance.resetFallDistance()
        }
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["travelInWater"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSprinting()Z", ordinal = 0)]) private fun modifySwimSprintSpeed(instance: net.minecraft.world.entity.LivingEntity): kotlin.Boolean {
        return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2) && instance.isSprinting()
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["aiStep"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getFluidHeight(Lnet/minecraft/tags/TagKey;)D")]) private fun redirectFluidHeight(instance: net.minecraft.world.entity.LivingEntity, tagKey: net.minecraft.tags.TagKey<net.minecraft.world.level.material.Fluid>): kotlin.Double {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2) && tagKey === net.minecraft.tags.FluidTags.WATER && this.isInWater()) {
            return 1.0
        } else {
            return instance.getFluidHeight(tagKey)
        }
    }
    
    @Inject(method = ["getFluidFallingAdjustedMovement"], at = [At("HEAD")], cancellable = true) private fun modifySwimSprintFallSpeed(baseGravity: kotlin.Double, isFalling: kotlin.Boolean, movement: net.minecraft.world.phys.Vec3, ci: CallbackInfoReturnable<net.minecraft.world.phys.Vec3>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2) && !this.isNoGravity()) {
            ci.setReturnValue(net.minecraft.world.phys.Vec3(movement.x, movement.y - 0.02, movement.z))
        }
    }
    
    @ModifyConstant(method = ["travelInWater"], constant = [org.spongepowered.asm.mixin.injection.Constant(floatValue = 0.9f)]) private fun modifySwimFriction(constant: kotlin.Float): kotlin.Float {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)) {
            return this.getWaterSlowDown()
        } else {
            return constant
        }
    }
    
}
