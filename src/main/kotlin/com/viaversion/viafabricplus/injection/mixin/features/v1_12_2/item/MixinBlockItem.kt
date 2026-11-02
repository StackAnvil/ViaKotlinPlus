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
package com.viaversion.viafabricplus.injection.mixin.features.v1_12_2.item

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(net.minecraft.world.item.BlockItem::class) 
abstract class MixinBlockItem {

    @Inject(method = ["canPlace"], at = [At("HEAD")], cancellable = true) private fun checkChestPlacement(context: net.minecraft.world.item.context.BlockPlaceContext, stateForPlacement: net.minecraft.world.level.block.state.BlockState, cir: CallbackInfoReturnable<kotlin.Boolean>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)) {
            val block: net.minecraft.world.level.block.Block = stateForPlacement.getBlock()
            if (block === net.minecraft.world.level.block.Blocks.CHEST || block === net.minecraft.world.level.block.Blocks.TRAPPED_CHEST) {
                val world: net.minecraft.world.level.Level = context.getLevel()
                val pos: net.minecraft.core.BlockPos = context.getClickedPos()
                var foundAdjChest: kotlin.Boolean = false
                for (dir in net.minecraft.core.Direction.Plane.HORIZONTAL) {
                    val otherState: net.minecraft.world.level.block.state.BlockState = world.getBlockState(pos.relative(dir))
                    if (otherState.getBlock() === block) {
                        if (foundAdjChest) {
                            cir.setReturnValue(false)
                            return 
                        }
                        foundAdjChest = true
                        if (otherState.getValue<net.minecraft.world.level.block.state.properties.ChestType>(net.minecraft.world.level.block.ChestBlock.TYPE) != net.minecraft.world.level.block.state.properties.ChestType.SINGLE) {
                            cir.setReturnValue(false)
                            return 
                        }
                    }
                }
            }
        }
    }
    
}
