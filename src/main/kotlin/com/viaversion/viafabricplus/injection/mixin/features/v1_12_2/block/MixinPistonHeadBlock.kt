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
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import com.viaversion.viafabricplus.injection.mixin.features.v1_12_2.block.MixinPistonHeadBlock

@Mixin(net.minecraft.world.level.block.piston.PistonHeadBlock::class) 
abstract class MixinPistonHeadBlock protected constructor(settings: net.minecraft.world.level.block.state.BlockBehaviour.Properties) : net.minecraft.world.level.block.DirectionalBlock(settings) {

    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$selfInflicted`: kotlin.Boolean = false
    
    @Inject(method = ["getShape"], at = [At("HEAD")], cancellable = true) private fun changeOutlineShape(state: net.minecraft.world.level.block.state.BlockState, level: net.minecraft.world.level.BlockGetter, pos: net.minecraft.core.BlockPos, context: net.minecraft.world.phys.shapes.CollisionContext, cir: CallbackInfoReturnable<net.minecraft.world.phys.shapes.VoxelShape>) {
        if (`viaFabricPlus$selfInflicted`) {
            `viaFabricPlus$selfInflicted` = false
            return 
        }
                // Outline shape for piston head doesn't exist in <= 1.12.2
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)) {
            cir.setReturnValue(when (state.getValue<net.minecraft.core.Direction>(net.minecraft.world.level.block.piston.PistonHeadBlock.FACING)){net.minecraft.core.Direction.DOWN -> MixinPistonHeadBlock.Companion.`viaFabricPlus$down_head_shape`
                net.minecraft.core.Direction.UP -> MixinPistonHeadBlock.Companion.`viaFabricPlus$up_head_shape`
                net.minecraft.core.Direction.NORTH -> MixinPistonHeadBlock.Companion.`viaFabricPlus$north_head_shape`
                net.minecraft.core.Direction.SOUTH -> MixinPistonHeadBlock.Companion.`viaFabricPlus$south_head_shape`
                net.minecraft.core.Direction.WEST -> MixinPistonHeadBlock.Companion.`viaFabricPlus$west_head_shape`
                net.minecraft.core.Direction.EAST -> MixinPistonHeadBlock.Companion.`viaFabricPlus$east_head_shape`})
        }
    }
    
    public override fun getCollisionShape(state: net.minecraft.world.level.block.state.BlockState, world: net.minecraft.world.level.BlockGetter, pos: net.minecraft.core.BlockPos, context: net.minecraft.world.phys.shapes.CollisionContext): net.minecraft.world.phys.shapes.VoxelShape {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8)) {
            return when (state.getValue<net.minecraft.core.Direction>(net.minecraft.world.level.block.piston.PistonHeadBlock.FACING)){net.minecraft.core.Direction.DOWN -> net.minecraft.world.phys.shapes.Shapes.or(MixinPistonHeadBlock.Companion.`viaFabricPlus$down_head_shape`, MixinPistonHeadBlock.Companion.`viaFabricPlus$down_arm_shape_r1_8_x`)
                net.minecraft.core.Direction.UP -> net.minecraft.world.phys.shapes.Shapes.or(MixinPistonHeadBlock.Companion.`viaFabricPlus$up_head_shape`, MixinPistonHeadBlock.Companion.`viaFabricPlus$up_arm_shape_r1_8_x`)
                net.minecraft.core.Direction.NORTH -> net.minecraft.world.phys.shapes.Shapes.or(MixinPistonHeadBlock.Companion.`viaFabricPlus$north_head_shape`, MixinPistonHeadBlock.Companion.`viaFabricPlus$north_arm_shape_r1_8_x`)
                net.minecraft.core.Direction.SOUTH -> net.minecraft.world.phys.shapes.Shapes.or(MixinPistonHeadBlock.Companion.`viaFabricPlus$south_head_shape`, MixinPistonHeadBlock.Companion.`viaFabricPlus$south_arm_shape_r1_8_x`)
                net.minecraft.core.Direction.WEST -> net.minecraft.world.phys.shapes.Shapes.or(MixinPistonHeadBlock.Companion.`viaFabricPlus$west_head_shape`, MixinPistonHeadBlock.Companion.`viaFabricPlus$west_arm_shape_r1_8_x`)
                net.minecraft.core.Direction.EAST -> net.minecraft.world.phys.shapes.Shapes.or(MixinPistonHeadBlock.Companion.`viaFabricPlus$east_head_shape`, MixinPistonHeadBlock.Companion.`viaFabricPlus$east_arm_shape_r1_8_x`)}
        } else if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)) {
            // Collision shape for piston head in <= 1.12.2 needs to be the 1.13+ outline shape
            `viaFabricPlus$selfInflicted` = true
            return getShape(state, world, pos, context)
        } else {
            return super.getCollisionShape(state, world, pos, context)
        }
    }
    
    private companion object {
        @field:org.spongepowered.asm.mixin.Unique 
        @JvmField val `viaFabricPlus$east_head_shape`: net.minecraft.world.phys.shapes.VoxelShape = net.minecraft.world.level.block.Block.box(12.0, 0.0, 0.0, 16.0, 16.0, 16.0)
        
        @field:org.spongepowered.asm.mixin.Unique 
        @JvmField val `viaFabricPlus$west_head_shape`: net.minecraft.world.phys.shapes.VoxelShape = net.minecraft.world.level.block.Block.box(0.0, 0.0, 0.0, 4.0, 16.0, 16.0)
        
        @field:org.spongepowered.asm.mixin.Unique 
        @JvmField val `viaFabricPlus$south_head_shape`: net.minecraft.world.phys.shapes.VoxelShape = net.minecraft.world.level.block.Block.box(0.0, 0.0, 12.0, 16.0, 16.0, 16.0)
        
        @field:org.spongepowered.asm.mixin.Unique 
        @JvmField val `viaFabricPlus$north_head_shape`: net.minecraft.world.phys.shapes.VoxelShape = net.minecraft.world.level.block.Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 4.0)
        
        @field:org.spongepowered.asm.mixin.Unique 
        @JvmField val `viaFabricPlus$up_head_shape`: net.minecraft.world.phys.shapes.VoxelShape = net.minecraft.world.level.block.Block.box(0.0, 12.0, 0.0, 16.0, 16.0, 16.0)
        
        @field:org.spongepowered.asm.mixin.Unique 
        @JvmField val `viaFabricPlus$down_head_shape`: net.minecraft.world.phys.shapes.VoxelShape = net.minecraft.world.level.block.Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0)
        
        @field:org.spongepowered.asm.mixin.Unique 
        @JvmField val `viaFabricPlus$up_arm_shape_r1_8_x`: net.minecraft.world.phys.shapes.VoxelShape = net.minecraft.world.level.block.Block.box(6.0, 0.0, 6.0, 10.0, 12.0, 10.0)
        
        @field:org.spongepowered.asm.mixin.Unique 
        @JvmField val `viaFabricPlus$down_arm_shape_r1_8_x`: net.minecraft.world.phys.shapes.VoxelShape = net.minecraft.world.level.block.Block.box(6.0, 4.0, 6.0, 10.0, 16.0, 10.0)
        
        @field:org.spongepowered.asm.mixin.Unique 
        @JvmField val `viaFabricPlus$south_arm_shape_r1_8_x`: net.minecraft.world.phys.shapes.VoxelShape = net.minecraft.world.level.block.Block.box(4.0, 6.0, 0.0, 12.0, 10.0, 12.0)
        
        @field:org.spongepowered.asm.mixin.Unique 
        @JvmField val `viaFabricPlus$north_arm_shape_r1_8_x`: net.minecraft.world.phys.shapes.VoxelShape = net.minecraft.world.level.block.Block.box(4.0, 6.0, 4.0, 12.0, 10.0, 16.0)
        
        @field:org.spongepowered.asm.mixin.Unique 
        @JvmField val `viaFabricPlus$east_arm_shape_r1_8_x`: net.minecraft.world.phys.shapes.VoxelShape = net.minecraft.world.level.block.Block.box(0.0, 6.0, 4.0, 12.0, 10.0, 12.0)
        
        @field:org.spongepowered.asm.mixin.Unique 
        @JvmField val `viaFabricPlus$west_arm_shape_r1_8_x`: net.minecraft.world.phys.shapes.VoxelShape = net.minecraft.world.level.block.Block.box(6.0, 4.0, 4.0, 10.0, 12.0, 16.0)
        
    }}
