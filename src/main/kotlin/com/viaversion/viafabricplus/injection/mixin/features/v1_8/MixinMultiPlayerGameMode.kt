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

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import com.viaversion.viafabricplus.protocoltranslator.impl.provider.viaversion.ViaFabricPlusHandItemProvider

@Mixin(net.minecraft.client.multiplayer.MultiPlayerGameMode::class) 
abstract class MixinMultiPlayerGameMode {

    @Inject(method = ["useItem"], at = [At("HEAD")], cancellable = true) private fun cancelOffHandItemInteract(player: net.minecraft.world.entity.player.Player, hand: net.minecraft.world.InteractionHand, cir: CallbackInfoReturnable<net.minecraft.world.InteractionResult>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8) && net.minecraft.world.InteractionHand.MAIN_HAND != hand) {
            cir.setReturnValue(net.minecraft.world.InteractionResult.PASS)
        }
    }
    
    @Inject(method = ["useItemOn"], at = [At("HEAD")], cancellable = true) private fun cancelOffHandBlockPlace(player: net.minecraft.client.player.LocalPlayer, hand: net.minecraft.world.InteractionHand, blockHit: net.minecraft.world.phys.BlockHitResult, cir: CallbackInfoReturnable<net.minecraft.world.InteractionResult>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8) && net.minecraft.world.InteractionHand.MAIN_HAND != hand) {
            cir.setReturnValue(net.minecraft.world.InteractionResult.PASS)
        }
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["lambda\$useItem$0"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;")]) private fun eitherSuccessOrPass(instance: net.minecraft.world.item.ItemStack, level: net.minecraft.world.level.Level, player: net.minecraft.world.entity.player.Player, hand: net.minecraft.world.InteractionHand, @com.llamalad7.mixinextras.sugar.Local(name = ["itemStack"]) itemStack: net.minecraft.world.item.ItemStack): net.minecraft.world.InteractionResult {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8)) {
            val count: kotlin.Int = instance.getCount()
            
            val actionResult: net.minecraft.world.InteractionResult = instance.use(level, player, hand)
            val output: net.minecraft.world.item.ItemStack
            if (actionResult is net.minecraft.world.InteractionResult.Success) {
                output = java.util.Objects.requireNonNullElseGet<net.minecraft.world.item.ItemStack>(actionResult.heldItemTransformedTo(), java.util.function.Supplier {player.getItemInHand(hand)})
            } else {
                output = player.getItemInHand(hand)
            }
            
                        // In <= 1.8, ActionResult weren't a thing, and interactItem simply returned either true or false
            // depending on if the input and output item are equal or not
            val accepted: kotlin.Boolean = !output.isEmpty() && (output != itemStack || output.getCount() != count)
            if (actionResult.consumesAction() == accepted) {
                return actionResult
            } else {
                return if (accepted) net.minecraft.world.InteractionResult.SUCCESS.heldItemTransformedTo(output) else net.minecraft.world.InteractionResult.PASS
            }
        } else {
            return instance.use(level, player, hand)
        }
    }
    
    @Inject(method = ["lambda\$useItem$0"], at = [At("HEAD")]) private fun trackLastUsedItem(hand: net.minecraft.world.InteractionHand, player: net.minecraft.world.entity.player.Player, interactionResult: org.apache.commons.lang3.mutable.MutableObject<net.minecraft.world.InteractionResult>, sequence: kotlin.Int, cir: CallbackInfoReturnable<net.minecraft.network.protocol.Packet<*>>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8)) {
            ViaFabricPlusHandItemProvider.lastUsedItem = player.getItemInHand(hand).copy()
        }
    }
    
}
