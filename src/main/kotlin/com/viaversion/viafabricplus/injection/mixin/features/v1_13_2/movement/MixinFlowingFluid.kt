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
package com.viaversion.viafabricplus.injection.mixin.features.v1_13_2.movement

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import com.viaversion.viafabricplus.protocoltranslator.impl.ViaFabricPlusMappingDataLoader

@Mixin(net.minecraft.world.level.material.FlowingFluid::class) 
abstract class MixinFlowingFluid {

    @org.spongepowered.asm.mixin.injection.Redirect(method = ["isSolidFace"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;isFaceSturdy(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z")]) private fun modifyIsSolidBlock(instance: net.minecraft.world.level.block.state.BlockState, blockView: net.minecraft.world.level.BlockGetter, blockPos: net.minecraft.core.BlockPos, direction: net.minecraft.core.Direction): kotlin.Boolean {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_11_1)) {
            val material: ViaFabricPlusMappingDataLoader.Material = ViaFabricPlusMappingDataLoader.MATERIALS.get(ViaFabricPlusMappingDataLoader.getBlockMaterial(instance.getBlock()))!!
            return material.solid
        } else if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_13_2)) {
            val block: net.minecraft.world.level.block.Block = instance.getBlock()
            if (block is net.minecraft.world.level.block.ShulkerBoxBlock || block is net.minecraft.world.level.block.LeavesBlock || block is net.minecraft.world.level.block.TrapDoorBlock || block === net.minecraft.world.level.block.Blocks.BEACON || block === net.minecraft.world.level.block.Blocks.CAULDRON || block === net.minecraft.world.level.block.Blocks.GLASS || block === net.minecraft.world.level.block.Blocks.GLOWSTONE || block === net.minecraft.world.level.block.Blocks.ICE || block === net.minecraft.world.level.block.Blocks.SEA_LANTERN || 
            block is net.minecraft.world.level.block.StainedGlassBlock || block === net.minecraft.world.level.block.Blocks.PISTON || block === net.minecraft.world.level.block.Blocks.STICKY_PISTON || block === net.minecraft.world.level.block.Blocks.PISTON_HEAD || block is net.minecraft.world.level.block.StairBlock) {
                return false
            }
        }
        return instance.isFaceSturdy(blockView, blockPos, direction)
    }
    
}
