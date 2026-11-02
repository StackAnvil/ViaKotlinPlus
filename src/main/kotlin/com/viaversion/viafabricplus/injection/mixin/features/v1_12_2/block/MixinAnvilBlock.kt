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

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import com.viaversion.viafabricplus.injection.ViaFabricPlusMixinPlugin
import com.viaversion.viafabricplus.injection.mixin.features.v1_12_2.block.MixinAnvilBlock
import org.spongepowered.asm.mixin.Final

@Mixin(net.minecraft.world.level.block.AnvilBlock::class) 
abstract class MixinAnvilBlock (settings: net.minecraft.world.level.block.state.BlockBehaviour.Properties) : net.minecraft.world.level.block.FallingBlock(settings) {

    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$requireOriginalShape`: kotlin.Boolean = false
    
    @Inject(method = ["getShape"], at = [At("HEAD")], cancellable = true) private fun changeOutlineShape(state: net.minecraft.world.level.block.state.BlockState, level: net.minecraft.world.level.BlockGetter, pos: net.minecraft.core.BlockPos, context: net.minecraft.world.phys.shapes.CollisionContext, cir: CallbackInfoReturnable<net.minecraft.world.phys.shapes.VoxelShape>) {
        if (ViaFabricPlusMixinPlugin.MORE_CULLING_PRESENT && `viaFabricPlus$requireOriginalShape`) {
            `viaFabricPlus$requireOriginalShape` = false
        } else if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)) {
            cir.setReturnValue(if (state.getValue<net.minecraft.core.Direction>(MixinAnvilBlock.Companion.FACING).getAxis() == net.minecraft.core.Direction.Axis.X) MixinAnvilBlock.Companion.`viaFabricPlus$x_axis_shape_r1_12_2` else MixinAnvilBlock.Companion.`viaFabricPlus$z_axis_shape_r1_12_2`)
        }
    }
    
    override fun getOcclusionShape(state: net.minecraft.world.level.block.state.BlockState): net.minecraft.world.phys.shapes.VoxelShape {
        // Workaround for https://github.com/ViaVersion/ViaFabricPlus/issues/246
        // MoreCulling is caching the culling shape and doesn't reload it, so we have to force vanilla's shape here.
        `viaFabricPlus$requireOriginalShape` = true
        return super.getOcclusionShape(state)
    }
    
    private companion object {
        @field:org.spongepowered.asm.mixin.Shadow @Final 
        @JvmField var FACING: net.minecraft.world.level.block.state.properties.EnumProperty<net.minecraft.core.Direction> = uninitialized()
        @field:org.spongepowered.asm.mixin.Unique 
        @JvmField val `viaFabricPlus$x_axis_shape_r1_12_2`: net.minecraft.world.phys.shapes.VoxelShape = net.minecraft.world.level.block.Block.box(0.0, 0.0, 2.0, 16.0, 16.0, 14.0)
        
        @field:org.spongepowered.asm.mixin.Unique 
        @JvmField val `viaFabricPlus$z_axis_shape_r1_12_2`: net.minecraft.world.phys.shapes.VoxelShape = net.minecraft.world.level.block.Block.box(2.0, 0.0, 0.0, 14.0, 16.0, 16.0)
        
    }}
