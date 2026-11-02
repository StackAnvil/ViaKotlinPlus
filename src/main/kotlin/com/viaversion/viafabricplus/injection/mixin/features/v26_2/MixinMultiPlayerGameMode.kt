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
import org.objectweb.asm.Opcodes

@Mixin(net.minecraft.client.multiplayer.MultiPlayerGameMode::class) 
abstract class MixinMultiPlayerGameMode {

    @com.llamalad7.mixinextras.injector.v2.WrapWithCondition(method = ["lambda\$startDestroyBlock$1"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;addBreakingBlockEffects(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Z)V")]) private fun dontAddStartBreakingEffects(instance: net.minecraft.client.multiplayer.ClientLevel, pos: net.minecraft.core.BlockPos, direction: net.minecraft.core.Direction, playSound: kotlin.Boolean): kotlin.Boolean {
        // 26.2 only spawned breaking effects while continuing to destroy, see MixinMinecraft
        return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v26_3)
    }
    
    @com.llamalad7.mixinextras.injector.v2.WrapWithCondition(method = ["attack"], at = [At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;destroyDelay:I")]) private fun dontDelayDestroyingAfterAttacking(instance: net.minecraft.client.multiplayer.MultiPlayerGameMode, value: kotlin.Int): kotlin.Boolean {
        return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v26_3)
    }
    
    @com.llamalad7.mixinextras.injector.v2.WrapWithCondition(method = ["performUseItemOn"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;setItemInHand(Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;)V")]) private fun dontPredictTransformedItems(instance: net.minecraft.client.player.LocalPlayer, hand: net.minecraft.world.InteractionHand, itemStack: net.minecraft.world.item.ItemStack): kotlin.Boolean {
        // 26.3 predicts the item the interaction transformed the held one into, 26.2 waited for the server
        return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v26_3)
    }
    
}
