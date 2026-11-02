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
import com.viaversion.viafabricplus.injection.ViaFabricPlusMixinPlugin
import com.viaversion.viafabricplus.injection.mixin.features.v1_12_2.block.MixinAbstractCauldronBlock

@Mixin(net.minecraft.world.level.block.AbstractCauldronBlock::class) 
abstract class MixinAbstractCauldronBlock (properties: net.minecraft.world.level.block.state.BlockBehaviour.Properties) : net.minecraft.world.level.block.Block(properties) {

    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$requireOriginalShape`: kotlin.Boolean = false
    
    @Inject(method = ["getShape"], at = [At("HEAD")], cancellable = true) private fun changeOutlineShape(state: net.minecraft.world.level.block.state.BlockState, level: net.minecraft.world.level.BlockGetter, pos: net.minecraft.core.BlockPos, context: net.minecraft.world.phys.shapes.CollisionContext, cir: CallbackInfoReturnable<net.minecraft.world.phys.shapes.VoxelShape>) {
        if (ViaFabricPlusMixinPlugin.MORE_CULLING_PRESENT && `viaFabricPlus$requireOriginalShape`) {
            `viaFabricPlus$requireOriginalShape` = false
        } else if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)) {
            cir.setReturnValue(net.minecraft.world.phys.shapes.Shapes.block())
        }
    }
    
    override fun getCollisionShape(blockState: net.minecraft.world.level.block.state.BlockState, blockGetter: net.minecraft.world.level.BlockGetter, blockPos: net.minecraft.core.BlockPos, collisionContext: net.minecraft.world.phys.shapes.CollisionContext): net.minecraft.world.phys.shapes.VoxelShape {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)) {
            return MixinAbstractCauldronBlock.Companion.`viaFabricPlus$collision_shape_r1_12_2`
        } else {
            return super.getCollisionShape(blockState, blockGetter, blockPos, collisionContext)
        }
    }
    
    public override fun getOcclusionShape(state: net.minecraft.world.level.block.state.BlockState): net.minecraft.world.phys.shapes.VoxelShape {
        // Workaround for https://github.com/ViaVersion/ViaFabricPlus/issues/246
        // MoreCulling is caching the culling shape and doesn't reload it, so we have to force vanilla's shape here.
        `viaFabricPlus$requireOriginalShape` = true
        return super.getOcclusionShape(state)
    }
    
    private companion object {
        @field:org.spongepowered.asm.mixin.Unique 
        @JvmField val `viaFabricPlus$collision_shape_r1_12_2`: net.minecraft.world.phys.shapes.VoxelShape = net.minecraft.world.phys.shapes.Shapes.or(
        net.minecraft.world.phys.shapes.Shapes.box(0.0, 0.0, 0.0, 1.0, 0.3125, 1.0), 
        net.minecraft.world.phys.shapes.Shapes.box(0.0, 0.0, 0.0, 0.125, 1.0, 1.0), 
        net.minecraft.world.phys.shapes.Shapes.box(0.0, 0.0, 0.0, 1.0, 1.0, 0.125), 
        net.minecraft.world.phys.shapes.Shapes.box(0.875, 0.0, 0.0, 1.0, 1.0, 1.0), 
        net.minecraft.world.phys.shapes.Shapes.box(0.0, 0.0, 0.875, 1.0, 1.0, 1.0)
        )
        
    }}
