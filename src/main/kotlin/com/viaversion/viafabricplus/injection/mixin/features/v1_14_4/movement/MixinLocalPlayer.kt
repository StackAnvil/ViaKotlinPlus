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
package com.viaversion.viafabricplus.injection.mixin.features.v1_14_4.movement

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(net.minecraft.client.player.LocalPlayer::class) 
abstract class MixinLocalPlayer {

    @org.spongepowered.asm.mixin.Shadow 
    @JvmField var input: net.minecraft.client.player.ClientInput = uninitialized()
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["modifyInput"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isMovingSlowly()Z")]) private fun changeSneakSlowdownCondition(instance: net.minecraft.client.player.LocalPlayer): kotlin.Boolean {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_13_2)) {
            return instance.input.keyPresses.shift()
        } else if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_14_4)) {
            return !net.minecraft.client.Minecraft.getInstance().player.isSpectator() && (instance.input.keyPresses.shift() || instance.isMovingSlowly())
        } else {
            return instance.isMovingSlowly()
        }
    }
    
    @Inject(method = ["aiStep()V"], slice = [org.spongepowered.asm.mixin.injection.Slice(from = At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isControlledCamera()Z"))], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Input;shift()Z", ordinal = 0)]) private fun undoSneakSlowdownForFly(ci: CallbackInfo) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().betweenInclusive(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_9, com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_14_4)) {
            if (this.input.keyPresses.shift()) {
                val movementSideways: kotlin.Float = (this.input.moveVector.x.toDouble() / 0.3).toFloat()
                val movementForward: kotlin.Float = (this.input.moveVector.y.toDouble() / 0.3).toFloat()
                this.input.moveVector = net.minecraft.world.phys.Vec2(movementSideways, movementForward)
            }
        }
    }
    
}
