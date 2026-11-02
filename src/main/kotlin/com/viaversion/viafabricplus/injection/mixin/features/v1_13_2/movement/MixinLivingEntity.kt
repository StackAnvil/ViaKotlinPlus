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
package com.viaversion.viafabricplus.injection.mixin.features.v1_13_2.movement

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.objectweb.asm.Opcodes
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import org.spongepowered.asm.mixin.injection.ModifyVariable

@Mixin(net.minecraft.world.entity.LivingEntity::class) 
abstract class MixinLivingEntity (type: net.minecraft.world.entity.EntityType<*>, level: net.minecraft.world.level.Level) : net.minecraft.world.entity.Entity(type, level) {

    @field:org.spongepowered.asm.mixin.Shadow 
    @JvmField var jumping: kotlin.Boolean = false
    
    @org.spongepowered.asm.mixin.Shadow abstract fun getSpeed(): kotlin.Float
    
    @org.spongepowered.asm.mixin.Shadow protected abstract fun getFlyingSpeed(): kotlin.Float
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["handleRelativeFrictionAndCalculateMovement"], at = [At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;jumping:Z", opcode = Opcodes.GETFIELD)]) private fun disableJumpOnLadder(self: net.minecraft.world.entity.LivingEntity): kotlin.Boolean {
        return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_13_2) && jumping
    }
    
    @Inject(method = ["getFrictionInfluencedSpeed"], at = [At("HEAD")], cancellable = true) private fun modifyFrictionInfluencedSpeed(blockFriction: kotlin.Float, cir: CallbackInfoReturnable<kotlin.Float>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_13_2)) {
            val drag: kotlin.Float = if (this.onGround()) blockFriction * 0.91f else 0.91f
            val accel: kotlin.Float = 0.16277136f / (drag * drag * drag)
            cir.setReturnValue(if (this.onGround()) this.getSpeed() * accel else this.getFlyingSpeed())
        }
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["travelInAir"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;hasChunkAt(Lnet/minecraft/core/BlockPos;)Z")]) private fun modifyLoadedCheck(instance: net.minecraft.world.level.Level, blockPos: net.minecraft.core.BlockPos): kotlin.Boolean {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_13_2)) {
            return instance.hasChunkAt(blockPos) && instance.getChunkSource().hasChunk(blockPos.getX() shr 4, blockPos.getZ() shr 4)
        } else {
            return instance.hasChunkAt(blockPos)
        }
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["travelInWater"], slice = [org.spongepowered.asm.mixin.injection.Slice(from = At(value = "FIELD", target = "Lnet/minecraft/world/effect/MobEffects;DOLPHINS_GRACE:Lnet/minecraft/core/Holder;", opcode = Opcodes.GETSTATIC))], at = [At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;horizontalCollision:Z", ordinal = 0, opcode = Opcodes.GETFIELD)]) private fun disableClimbing(instance: net.minecraft.world.entity.LivingEntity): kotlin.Boolean {
        return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_13_2) && instance.horizontalCollision
    }
    
    @ModifyVariable(method = ["getFluidFallingAdjustedMovement"], at = [At("HEAD")], argsOnly = true) private fun modifyMovingDown(isFalling: kotlin.Boolean): kotlin.Boolean {
        return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_13_2) && isFalling
    }
    
}
