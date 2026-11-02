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
package com.viaversion.viafabricplus.injection.mixin.features.v1_12_2.block

import org.spongepowered.asm.mixin.Mixin
import com.viaversion.viafabricplus.injection.access.v1_14.ICrossCollisionBlock

@Mixin(net.minecraft.world.level.block.CrossCollisionBlock::class) 
abstract class MixinCrossCollisionBlock : ICrossCollisionBlock {

    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField val `viaFabricPlus$SHAPE_INDEX_CACHE`: it.unimi.dsi.fastutil.objects.Object2IntMap<net.minecraft.world.level.block.state.BlockState> = it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap<net.minecraft.world.level.block.state.BlockState>()
    
    override fun `viaFabricPlus$getShapeIndex`(blockState: net.minecraft.world.level.block.state.BlockState): kotlin.Int {
        return `viaFabricPlus$SHAPE_INDEX_CACHE`.computeIfAbsent(blockState, it.unimi.dsi.fastutil.objects.Object2IntFunction {
        `_`: kotlin.Any -> var index: kotlin.Int = 0
        if (blockState.getValue<kotlin.Boolean>(net.minecraft.world.level.block.CrossCollisionBlock.NORTH)) {
            index = index or (1 shl net.minecraft.core.Direction.NORTH.get2DDataValue())
        }
        if (blockState.getValue<kotlin.Boolean>(net.minecraft.world.level.block.CrossCollisionBlock.EAST)) {
            index = index or (1 shl net.minecraft.core.Direction.EAST.get2DDataValue())
        }
        if (blockState.getValue<kotlin.Boolean>(net.minecraft.world.level.block.CrossCollisionBlock.SOUTH)) {
            index = index or (1 shl net.minecraft.core.Direction.SOUTH.get2DDataValue())
        }
        if (blockState.getValue<kotlin.Boolean>(net.minecraft.world.level.block.CrossCollisionBlock.WEST)) {
            index = index or (1 shl net.minecraft.core.Direction.WEST.get2DDataValue())
        }
        
        index
        })
    }
    
}
