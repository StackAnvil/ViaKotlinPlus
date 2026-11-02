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

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(net.minecraft.client.player.LocalPlayer::class) 
abstract class MixinLocalPlayer (world: net.minecraft.client.multiplayer.ClientLevel, profile: com.mojang.authlib.GameProfile) : net.minecraft.client.player.AbstractClientPlayer(world, profile) {

    @org.spongepowered.asm.mixin.Shadow 
    @JvmField var input: net.minecraft.client.player.ClientInput = uninitialized()
    @org.spongepowered.asm.mixin.Shadow protected abstract fun modifyInput(input: net.minecraft.world.phys.Vec2): net.minecraft.world.phys.Vec2
    
    @org.spongepowered.asm.mixin.Shadow protected abstract fun shouldStopRunSprinting(): kotlin.Boolean
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["applyInput"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;modifyInput(Lnet/minecraft/world/phys/Vec2;)Lnet/minecraft/world/phys/Vec2;")]) private fun moveMovementSpeedFactors(instance: net.minecraft.client.player.LocalPlayer, input: net.minecraft.world.phys.Vec2): net.minecraft.world.phys.Vec2 {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_4)) {
            return input
        } else {
            return this.modifyInput(input)
        }
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["modifyInput"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;modifyInputSpeedForSquareMovement(Lnet/minecraft/world/phys/Vec2;)Lnet/minecraft/world/phys/Vec2;")]) private fun moveMovementSpeedFactors(vec: net.minecraft.world.phys.Vec2): net.minecraft.world.phys.Vec2 {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_4)) {
            return vec
        } else {
            return com.viaversion.viafabricplus.injection.mixin.features.v1_21_4.movement.MixinLocalPlayer.Companion.modifyInputSpeedForSquareMovement(vec)
        }
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["modifyInput"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec2;scale(F)Lnet/minecraft/world/phys/Vec2;", ordinal = 0)]) private fun moveMovementSpeedFactors(instance: net.minecraft.world.phys.Vec2, s: kotlin.Float): net.minecraft.world.phys.Vec2 {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_4)) {
            return instance
        } else {
            return instance.scale(s)
        }
    }
    
    @Inject(method = ["aiStep"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/Tutorial;onInput(Lnet/minecraft/client/player/ClientInput;)V", shift = At.Shift.AFTER)]) private fun moveMovementSpeedFactors(ci: CallbackInfo) {
        //... and also add this hotfix back
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion() == com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_4 && this.shouldStopRunSprinting()) {
            this.setSprinting(false)
        }
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_4)) {
            this.input.moveVector = this.modifyInput(this.input.moveVector)
        }
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["aiStep"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Input;backward()Z")]) private fun dontResetDoubleTapTicks(instance: net.minecraft.world.entity.player.Input): kotlin.Boolean {
        return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_4) && instance.backward()
    }
    
    private companion object {
        @JvmStatic @org.spongepowered.asm.mixin.Shadow fun modifyInputSpeedForSquareMovement(vec: net.minecraft.world.phys.Vec2): net.minecraft.world.phys.Vec2 {
            return uninitialized()
        }
        
    }}
