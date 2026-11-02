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
package com.viaversion.viafabricplus.injection.mixin.features.v1_8

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.objectweb.asm.Opcodes
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation

@Mixin(net.minecraft.client.Minecraft::class) 
abstract class MixinMinecraft {

    @field:org.spongepowered.asm.mixin.Shadow 
    @JvmField var player: net.minecraft.client.player.LocalPlayer = uninitialized()
    @field:org.spongepowered.asm.mixin.Shadow 
    @JvmField var missTime: kotlin.Int = 0
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["tick"], at = [At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;missTime:I", ordinal = 1, opcode = Opcodes.GETFIELD)]) private fun moveCooldownIncrement(instance: net.minecraft.client.Minecraft): kotlin.Int {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8)) {
            return 0
        } else {
            return missTime
        }
    }
    
    @Inject(method = ["tick"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;handleKeybinds()V")]) private fun moveCooldownIncrement(ci: CallbackInfo) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8)) {
            if (this.missTime > 0) {
                --this.missTime
            }
        }
    }
    
    @Inject(method = ["startAttack"], at = [At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;hitResult:Lnet/minecraft/world/phys/HitResult;", ordinal = 0, opcode = Opcodes.GETFIELD)]) private fun fixSwingPacketOrder(cir: CallbackInfoReturnable<kotlin.Boolean>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8)) {
            this.player.swing(net.minecraft.world.InteractionHand.MAIN_HAND, this.player.getItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND).getAttackAnimation(), false)
        }
    }
    
    @WrapOperation(method = ["startAttack"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;swing(Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/component/SwingAnimation;Z)Z")]) private fun fixSwingPacketOrder(instance: net.minecraft.client.player.LocalPlayer, hand: net.minecraft.world.InteractionHand, animation: net.minecraft.world.item.component.SwingAnimation, sendToSwingingEntity: kotlin.Boolean, original: com.llamalad7.mixinextras.injector.wrapoperation.Operation<kotlin.Boolean>): kotlin.Boolean {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8)) {
            return original.call(instance, hand, animation, sendToSwingingEntity)
        } else {
            return false
        }
    }
    
}
