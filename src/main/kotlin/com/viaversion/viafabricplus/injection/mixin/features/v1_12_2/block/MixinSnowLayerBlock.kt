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
import com.viaversion.viafabricplus.injection.mixin.features.v1_12_2.block.MixinSnowLayerBlock
import org.spongepowered.asm.mixin.Final

@Mixin(net.minecraft.world.level.block.SnowLayerBlock::class) 
abstract class MixinSnowLayerBlock {

    @Inject(method = ["getCollisionShape"], at = [At("HEAD")], cancellable = true) private fun changeCollisionShape(state: net.minecraft.world.level.block.state.BlockState, level: net.minecraft.world.level.BlockGetter, pos: net.minecraft.core.BlockPos, context: net.minecraft.world.phys.shapes.CollisionContext, cir: CallbackInfoReturnable<net.minecraft.world.phys.shapes.VoxelShape>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)) {
            cir.setReturnValue(MixinSnowLayerBlock.Companion.`viaFabricPlus$layers_to_shape_r1_12_2`[state.getValue<kotlin.Int>(MixinSnowLayerBlock.Companion.LAYERS) - 1])
        }
    }
    
    private companion object {
        @field:org.spongepowered.asm.mixin.Shadow @Final 
        @JvmField var LAYERS: net.minecraft.world.level.block.state.properties.IntegerProperty = uninitialized()
        @field:org.spongepowered.asm.mixin.Unique 
        @JvmField val `viaFabricPlus$layers_to_shape_r1_12_2`: kotlin.Array<net.minecraft.world.phys.shapes.VoxelShape> = kotlin.arrayOf<net.minecraft.world.phys.shapes.VoxelShape>(net.minecraft.world.level.block.Block.box(0.0, -0.00001,  /* 0.0D */0.0, 16.0, 0.0, 16.0),
        net.minecraft.world.level.block.Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), 
        net.minecraft.world.level.block.Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0), 
        net.minecraft.world.level.block.Block.box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0), 
        net.minecraft.world.level.block.Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0), 
        net.minecraft.world.level.block.Block.box(0.0, 0.0, 0.0, 16.0, 10.0, 16.0), 
        net.minecraft.world.level.block.Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0), 
        net.minecraft.world.level.block.Block.box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0), 
        net.minecraft.world.level.block.Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
        )
        
    }}
