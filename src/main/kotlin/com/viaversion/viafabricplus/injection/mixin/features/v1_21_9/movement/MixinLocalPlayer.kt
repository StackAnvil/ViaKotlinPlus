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
package com.viaversion.viafabricplus.injection.mixin.features.v1_21_9.movement

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.llamalad7.mixinextras.sugar.Share
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation

@Mixin(net.minecraft.client.player.LocalPlayer::class) 
abstract class MixinLocalPlayer (world: net.minecraft.client.multiplayer.ClientLevel, profile: com.mojang.authlib.GameProfile) : net.minecraft.client.player.AbstractClientPlayer(world, profile) {

    @field:org.spongepowered.asm.mixin.Shadow 
    @JvmField var input: net.minecraft.client.player.ClientInput = uninitialized()
    @field:org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField var minecraft: net.minecraft.client.Minecraft = uninitialized()
    @field:org.spongepowered.asm.mixin.Shadow 
    @JvmField var sprintTriggerTime: kotlin.Int = 0
    
    @org.spongepowered.asm.mixin.Shadow protected abstract fun shouldStopRunSprinting(): kotlin.Boolean
    
    @org.spongepowered.asm.mixin.Shadow abstract fun isMovingSlowly(): kotlin.Boolean
    
    @org.spongepowered.asm.mixin.Shadow protected abstract fun vehicleCanSprint(vehicle: net.minecraft.world.entity.Entity): kotlin.Boolean
    
    @org.spongepowered.asm.mixin.Shadow protected abstract fun canStartSprinting(): kotlin.Boolean
    
    @org.spongepowered.asm.mixin.Shadow abstract override fun isUnderWater(): kotlin.Boolean
    
    @org.spongepowered.asm.mixin.Shadow abstract override fun isUsingItem(): kotlin.Boolean
    
