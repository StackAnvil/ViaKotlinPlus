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
package com.viaversion.viafabricplus.injection.mixin.features.v1_21_4

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import com.viaversion.viafabricplus.injection.mixin.features.v1_21_4.MixinPitcherCropBlock
import org.spongepowered.asm.mixin.Final

@Mixin(net.minecraft.world.level.block.PitcherCropBlock::class) 
abstract class MixinPitcherCropBlock (settings: net.minecraft.world.level.block.state.BlockBehaviour.Properties) : net.minecraft.world.level.block.DoublePlantBlock(settings) {

    @Inject(method = ["getShape"], at = [At("HEAD")], cancellable = true) private fun changeOutlineShape(state: net.minecraft.world.level.block.state.BlockState, level: net.minecraft.world.level.BlockGetter, pos: net.minecraft.core.BlockPos, context: net.minecraft.world.phys.shapes.CollisionContext, cir: CallbackInfoReturnable<net.minecraft.world.phys.shapes.VoxelShape>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_4)) {
            val age: kotlin.Int = state.getValue<kotlin.Int>(MixinPitcherCropBlock.Companion.AGE)
            if (state.getValue<net.minecraft.world.level.block.state.properties.DoubleBlockHalf>(MixinPitcherCropBlock.Companion.HALF) == net.minecraft.world.level.block.state.properties.DoubleBlockHalf.UPPER) {
                cir.setReturnValue(MixinPitcherCropBlock.Companion.`viaFabricPlus$upper_outline_shapes_r1_21_4`[kotlin.math.min(kotlin.math.abs(4 - (age + 1)), MixinPitcherCropBlock.Companion.`viaFabricPlus$upper_outline_shapes_r1_21_4`.size - 1)])
            } else {
                cir.setReturnValue(MixinPitcherCropBlock.Companion.`viaFabricPlus$lower_outline_shapes_r1_21_4`[age])
            }
        }
    }
    
    @Inject(method = ["getCollisionShape"], at = [At("HEAD")], cancellable = true) private fun changeBlockStatePropertyPriority(state: net.minecraft.world.level.block.state.BlockState, level: net.minecraft.world.level.BlockGetter, pos: net.minecraft.core.BlockPos, context: net.minecraft.world.phys.shapes.CollisionContext, cir: CallbackInfoReturnable<net.minecraft.world.phys.shapes.VoxelShape>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_4)) {
            if (state.getValue<kotlin.Int>(MixinPitcherCropBlock.Companion.AGE) == 0) {
                cir.setReturnValue(MixinPitcherCropBlock.Companion.SHAPE_BULB)
            } else {
                cir.setReturnValue(if (state.getValue<net.minecraft.world.level.block.state.properties.DoubleBlockHalf>(MixinPitcherCropBlock.Companion.HALF) == net.minecraft.world.level.block.state.properties.DoubleBlockHalf.LOWER) MixinPitcherCropBlock.Companion.SHAPE_CROP else super.getCollisionShape(state, level, pos, context))
            }
        }
    }
    
    private companion object {
        @field:org.spongepowered.asm.mixin.Shadow @Final 
        @JvmField var AGE: net.minecraft.world.level.block.state.properties.IntegerProperty = uninitialized()
        @field:org.spongepowered.asm.mixin.Shadow @Final 
        @JvmField var HALF: net.minecraft.world.level.block.state.properties.EnumProperty<net.minecraft.world.level.block.state.properties.DoubleBlockHalf> = uninitialized()
        @field:org.spongepowered.asm.mixin.Shadow @Final 
        @JvmField var SHAPE_BULB: net.minecraft.world.phys.shapes.VoxelShape = uninitialized()
        @field:org.spongepowered.asm.mixin.Shadow @Final 
        @JvmField val SHAPE_CROP: net.minecraft.world.phys.shapes.VoxelShape = uninitialized()
        @field:org.spongepowered.asm.mixin.Unique 
        @JvmField val `viaFabricPlus$grown_upper_outline_shape_r1_21_4`: net.minecraft.world.phys.shapes.VoxelShape = net.minecraft.world.level.block.Block.box(3.0, 0.0, 3.0, 13.0, 15.0, 13.0)
        
        @field:org.spongepowered.asm.mixin.Unique 
        @JvmField val `viaFabricPlus$grown_lower_outline_shape_r1_21_4`: net.minecraft.world.phys.shapes.VoxelShape = net.minecraft.world.level.block.Block.box(3.0, -1.0, 3.0, 13.0, 16.0, 13.0)
        
        @field:org.spongepowered.asm.mixin.Unique 
        @JvmField val `viaFabricPlus$upper_outline_shapes_r1_21_4`: kotlin.Array<net.minecraft.world.phys.shapes.VoxelShape> = kotlin.arrayOf<net.minecraft.world.phys.shapes.VoxelShape>(net.minecraft.world.level.block.Block.box(3.0, 0.0, 3.0, 13.0, 11.0, 13.0), MixinPitcherCropBlock.Companion.`viaFabricPlus$grown_upper_outline_shape_r1_21_4`)
        
        @field:org.spongepowered.asm.mixin.Unique 
        @JvmField val `viaFabricPlus$lower_outline_shapes_r1_21_4`: kotlin.Array<net.minecraft.world.phys.shapes.VoxelShape> = kotlin.arrayOf<net.minecraft.world.phys.shapes.VoxelShape>(MixinPitcherCropBlock.Companion.SHAPE_BULB, net.minecraft.world.level.block.Block.box(3.0, -1.0, 3.0, 13.0, 14.0, 13.0), MixinPitcherCropBlock.Companion.`viaFabricPlus$grown_lower_outline_shape_r1_21_4`, MixinPitcherCropBlock.Companion.`viaFabricPlus$grown_lower_outline_shape_r1_21_4`, MixinPitcherCropBlock.Companion.`viaFabricPlus$grown_lower_outline_shape_r1_21_4`)
        
    }}
