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
package com.viaversion.viafabricplus.injection.mixin.features.v1_19_4

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(net.minecraft.world.level.block.SignBlock::class) 
abstract class MixinSignBlock {

    @Inject(method = ["useItemOn"], at = [At("HEAD")], cancellable = true) private fun changeInteractionCalculation(itemStack: net.minecraft.world.item.ItemStack, state: net.minecraft.world.level.block.state.BlockState, level: net.minecraft.world.level.Level, pos: net.minecraft.core.BlockPos, player: net.minecraft.world.entity.player.Player, hand: net.minecraft.world.InteractionHand, hitResult: net.minecraft.world.phys.BlockHitResult, cir: CallbackInfoReturnable<net.minecraft.world.InteractionResult>) {
        if (!level.isClientSide()) {
            return 
        }
        
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_14_4)) {
            // <= 1.14.4 doesn't have any sign interactions.
            cir.setReturnValue(net.minecraft.world.InteractionResult.SUCCESS)
        } else if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_19_4)) {
            // Removes the isWaxed() condition and reverts the interaction changes from 1.19.4 -> 1.20 when signs
            // got a front and back side.
            val itemInHand: net.minecraft.world.item.ItemStack = player.getItemInHand(hand)
            val item: net.minecraft.world.item.Item = itemInHand.getItem()
            val isSuccess: kotlin.Boolean = (item is net.minecraft.world.item.DyeItem || itemInHand.`is`(net.minecraft.world.item.Items.GLOW_INK_SAC) || itemInHand.`is`(net.minecraft.world.item.Items.INK_SAC)) && player.mayBuild()
            
            cir.setReturnValue(if (isSuccess) net.minecraft.world.InteractionResult.SUCCESS else net.minecraft.world.InteractionResult.CONSUME)
        }
    }
    
}
