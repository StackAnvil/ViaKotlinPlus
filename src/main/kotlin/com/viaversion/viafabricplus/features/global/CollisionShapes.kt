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
package com.viaversion.viafabricplus.features.global

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.AnvilBlock
import net.minecraft.world.level.block.BedBlock
import net.minecraft.world.level.block.BrewingStandBlock
import net.minecraft.world.level.block.CarpetBlock
import net.minecraft.world.level.block.CauldronBlock
import net.minecraft.world.level.block.ChestBlock
import net.minecraft.world.level.block.CropBlock
import net.minecraft.world.level.block.EndPortalBlock
import net.minecraft.world.level.block.EndPortalFrameBlock
import net.minecraft.world.level.block.EnderChestBlock
import net.minecraft.world.level.block.FarmlandBlock
import net.minecraft.world.level.block.FenceBlock
import net.minecraft.world.level.block.FenceGateBlock
import net.minecraft.world.level.block.FlowerBedBlock
import net.minecraft.world.level.block.HopperBlock
import net.minecraft.world.level.block.IronBarsBlock
import net.minecraft.world.level.block.LadderBlock
import net.minecraft.world.level.block.LeavesBlock
import net.minecraft.world.level.block.LilyPadBlock
import net.minecraft.world.level.block.SnowLayerBlock
import net.minecraft.world.level.block.WallBlock
import net.minecraft.world.level.block.piston.PistonBaseBlock
import net.minecraft.world.level.block.piston.PistonHeadBlock

object CollisionShapes {
    fun reloadBlockShapes() {
        for (block in BuiltInRegistries.BLOCK) {
            if (block is AnvilBlock || block is BedBlock || block is BrewingStandBlock
            || block is CarpetBlock || block is CauldronBlock || block is ChestBlock
            || block is EnderChestBlock || block is EndPortalBlock || block is EndPortalFrameBlock
            || block is FarmlandBlock || block is FenceBlock || block is FenceGateBlock
            || block is HopperBlock || block is LadderBlock || block is LeavesBlock
            || block is LilyPadBlock || block is IronBarsBlock || block is PistonBaseBlock
            || block is PistonHeadBlock || block is SnowLayerBlock || block is WallBlock
            || block is CropBlock || block is FlowerBedBlock
            ) {
                for (state in block.getStateDefinition().getPossibleStates()) {
                    state.initCache()
                }
            }
        }
    }
 }
