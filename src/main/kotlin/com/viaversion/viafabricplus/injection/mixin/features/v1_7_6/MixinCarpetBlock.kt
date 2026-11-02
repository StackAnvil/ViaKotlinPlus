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
package com.viaversion.viafabricplus.injection.mixin.features.v1_7_6

import org.spongepowered.asm.mixin.Mixin
import com.viaversion.viafabricplus.injection.mixin.features.v1_7_6.MixinCarpetBlock

@Mixin(net.minecraft.world.level.block.CarpetBlock::class) 
abstract class MixinCarpetBlock (settings: net.minecraft.world.level.block.state.BlockBehaviour.Properties) : net.minecraft.world.level.block.Block(settings) {

    public override fun getCollisionShape(state: net.minecraft.world.level.block.state.BlockState, world: net.minecraft.world.level.BlockGetter, pos: net.minecraft.core.BlockPos, context: net.minecraft.world.phys.shapes.CollisionContext): net.minecraft.world.phys.shapes.VoxelShape {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_7_6)) {
            return MixinCarpetBlock.Companion.`viaFabricPlus$shape_r1_7_10`
        } else {
            return super.getCollisionShape(state, world, pos, context)
        }
    }
    
    private companion object {
        @field:org.spongepowered.asm.mixin.Unique 
        @JvmField val `viaFabricPlus$shape_r1_7_10`: net.minecraft.world.phys.shapes.VoxelShape = net.minecraft.world.level.block.Block.box(0.0, -0.00001,  /* 0.0D */0.0, 16.0, 0.0, 16.0)
        
    }}
