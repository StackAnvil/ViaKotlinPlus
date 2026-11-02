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
import com.viaversion.viafabricplus.injection.mixin.features.v1_12_2.block.MixinEndPortalFrameBlock
import org.spongepowered.asm.mixin.Final

@Mixin(net.minecraft.world.level.block.EndPortalFrameBlock::class) 
abstract class MixinEndPortalFrameBlock (settings: net.minecraft.world.level.block.state.BlockBehaviour.Properties) : net.minecraft.world.level.block.Block(settings) {

    @Inject(method = ["getShape"], at = [At(value = "HEAD")], cancellable = true) private fun changeOutlineShape(state: net.minecraft.world.level.block.state.BlockState, level: net.minecraft.world.level.BlockGetter, pos: net.minecraft.core.BlockPos, context: net.minecraft.world.phys.shapes.CollisionContext, cir: CallbackInfoReturnable<net.minecraft.world.phys.shapes.VoxelShape>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)) {
            cir.setReturnValue(MixinEndPortalFrameBlock.Companion.SHAPE_EMPTY)
        }
    }
    
    public override fun getCollisionShape(state: net.minecraft.world.level.block.state.BlockState, world: net.minecraft.world.level.BlockGetter, pos: net.minecraft.core.BlockPos, context: net.minecraft.world.phys.shapes.CollisionContext): net.minecraft.world.phys.shapes.VoxelShape {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)) {
            return if (state.getValue<kotlin.Boolean>(MixinEndPortalFrameBlock.Companion.HAS_EYE)) MixinEndPortalFrameBlock.Companion.`viaFabricPlus$frame_with_eye_shape_r1_12_2` else MixinEndPortalFrameBlock.Companion.SHAPE_EMPTY
        } else {
            return super.getCollisionShape(state, world, pos, context)
        }
    }
    
    private companion object {
        @field:org.spongepowered.asm.mixin.Shadow @Final 
        @JvmField var SHAPE_EMPTY: net.minecraft.world.phys.shapes.VoxelShape = uninitialized()
        @field:org.spongepowered.asm.mixin.Shadow @Final 
        @JvmField var HAS_EYE: net.minecraft.world.level.block.state.properties.BooleanProperty = uninitialized()
        @field:org.spongepowered.asm.mixin.Unique 
        @JvmField val `viaFabricPlus$eye_shape_r1_12_2`: net.minecraft.world.phys.shapes.VoxelShape = net.minecraft.world.level.block.Block.box(5.0, 13.0, 5.0, 11.0, 16.0, 11.0)
        
        @field:org.spongepowered.asm.mixin.Unique 
        @JvmField val `viaFabricPlus$frame_with_eye_shape_r1_12_2`: net.minecraft.world.phys.shapes.VoxelShape = net.minecraft.world.phys.shapes.Shapes.or(MixinEndPortalFrameBlock.Companion.SHAPE_EMPTY, MixinEndPortalFrameBlock.Companion.`viaFabricPlus$eye_shape_r1_12_2`)
        
    }}
