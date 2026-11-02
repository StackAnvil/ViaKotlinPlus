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
package com.viaversion.viafabricplus.injection.mixin.features.v26_2

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
import com.viaversion.viafabricplus.injection.access.v26_2.ILivingEntity

@Mixin(net.minecraft.client.Minecraft::class) 
abstract class MixinMinecraft {

    @com.llamalad7.mixinextras.injector.v2.WrapWithCondition(method = ["startAttack"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V")]) private fun dontSendPunch(instance: net.minecraft.client.multiplayer.ClientPacketListener, packet: net.minecraft.network.protocol.Packet<*>): kotlin.Boolean {
        // 26.2 and older clients sent the swing packet from LocalPlayer#swing instead
        return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v26_3)
    }
    
    @WrapOperation(method = ["continueAttack"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;swing(Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/component/SwingAnimation;Z)Z")]) private fun dontSendSwing(instance: net.minecraft.client.player.LocalPlayer, hand: net.minecraft.world.InteractionHand, animation: net.minecraft.world.item.component.SwingAnimation, sendToSwingingEntity: kotlin.Boolean, original: com.llamalad7.mixinextras.injector.wrapoperation.Operation<kotlin.Boolean>): kotlin.Boolean {
        // The punch packet sent right after becomes the swing packet in VV, which also uses it to emulate the block breaking effects
        (instance as ILivingEntity).`viaFabricPlus$setSkipSwingPacket`(true)
        try {
            return original.call(instance, hand, animation, sendToSwingingEntity)
        }finally {
            (instance as ILivingEntity).`viaFabricPlus$setSkipSwingPacket`(false)
        }
    }
    
    @com.llamalad7.mixinextras.injector.v2.WrapWithCondition(method = ["pauseGame"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;stopDestroyBlock()V")]) private fun dontStopDestroyingOnPause(instance: net.minecraft.client.multiplayer.MultiPlayerGameMode): kotlin.Boolean {
        return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v26_3)
    }
    
}
