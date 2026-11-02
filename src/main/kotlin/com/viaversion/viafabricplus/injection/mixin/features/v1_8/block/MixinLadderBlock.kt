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
package com.viaversion.viafabricplus.injection.mixin.features.v1_8.block

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.objectweb.asm.Opcodes
import com.viaversion.viafabricplus.injection.mixin.features.v1_8.block.MixinLadderBlock
import org.spongepowered.asm.mixin.Final

@Mixin(net.minecraft.world.level.block.LadderBlock::class) 
abstract class MixinLadderBlock (settings: net.minecraft.world.level.block.state.BlockBehaviour.Properties) : net.minecraft.world.level.block.Block(settings) {

    @org.spongepowered.asm.mixin.injection.Redirect(method = ["getShape"], at = [At(value = "FIELD", target = "Lnet/minecraft/world/level/block/LadderBlock;SHAPES:Ljava/util/Map;", opcode = Opcodes.GETSTATIC)]) private fun changeOutlineShape(): kotlin.collections.MutableMap<net.minecraft.core.Direction, net.minecraft.world.phys.shapes.VoxelShape> {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8)) {
            return MixinLadderBlock.Companion.`viaFabricPlus$shapes_r1_8_x`
        } else {
            return MixinLadderBlock.Companion.SHAPES
        }
    }
    
    public override fun getOcclusionShape(state: net.minecraft.world.level.block.state.BlockState): net.minecraft.world.phys.shapes.VoxelShape {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8)) {
            return MixinLadderBlock.Companion.SHAPES!!.get(state.getValue<net.minecraft.core.Direction>(MixinLadderBlock.Companion.FACING))!!
        } else {
            return super.getOcclusionShape(state)
        }
    }
    
    private companion object {
        @field:org.spongepowered.asm.mixin.Shadow @Final 
        @JvmField var SHAPES: kotlin.collections.MutableMap<net.minecraft.core.Direction, net.minecraft.world.phys.shapes.VoxelShape> = uninitialized()
        @field:org.spongepowered.asm.mixin.Shadow @Final 
        @JvmField var FACING: net.minecraft.world.level.block.state.properties.EnumProperty<net.minecraft.core.Direction> = uninitialized()
        @field:org.spongepowered.asm.mixin.Unique 
        @JvmField val `viaFabricPlus$shapes_r1_8_x`: kotlin.collections.MutableMap<net.minecraft.core.Direction, net.minecraft.world.phys.shapes.VoxelShape> = java.util.Map.of<net.minecraft.core.Direction, net.minecraft.world.phys.shapes.VoxelShape>(
        net.minecraft.core.Direction.NORTH, net.minecraft.world.level.block.Block.box(0.0, 0.0, 14.0, 16.0, 16.0, 16.0), 
        net.minecraft.core.Direction.SOUTH, net.minecraft.world.level.block.Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 2.0), 
        net.minecraft.core.Direction.WEST, net.minecraft.world.level.block.Block.box(14.0, 0.0, 0.0, 16.0, 16.0, 16.0), 
        net.minecraft.core.Direction.EAST, net.minecraft.world.level.block.Block.box(0.0, 0.0, 0.0, 2.0, 16.0, 16.0)
        )
        
    }}
