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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import net.raphimc.vialegacy.api.LegacyProtocolVersion

@Mixin(net.minecraft.world.level.block.WallBlock::class) 
abstract class MixinWallBlock (settings: net.minecraft.world.level.block.state.BlockBehaviour.Properties) : net.minecraft.world.level.block.Block(settings) {

    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$shapes_r1_12_2`: kotlin.Array<net.minecraft.world.phys.shapes.VoxelShape> = uninitialized()
    
    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$collision_shapes_r1_12_2`: kotlin.Array<net.minecraft.world.phys.shapes.VoxelShape> = uninitialized()
    
    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$shapes_r1_8`: kotlin.Array<net.minecraft.world.phys.shapes.VoxelShape> = uninitialized()
    
    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$collision_shapes_r1_4_2`: kotlin.Array<net.minecraft.world.phys.shapes.VoxelShape> = uninitialized()
    
    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField val `viaFabricPlus$shapeIndexCache_r1_12_2`: it.unimi.dsi.fastutil.objects.Object2IntMap<net.minecraft.world.level.block.state.BlockState> = it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap<net.minecraft.world.level.block.state.BlockState>()
    
    @Inject(method = ["<init>"], at = [At("RETURN")]) private fun initShapes1_12_2(properties: net.minecraft.world.level.block.state.BlockBehaviour.Properties, ci: CallbackInfo) {
        this.`viaFabricPlus$shapes_r1_12_2` = this.`viaFabricPlus$createShapes1_12_2`(false)
        this.`viaFabricPlus$collision_shapes_r1_12_2` = this.`viaFabricPlus$createShapes1_12_2`(true)
        
        this.`viaFabricPlus$shapes_r1_8` = this.`viaFabricPlus$shapes_r1_12_2`.clone()
        this.`viaFabricPlus$shapes_r1_8`[5] = net.minecraft.world.phys.shapes.Shapes.box(0.3125, 0.0, 0.0, 0.6875, 0.8125, 1.0)
        this.`viaFabricPlus$shapes_r1_8`[10] = net.minecraft.world.phys.shapes.Shapes.box(0.0, 0.0, 0.3125, 1.0, 0.8125, 0.6875)
        
        this.`viaFabricPlus$collision_shapes_r1_4_2` = this.`viaFabricPlus$createCollisionShapes1_4_2`()
    }
    
