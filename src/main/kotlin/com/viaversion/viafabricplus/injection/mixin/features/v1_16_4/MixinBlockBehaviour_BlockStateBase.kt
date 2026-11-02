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
package com.viaversion.viafabricplus.injection.mixin.features.v1_16_4

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import net.raphimc.vialegacy.api.LegacyProtocolVersion

@Mixin(net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase::class) 
abstract class MixinBlockBehaviour_BlockStateBase {

    @org.spongepowered.asm.mixin.Shadow abstract fun getBlock(): net.minecraft.world.level.block.Block
    
    @Inject(method = ["getDestroySpeed"], at = [At("RETURN")], cancellable = true) private fun changeHardness(level: net.minecraft.world.level.BlockGetter, pos: net.minecraft.core.BlockPos, cir: CallbackInfoReturnable<kotlin.Float>) {
        val block: net.minecraft.world.level.block.Block = this.getBlock()
        
        if (block == net.minecraft.world.level.block.Blocks.END_STONE_BRICKS || block == net.minecraft.world.level.block.Blocks.END_STONE_BRICK_SLAB || block == net.minecraft.world.level.block.Blocks.END_STONE_BRICK_STAIRS || block == net.minecraft.world.level.block.Blocks.END_STONE_BRICK_WALL) {
            if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_14_4)) {
                cir.setReturnValue(0.8f)
            }
        } else if (block == net.minecraft.world.level.block.Blocks.PISTON || block == net.minecraft.world.level.block.Blocks.STICKY_PISTON || block == net.minecraft.world.level.block.Blocks.PISTON_HEAD) {
            if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_15_2)) {
                cir.setReturnValue(0.5f)
            }
        } else if (block is net.minecraft.world.level.block.InfestedBlock) {
            if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)) {
                cir.setReturnValue(0.75f)
            } else if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_16_4)) {
                cir.setReturnValue(0f)
            }
        } else if (block == net.minecraft.world.level.block.Blocks.OBSIDIAN) {
            if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(LegacyProtocolVersion.b1_8tob1_8_1)) {
                cir.setReturnValue(10.0f)
            }
        }
    }
    
}
