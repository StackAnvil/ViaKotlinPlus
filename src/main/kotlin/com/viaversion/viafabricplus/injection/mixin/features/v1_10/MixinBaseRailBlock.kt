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
package com.viaversion.viafabricplus.injection.mixin.features.v1_10

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.objectweb.asm.Opcodes
import com.viaversion.viafabricplus.injection.mixin.features.v1_10.MixinBaseRailBlock
import org.spongepowered.asm.mixin.Final

@Mixin(net.minecraft.world.level.block.BaseRailBlock::class) 
abstract class MixinBaseRailBlock (settings: net.minecraft.world.level.block.state.BlockBehaviour.Properties) : net.minecraft.world.level.block.Block(settings) {

    @org.spongepowered.asm.mixin.injection.Redirect(method = ["getShape"], at = [At(value = "FIELD", target = "Lnet/minecraft/world/level/block/BaseRailBlock;SHAPE_SLOPE:Lnet/minecraft/world/phys/shapes/VoxelShape;", opcode = Opcodes.GETSTATIC)]) private fun changeOutlineShape(): net.minecraft.world.phys.shapes.VoxelShape {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().equalTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_10)) {
            return MixinBaseRailBlock.Companion.`viaFabricPlus$ascending_shape_r1_10_x`
        } else if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().betweenInclusive(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_9, com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_9_3)) {
            return MixinBaseRailBlock.Companion.`viaFabricPlus$ascending_shape_r1_9_x`
        } else if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8)) {
            return MixinBaseRailBlock.Companion.`viaFabricPlus$ascending_shape_r1_8_x`
        } else {
            return MixinBaseRailBlock.Companion.SHAPE_SLOPE
        }
    }
    
    private companion object {
        @field:org.spongepowered.asm.mixin.Shadow @Final 
        @JvmField val SHAPE_SLOPE: net.minecraft.world.phys.shapes.VoxelShape = uninitialized()
        @field:org.spongepowered.asm.mixin.Unique 
        @JvmField val `viaFabricPlus$ascending_shape_r1_10_x`: net.minecraft.world.phys.shapes.VoxelShape = net.minecraft.world.phys.shapes.Shapes.block()
        
        @field:org.spongepowered.asm.mixin.Unique 
        @JvmField val `viaFabricPlus$ascending_shape_r1_9_x`: net.minecraft.world.phys.shapes.VoxelShape = net.minecraft.world.level.block.Block.box(0.0, 0.0, 0.0, 16.0, 2.5, 16.0)
        
        @field:org.spongepowered.asm.mixin.Unique 
        @JvmField val `viaFabricPlus$ascending_shape_r1_8_x`: net.minecraft.world.phys.shapes.VoxelShape = net.minecraft.world.level.block.Block.box(0.0, 0.0, 0.0, 16.0, 10.0, 16.0)
        
    }}