    @Inject(method = ["getCollisionShape"], at = [At("HEAD")], cancellable = true) private fun changeCollisionShape(state: net.minecraft.world.level.block.state.BlockState, level: net.minecraft.world.level.BlockGetter, pos: net.minecraft.core.BlockPos, context: net.minecraft.world.phys.shapes.CollisionContext, cir: CallbackInfoReturnable<net.minecraft.world.phys.shapes.VoxelShape>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_4_2)) {
            cir.setReturnValue(this.`viaFabricPlus$collision_shapes_r1_4_2`[this.`viaFabricPlus$getShapeIndex`(state)])
        } else if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)) {
            cir.setReturnValue(this.`viaFabricPlus$collision_shapes_r1_12_2`[this.`viaFabricPlus$getShapeIndex`(state)])
        }
    }
    
    @Inject(method = ["getShape"], at = [At("HEAD")], cancellable = true) private fun changeOutlineShape(state: net.minecraft.world.level.block.state.BlockState, level: net.minecraft.world.level.BlockGetter, pos: net.minecraft.core.BlockPos, context: net.minecraft.world.phys.shapes.CollisionContext, cir: CallbackInfoReturnable<net.minecraft.world.phys.shapes.VoxelShape>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8)) {
            cir.setReturnValue(this.`viaFabricPlus$shapes_r1_8`[this.`viaFabricPlus$getShapeIndex`(state)])
        } else if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)) {
            cir.setReturnValue(this.`viaFabricPlus$shapes_r1_12_2`[this.`viaFabricPlus$getShapeIndex`(state)])
        }
    }
    
    public override fun getOcclusionShape(state: net.minecraft.world.level.block.state.BlockState): net.minecraft.world.phys.shapes.VoxelShape {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8)) {
            return this.`viaFabricPlus$shapes_r1_8`[this.`viaFabricPlus$getShapeIndex`(state)]
        } else if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)) {
            return this.`viaFabricPlus$shapes_r1_12_2`[this.`viaFabricPlus$getShapeIndex`(state)]
        } else {
            return super.getOcclusionShape(state)
        }
    }
    
    @org.spongepowered.asm.mixin.Unique private fun `viaFabricPlus$createShapes1_12_2`(collision: kotlin.Boolean): kotlin.Array<net.minecraft.world.phys.shapes.VoxelShape> {
        return kotlin.arrayOf<net.minecraft.world.phys.shapes.VoxelShape>(net.minecraft.world.phys.shapes.Shapes.box(0.25, 0.0, 0.25, 0.75, if (collision) 1.5 else 1.0, 0.75), 
        net.minecraft.world.phys.shapes.Shapes.box(0.25, 0.0, 0.25, 0.75, if (collision) 1.5 else 1.0, 1.0), 
        net.minecraft.world.phys.shapes.Shapes.box(0.0, 0.0, 0.25, 0.75, if (collision) 1.5 else 1.0, 0.75), 
        net.minecraft.world.phys.shapes.Shapes.box(0.0, 0.0, 0.25, 0.75, if (collision) 1.5 else 1.0, 1.0), 
        net.minecraft.world.phys.shapes.Shapes.box(0.25, 0.0, 0.0, 0.75, if (collision) 1.5 else 1.0, 0.75), 
        net.minecraft.world.phys.shapes.Shapes.box(0.3125, 0.0, 0.0, 0.6875, if (collision) 1.5 else 0.875, 1.0), 
        net.minecraft.world.phys.shapes.Shapes.box(0.0, 0.0, 0.0, 0.75, if (collision) 1.5 else 1.0, 0.75), 
        net.minecraft.world.phys.shapes.Shapes.box(0.0, 0.0, 0.0, 0.75, if (collision) 1.5 else 1.0, 1.0), 
        net.minecraft.world.phys.shapes.Shapes.box(0.25, 0.0, 0.25, 1.0, if (collision) 1.5 else 1.0, 0.75), 
        net.minecraft.world.phys.shapes.Shapes.box(0.25, 0.0, 0.25, 1.0, if (collision) 1.5 else 1.0, 1.0), 
        net.minecraft.world.phys.shapes.Shapes.box(0.0, 0.0, 0.3125, 1.0, if (collision) 1.5 else 0.875, 0.6875), 
        net.minecraft.world.phys.shapes.Shapes.box(0.0, 0.0, 0.25, 1.0, if (collision) 1.5 else 1.0, 1.0), 
        net.minecraft.world.phys.shapes.Shapes.box(0.25, 0.0, 0.0, 1.0, if (collision) 1.5 else 1.0, 0.75), 
        net.minecraft.world.phys.shapes.Shapes.box(0.25, 0.0, 0.0, 1.0, if (collision) 1.5 else 1.0, 1.0), 
        net.minecraft.world.phys.shapes.Shapes.box(0.0, 0.0, 0.0, 1.0, if (collision) 1.5 else 1.0, 0.75), 
        net.minecraft.world.phys.shapes.Shapes.box(0.0, 0.0, 0.0, 1.0, if (collision) 1.5 else 1.0, 1.0)
        )
    }
    
    @org.spongepowered.asm.mixin.Unique private fun `viaFabricPlus$createCollisionShapes1_4_2`(): kotlin.Array<net.minecraft.world.phys.shapes.VoxelShape> {
        return kotlin.arrayOf<net.minecraft.world.phys.shapes.VoxelShape>(net.minecraft.world.phys.shapes.Shapes.box(0.375, 0.0, 0.375, 0.625, 1.5, 0.625), 
        net.minecraft.world.phys.shapes.Shapes.box(0.375, 0.0, 0.375, 0.625, 1.5, 1.0), 
        net.minecraft.world.phys.shapes.Shapes.box(0.0, 0.0, 0.375, 0.625, 1.5, 0.625), 
        net.minecraft.world.phys.shapes.Shapes.box(0.0, 0.0, 0.375, 0.625, 1.5, 1.0), 
        net.minecraft.world.phys.shapes.Shapes.box(0.375, 0.0, 0.0, 0.625, 1.5, 0.625), 
        net.minecraft.world.phys.shapes.Shapes.box(0.375, 0.0, 0.0, 0.625, 1.5, 1.0), 
        net.minecraft.world.phys.shapes.Shapes.box(0.0, 0.0, 0.0, 0.625, 1.5, 0.625), 
        net.minecraft.world.phys.shapes.Shapes.box(0.0, 0.0, 0.0, 0.625, 1.5, 1.0), 
        net.minecraft.world.phys.shapes.Shapes.box(0.375, 0.0, 0.375, 1.0, 1.5, 0.625), 
        net.minecraft.world.phys.shapes.Shapes.box(0.375, 0.0, 0.375, 1.0, 1.5, 1.0), 
        net.minecraft.world.phys.shapes.Shapes.box(0.0, 0.0, 0.375, 1.0, 1.5, 0.625), 
        net.minecraft.world.phys.shapes.Shapes.box(0.0, 0.0, 0.375, 1.0, 1.5, 1.0), 
        net.minecraft.world.phys.shapes.Shapes.box(0.375, 0.0, 0.0, 1.0, 1.5, 0.625), 
        net.minecraft.world.phys.shapes.Shapes.box(0.375, 0.0, 0.0, 1.0, 1.5, 1.0), 
        net.minecraft.world.phys.shapes.Shapes.box(0.0, 0.0, 0.0, 1.0, 1.5, 0.625), 
        net.minecraft.world.phys.shapes.Shapes.box(0.0, 0.0, 0.0, 1.0, 1.5, 1.0)
        )
    }
    
    @org.spongepowered.asm.mixin.Unique private fun `viaFabricPlus$getShapeIndex`(state: net.minecraft.world.level.block.state.BlockState): kotlin.Int {
        return this.`viaFabricPlus$shapeIndexCache_r1_12_2`.computeIfAbsent(state, it.unimi.dsi.fastutil.objects.Object2IntFunction {
        `_`: kotlin.Any -> var index: kotlin.Int = 0
        if (net.minecraft.world.level.block.state.properties.WallSide.NONE != state.getValue<net.minecraft.world.level.block.state.properties.WallSide>(net.minecraft.world.level.block.WallBlock.NORTH)) {
            index = index or (1 shl net.minecraft.core.Direction.NORTH.get2DDataValue())
        }
        
        if (net.minecraft.world.level.block.state.properties.WallSide.NONE != state.getValue<net.minecraft.world.level.block.state.properties.WallSide>(net.minecraft.world.level.block.WallBlock.EAST)) {
            index = index or (1 shl net.minecraft.core.Direction.EAST.get2DDataValue())
        }
        
        if (net.minecraft.world.level.block.state.properties.WallSide.NONE != state.getValue<net.minecraft.world.level.block.state.properties.WallSide>(net.minecraft.world.level.block.WallBlock.SOUTH)) {
            index = index or (1 shl net.minecraft.core.Direction.SOUTH.get2DDataValue())
        }
        
        if (net.minecraft.world.level.block.state.properties.WallSide.NONE != state.getValue<net.minecraft.world.level.block.state.properties.WallSide>(net.minecraft.world.level.block.WallBlock.WEST)) {
            index = index or (1 shl net.minecraft.core.Direction.WEST.get2DDataValue())
        }
        
        index
        })
    }
    
}