    @Inject(method = ["isSprintingPossible"], at = [At("HEAD")], cancellable = true) private fun isSprintingPossible1_21_10(allowedInShallowWater: kotlin.Boolean, cir: CallbackInfoReturnable<kotlin.Boolean>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_9)) {
            cir.setReturnValue(!this.isMobilityRestricted() && this.`viaFabricPlus$hasEnoughFoodToSprint1_19_1`()
            && (!this.isPassenger() || this.vehicleCanSprint(this.getVehicle())) && (allowedInShallowWater || !this.isInShallowWater()))
        }
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["aiStep"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;shouldStopRunSprinting()Z")]) private fun changeStopSprintingConditions(instance: net.minecraft.client.player.LocalPlayer): kotlin.Boolean {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_4)) {
            return this.`viaFabricPlus$shouldCancelSprinting`() || this.horizontalCollision && !this.minorHorizontalCollision || !this.`viaFabricPlus$canWaterSprint`()
        } else {
            return this.shouldStopRunSprinting()
        }
    }
    
    @Inject(method = ["aiStep"], at = [At("HEAD")]) private fun storeSprintingSneakingState(ci: CallbackInfo, @Share("sneakSprint") ref: LocalBooleanRef) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_4)) {
            ref.set(!this.input.keyPresses.shift() && !this.`viaFabricPlus$isWalking1_21_4`())
        }
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["aiStep"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;canStartSprinting()Z")]) private fun changeCanStartSprintingConditions(instance: net.minecraft.client.player.LocalPlayer, @Share("sneakSprint") ref: LocalBooleanRef): kotlin.Boolean {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_4)) {
            val canStartSprinting: kotlin.Boolean = this.canStartSprinting()
            val onGround: kotlin.Boolean = if (this.isPassenger()) this.getVehicle().onGround() else this.onGround()
            if ((onGround || this.isUnderWater()) && ref.get() && canStartSprinting) {
                if (this.sprintTriggerTime <= 0 && !this.minecraft.options.keySprint.isDown()) {
                    this.sprintTriggerTime = this.minecraft.options.sprintWindow().get()
                } else {
                    this.setSprinting(true)
                }
            }
            
            if ((this.`viaFabricPlus$canWaterSprint`()) && canStartSprinting && this.minecraft.options.keySprint.isDown()) {
                this.setSprinting(true)
            }
            return false
        } else {
            return this.canStartSprinting()
        }
    }
    
    @Inject(method = ["canStartSprinting"], at = [At("HEAD")], cancellable = true) private fun changeCanStartSprintingConditions(cir: CallbackInfoReturnable<kotlin.Boolean>) {
        val version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion = com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion()
        if (version.olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_7)) {
            cir.setReturnValue(!this.isSprinting() && (if (version.olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_4)) this.`viaFabricPlus$isWalking1_21_4`() else this.input.hasForwardImpulse())
            && this.`viaFabricPlus$hasEnoughFoodToSprint1_19_1`()
            && !this.isUsingItem() && !this.isMobilityRestricted() && (!(version.newerThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_19_3) && this.isPassenger()) || this.vehicleCanSprint(this.getVehicle()))
            && (!(version.newerThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_19_3) && this.isFallFlying()) || this.isUnderWater())
            && (!(this.isMovingSlowly() && version == com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_4) || (this.isUnderWater() && version == com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_4))
            && (!version.olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_4) && (!this.isInWater() || this.isUnderWater()) || version.olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_4)))
        }
    }
    
    @Inject(method = ["shouldStopSwimSprinting"], at = [At("HEAD")], cancellable = true) private fun changeStopSwimSprintingConditions(cir: CallbackInfoReturnable<kotlin.Boolean>) {
        // Not needed, but for consistency and in case a mod uses this method
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_4)) {
            cir.setReturnValue(!this.onGround() && !this.input.keyPresses.shift() && this.`viaFabricPlus$shouldCancelSprinting`() || !this.isInWater())
        } else if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_7)) {
            cir.setReturnValue(this.isMobilityRestricted() || this.isPassenger() && !this.vehicleCanSprint(this.getVehicle()) || !this.isInWater() || !this.input.hasForwardImpulse() && !this.onGround() && !this.input.keyPresses.shift() || !this.`viaFabricPlus$hasEnoughFoodToSprint1_19_1`())
        }
    }
    
    @Inject(method = ["shouldStopRunSprinting"], at = [At("HEAD")], cancellable = true) private fun changeStopSprintingConditions(cir: CallbackInfoReturnable<kotlin.Boolean>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_4)) {
            val ridingCamel: kotlin.Boolean = getVehicle() != null && getVehicle().getType() === net.minecraft.world.entity.EntityTypes.CAMEL
            cir.setReturnValue(this.isFallFlying() || this.isMobilityRestricted() || this.isMovingSlowly() || this.isPassenger() && !ridingCamel || this.isUsingItem() && !this.isPassenger() && !this.isUnderWater())
        } else if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_7)) {
            cir.setReturnValue(this.isMobilityRestricted() || this.isPassenger() && !this.vehicleCanSprint(this.getVehicle()) || !this.input.hasForwardImpulse() || !this.`viaFabricPlus$hasEnoughFoodToSprint1_19_1`() || this.horizontalCollision && !this.minorHorizontalCollision || this.isInWater() && !this.isUnderWater())
        }
    }
    
    @WrapOperation(method = ["aiStep"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/player/ClientInput;hasForwardImpulse()Z")]) private fun easierUnderwaterSprinting(instance: net.minecraft.client.player.ClientInput, original: com.llamalad7.mixinextras.injector.wrapoperation.Operation<kotlin.Boolean>): kotlin.Boolean {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_4)) {
            return this.`viaFabricPlus$isWalking1_21_4`()
        } else {
            return original.call(instance)
        }
    }
    
    @org.spongepowered.asm.mixin.Unique private fun `viaFabricPlus$shouldCancelSprinting`(): kotlin.Boolean {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_14_1)) {
            return !(this.input.moveVector.y >= 0.8f) || !this.`viaFabricPlus$hasEnoughFoodToSprint1_19_1`() // Disables sprint sneaking
        } else {
            return !this.input.hasForwardImpulse() || !this.`viaFabricPlus$hasEnoughFoodToSprint1_19_1`()
        }
    }
    
    @org.spongepowered.asm.mixin.Unique private fun `viaFabricPlus$hasEnoughFoodToSprint1_19_1`(): kotlin.Boolean {
        return (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_19_1) && this.isPassenger()) || this.hasEnoughFoodToDoExhaustiveManoeuvres()
    }
    
    @org.spongepowered.asm.mixin.Unique private fun `viaFabricPlus$isWalking1_21_4`(): kotlin.Boolean {
        val submergedInWater: kotlin.Boolean = com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_14_1) && isUnderWater()
        return if (submergedInWater) this.input.hasForwardImpulse() else this.input.moveVector.y >= 0.8
    }
    
    @org.spongepowered.asm.mixin.Unique private fun `viaFabricPlus$canWaterSprint`(): kotlin.Boolean {
        return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2) || (!this.isInWater() || this.isUnderWater())
    }
    
}
