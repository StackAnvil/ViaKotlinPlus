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
package com.viaversion.viafabricplus.injection.mixin.features.v1_20_5.entity

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(net.minecraft.world.entity.Mob::class) 
abstract class MixinMob {

    @org.spongepowered.asm.mixin.Shadow protected abstract fun checkAndHandleImportantInteractions(player: net.minecraft.world.entity.player.Player, hand: net.minecraft.world.InteractionHand): net.minecraft.world.InteractionResult
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["interact"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;checkAndHandleImportantInteractions(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;")]) private fun moveItemInteractionsAfterLeashing(instance: net.minecraft.world.entity.Mob, player: net.minecraft.world.entity.player.Player, hand: net.minecraft.world.InteractionHand): net.minecraft.world.InteractionResult {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20_5)) {
            return net.minecraft.world.InteractionResult.FAIL
        } else {
            return checkAndHandleImportantInteractions(player, hand)
        }
    }
    
    @Inject(method = ["interact"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;mobInteract(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;")], cancellable = true) private fun moveItemInteractionsAfterLeashing(player: net.minecraft.world.entity.player.Player, hand: net.minecraft.world.InteractionHand, location: net.minecraft.world.phys.Vec3, cir: CallbackInfoReturnable<net.minecraft.world.InteractionResult>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20_5)) {
            val result: net.minecraft.world.InteractionResult = checkAndHandleImportantInteractions(player, hand)
            if (result.consumesAction()) {
                cir.setReturnValue(result)
            }
        }
    }
    
}
