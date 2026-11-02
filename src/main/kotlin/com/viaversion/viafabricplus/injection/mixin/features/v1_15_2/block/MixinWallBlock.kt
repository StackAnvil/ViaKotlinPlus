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
package com.viaversion.viafabricplus.injection.mixin.features.v1_15_2.block

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(net.minecraft.world.level.block.WallBlock::class) 
abstract class MixinWallBlock {

    @Inject(method = ["getStateForPlacement"], at = [At("RETURN")], cancellable = true) private fun modifyPlacementState(cir: CallbackInfoReturnable<net.minecraft.world.level.block.state.BlockState>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_15_2)) {
            cir.setReturnValue(com.viaversion.viafabricplus.injection.mixin.features.v1_15_2.block.MixinWallBlock.Companion.`viaFabricPlus$oldWallPlacementLogic`(cir.getReturnValue()))
        }
    }
    
    @Inject(method = ["updateShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/world/level/ScheduledTickAccess;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/util/RandomSource;)Lnet/minecraft/world/level/block/state/BlockState;"], at = [At("RETURN")], cancellable = true) private fun modifyBlockState(cir: CallbackInfoReturnable<net.minecraft.world.level.block.state.BlockState>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_15_2)) {
            cir.setReturnValue(com.viaversion.viafabricplus.injection.mixin.features.v1_15_2.block.MixinWallBlock.Companion.`viaFabricPlus$oldWallPlacementLogic`(cir.getReturnValue()))
        }
    }
    
    private companion object {
        @JvmStatic @org.spongepowered.asm.mixin.Unique fun `viaFabricPlus$oldWallPlacementLogic`(state: net.minecraft.world.level.block.state.BlockState): net.minecraft.world.level.block.state.BlockState {
            var state: net.minecraft.world.level.block.state.BlockState = state
            var addUp: kotlin.Boolean = false
            if (state.getValue<net.minecraft.world.level.block.state.properties.WallSide>(net.minecraft.world.level.block.WallBlock.NORTH) == net.minecraft.world.level.block.state.properties.WallSide.TALL) {
                state = state.setValue<net.minecraft.world.level.block.state.properties.WallSide, net.minecraft.world.level.block.state.properties.WallSide>(net.minecraft.world.level.block.WallBlock.NORTH, net.minecraft.world.level.block.state.properties.WallSide.LOW)
                addUp = true
            }
            if (state.getValue<net.minecraft.world.level.block.state.properties.WallSide>(net.minecraft.world.level.block.WallBlock.EAST) == net.minecraft.world.level.block.state.properties.WallSide.TALL) {
                state = state.setValue<net.minecraft.world.level.block.state.properties.WallSide, net.minecraft.world.level.block.state.properties.WallSide>(net.minecraft.world.level.block.WallBlock.EAST, net.minecraft.world.level.block.state.properties.WallSide.LOW)
                addUp = true
            }
            if (state.getValue<net.minecraft.world.level.block.state.properties.WallSide>(net.minecraft.world.level.block.WallBlock.SOUTH) == net.minecraft.world.level.block.state.properties.WallSide.TALL) {
                state = state.setValue<net.minecraft.world.level.block.state.properties.WallSide, net.minecraft.world.level.block.state.properties.WallSide>(net.minecraft.world.level.block.WallBlock.SOUTH, net.minecraft.world.level.block.state.properties.WallSide.LOW)
                addUp = true
            }
            if (state.getValue<net.minecraft.world.level.block.state.properties.WallSide>(net.minecraft.world.level.block.WallBlock.WEST) == net.minecraft.world.level.block.state.properties.WallSide.TALL) {
                state = state.setValue<net.minecraft.world.level.block.state.properties.WallSide, net.minecraft.world.level.block.state.properties.WallSide>(net.minecraft.world.level.block.WallBlock.WEST, net.minecraft.world.level.block.state.properties.WallSide.LOW)
                addUp = true
            }
            if (addUp) {
                state = state.setValue<kotlin.Boolean, kotlin.Boolean>(net.minecraft.world.level.block.WallBlock.UP, true)
            }
            return state
        }
        
    }}
