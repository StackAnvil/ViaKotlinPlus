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
package com.viaversion.viafabricplus.injection.mixin.features.b1_7

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import net.raphimc.vialegacy.api.LegacyProtocolVersion

@Mixin(net.minecraft.world.item.BowItem::class) 
abstract class MixinBowItem {

    @Inject(method = ["getUseDuration"], at = [At("HEAD")], cancellable = true) private fun makeInstantUsable_Time(cir: CallbackInfoReturnable<kotlin.Int>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(LegacyProtocolVersion.b1_7tob1_7_3)) {
            cir.setReturnValue(0)
        }
    }
    
    @Inject(method = ["getUseAnimation"], at = [At("HEAD")], cancellable = true) private fun makeInstantUsable_Action(cir: CallbackInfoReturnable<net.minecraft.world.item.ItemUseAnimation>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(LegacyProtocolVersion.b1_7tob1_7_3)) {
            cir.setReturnValue(net.minecraft.world.item.ItemUseAnimation.NONE)
        }
    }
    
    @Inject(method = ["use"], at = [At("HEAD")], cancellable = true) private fun makeInstantUsable(level: net.minecraft.world.level.Level, player: net.minecraft.world.entity.player.Player, hand: net.minecraft.world.InteractionHand, cir: CallbackInfoReturnable<net.minecraft.world.InteractionResult>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(LegacyProtocolVersion.b1_7tob1_7_3)) {
            val arrowStack: net.minecraft.world.item.ItemStack = player.getProjectile(player.getItemInHand(hand))
            if (arrowStack.isEmpty()) {
                cir.setReturnValue(net.minecraft.world.InteractionResult.FAIL)
            } else {
                arrowStack.shrink(1)
                cir.setReturnValue(net.minecraft.world.InteractionResult.PASS)
            }
        }
    }
    
}
