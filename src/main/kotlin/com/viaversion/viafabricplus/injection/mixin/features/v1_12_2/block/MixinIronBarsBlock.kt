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
import com.viaversion.viafabricplus.injection.access.v1_14.ICrossCollisionBlock
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.viaversion.viafabricplus.ViaFabricPlusImpl

@Mixin(net.minecraft.world.level.block.IronBarsBlock::class) 
abstract class MixinIronBarsBlock protected constructor(radius1: kotlin.Float, radius2: kotlin.Float, boundingHeight1: kotlin.Float, boundingHeight2: kotlin.Float, collisionHeight: kotlin.Float, settings: net.minecraft.world.level.block.state.BlockBehaviour.Properties) : net.minecraft.world.level.block.CrossCollisionBlock(radius1, radius2, boundingHeight1, boundingHeight2, collisionHeight, settings), ICrossCollisionBlock {

    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$shape_r1_12_2`: kotlin.Array<net.minecraft.world.phys.shapes.VoxelShape> = uninitialized()
    
    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$shape_r1_8`: kotlin.Array<net.minecraft.world.phys.shapes.VoxelShape> = uninitialized()
    
    @Inject(method = ["<init>"], at = [At("RETURN")]) private fun initShapes1_8(properties: net.minecraft.world.level.block.state.BlockBehaviour.Properties, ci: CallbackInfo) {
        val f: kotlin.Float = 7.0f
        val g: kotlin.Float = 9.0f
        val h: kotlin.Float = 7.0f
        val i: kotlin.Float = 9.0f
        
        val baseShape: net.minecraft.world.phys.shapes.VoxelShape = net.minecraft.world.level.block.Block.box(f.toDouble(), 0.0, f.toDouble(), g.toDouble(), 16.0.toFloat().toDouble(), g.toDouble())
        
        `viaFabricPlus$shape_r1_12_2` = kotlin.arrayOf<net.minecraft.world.phys.shapes.VoxelShape>(baseShape, 
        net.minecraft.world.level.block.Block.box(h.toDouble(), 0.0, h.toDouble(), i.toDouble(), 16.0, 16.0),  // south
        net.minecraft.world.level.block.Block.box(0.0, 0.0, h.toDouble(), i.toDouble(), 16.0, i.toDouble()),  // west
        net.minecraft.world.level.block.Block.box(0.0, 0.0, h.toDouble(), i.toDouble(), 16.0, 16.0),  // south-west corner
        net.minecraft.world.level.block.Block.box(h.toDouble(), 0.0, 0.0, i.toDouble(), 16.0, i.toDouble()),  // north
        net.minecraft.world.level.block.Block.box(h.toDouble(), 0.0, 0.0, i.toDouble(), 16.0, 16.0),  // south-north line
        net.minecraft.world.level.block.Block.box(0.0, 0.0, 0.0, i.toDouble(), 16.0, i.toDouble()),  // west-north corner
        net.minecraft.world.level.block.Block.box(0.0, 0.0, 0.0, i.toDouble(), 16.0, 16.0),  // south-west-north T
        net.minecraft.world.level.block.Block.box(h.toDouble(), 0.0, h.toDouble(), 16.0, 16.0, i.toDouble()),  // east
        net.minecraft.world.level.block.Block.box(h.toDouble(), 0.0, h.toDouble(), 16.0, 16.0, 16.0),  // south-east corner
        net.minecraft.world.level.block.Block.box(0.0, 0.0, h.toDouble(), 16.0, 16.0, i.toDouble()),  // west-east line
        net.minecraft.world.level.block.Block.box(0.0, 0.0, h.toDouble(), 16.0, 16.0, 16.0),  // south-west-east T
        net.minecraft.world.level.block.Block.box(h.toDouble(), 0.0, 0.0, 16.0, 16.0, i.toDouble()),  // north-east corner
        net.minecraft.world.level.block.Block.box(h.toDouble(), 0.0, 0.0, 16.0, 16.0, 16.0),  // south-north-east T
        net.minecraft.world.level.block.Block.box(0.0, 0.0, 0.0, 16.0, 16.0, i.toDouble()),  // west-north-east T
        net.minecraft.world.phys.shapes.Shapes.block() // cross
        )
        
        val northShape: net.minecraft.world.phys.shapes.VoxelShape = net.minecraft.world.level.block.Block.box(h.toDouble(), 0.0.toFloat().toDouble(), 0.0, i.toDouble(), 16.0.toFloat().toDouble(), (i - 1).toDouble())
        val southShape: net.minecraft.world.phys.shapes.VoxelShape = net.minecraft.world.level.block.Block.box(h.toDouble(), 0.0.toFloat().toDouble(), (h + 1).toDouble(), i.toDouble(), 16.0.toFloat().toDouble(), 16.0)
        val westShape: net.minecraft.world.phys.shapes.VoxelShape = net.minecraft.world.level.block.Block.box(0.0, 0.0.toFloat().toDouble(), h.toDouble(), (i - 1).toDouble(), 16.0.toFloat().toDouble(), i.toDouble())
        val eastShape: net.minecraft.world.phys.shapes.VoxelShape = net.minecraft.world.level.block.Block.box((h + 1).toDouble(), 0.0.toFloat().toDouble(), h.toDouble(), 16.0, 16.0.toFloat().toDouble(), i.toDouble())
        
        val northEastCornerShape: net.minecraft.world.phys.shapes.VoxelShape = net.minecraft.world.phys.shapes.Shapes.or(northShape, eastShape)
        val southWestCornerShape: net.minecraft.world.phys.shapes.VoxelShape = net.minecraft.world.phys.shapes.Shapes.or(southShape, westShape)
        
        `viaFabricPlus$shape_r1_8` = kotlin.arrayOf<net.minecraft.world.phys.shapes.VoxelShape>(baseShape, 
        southShape, 
        westShape, 
        southWestCornerShape, 
        northShape, 
        net.minecraft.world.phys.shapes.Shapes.or(southShape, northShape), 
        net.minecraft.world.phys.shapes.Shapes.or(westShape, northShape), 
        net.minecraft.world.phys.shapes.Shapes.or(southWestCornerShape, northShape), 
        eastShape, 
        net.minecraft.world.phys.shapes.Shapes.or(southShape, eastShape), 
        net.minecraft.world.phys.shapes.Shapes.or(westShape, eastShape), 
        net.minecraft.world.phys.shapes.Shapes.or(southWestCornerShape, eastShape), 
        northEastCornerShape, 
        net.minecraft.world.phys.shapes.Shapes.or(southShape, northEastCornerShape), 
        net.minecraft.world.phys.shapes.Shapes.or(westShape, northEastCornerShape), 
        net.minecraft.world.phys.shapes.Shapes.or(southWestCornerShape, northEastCornerShape)
        )
    }
    
    public override fun getShape(state: net.minecraft.world.level.block.state.BlockState, world: net.minecraft.world.level.BlockGetter, pos: net.minecraft.core.BlockPos, context: net.minecraft.world.phys.shapes.CollisionContext): net.minecraft.world.phys.shapes.VoxelShape {
        if (ViaFabricPlusImpl.impl().visuals().legacyPaneOutlines().isActive()) {
            return this.`viaFabricPlus$shape_r1_12_2`[this.`viaFabricPlus$getShapeIndex`(state)]
        } else {
            return super.getShape(state, world, pos, context)
        }
    }
    
    public override fun getCollisionShape(state: net.minecraft.world.level.block.state.BlockState, world: net.minecraft.world.level.BlockGetter, pos: net.minecraft.core.BlockPos, context: net.minecraft.world.phys.shapes.CollisionContext): net.minecraft.world.phys.shapes.VoxelShape {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8)) {
            return this.`viaFabricPlus$shape_r1_8`[this.`viaFabricPlus$getShapeIndex`(state)]
        } else {
            return super.getCollisionShape(state, world, pos, context)
        }
    }
    
}
