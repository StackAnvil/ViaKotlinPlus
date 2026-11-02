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
package com.viaversion.viafabricplus.injection.mixin.features.v1_21

import org.spongepowered.asm.mixin.Mixin

@Mixin(net.minecraft.world.level.block.FenceBlock::class) 
abstract class MixinFenceBlock protected constructor(radius1: kotlin.Float, radius2: kotlin.Float, boundingHeight1: kotlin.Float, boundingHeight2: kotlin.Float, collisionHeight: kotlin.Float, settings: net.minecraft.world.level.block.state.BlockBehaviour.Properties) : net.minecraft.world.level.block.CrossCollisionBlock(radius1, radius2, boundingHeight1, boundingHeight2, collisionHeight, settings) {

    override fun useItemOn(stack: net.minecraft.world.item.ItemStack, state: net.minecraft.world.level.block.state.BlockState, world: net.minecraft.world.level.Level, pos: net.minecraft.core.BlockPos, player: net.minecraft.world.entity.player.Player, hand: net.minecraft.world.InteractionHand, hit: net.minecraft.world.phys.BlockHitResult): net.minecraft.world.InteractionResult {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21)) {
            return if (stack.`is`(net.minecraft.world.item.Items.LEAD)) net.minecraft.world.InteractionResult.SUCCESS else net.minecraft.world.InteractionResult.PASS
        } else if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_10)) {
            return net.minecraft.world.InteractionResult.SUCCESS
        } else {
            return super.useItemOn(stack, state, world, pos, player, hand, hit)
        }
    }
    
}
