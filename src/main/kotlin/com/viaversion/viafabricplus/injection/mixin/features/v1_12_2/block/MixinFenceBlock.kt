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
import net.raphimc.vialegacy.api.LegacyProtocolVersion

@Mixin(net.minecraft.world.level.block.FenceBlock::class) 
abstract class MixinFenceBlock protected constructor(postWidth: kotlin.Float, postHeight: kotlin.Float, wallWidth: kotlin.Float, wallHeight: kotlin.Float, collisionHeight: kotlin.Float, properties: net.minecraft.world.level.block.state.BlockBehaviour.Properties) : net.minecraft.world.level.block.CrossCollisionBlock(postWidth, postHeight, wallWidth, wallHeight, collisionHeight, properties), ICrossCollisionBlock {

    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$shapes_r1_12_2`: kotlin.Array<net.minecraft.world.phys.shapes.VoxelShape> = uninitialized()
    
    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$collision_shapes_r1_4_7`: kotlin.Array<net.minecraft.world.phys.shapes.VoxelShape> = uninitialized()
    
    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField val `viaFabricPlus$shape_b1_8_1`: net.minecraft.world.phys.shapes.VoxelShape = net.minecraft.world.level.block.Block.box(0.0, 0.0, 0.0, 16.0, 24.0, 16.0)
    
    @Inject(method = ["<init>"], at = [At("RETURN")]) private fun initShapes1_12_2(properties: net.minecraft.world.level.block.state.BlockBehaviour.Properties, ci: CallbackInfo) {
        this.`viaFabricPlus$shapes_r1_12_2` = this.`viaFabricPlus$createShapes1_12_2`(false)
        this.`viaFabricPlus$collision_shapes_r1_4_7` = this.`viaFabricPlus$createShapes1_12_2`(true)
    }
    
    public override fun getShape(state: net.minecraft.world.level.block.state.BlockState, world: net.minecraft.world.level.BlockGetter, pos: net.minecraft.core.BlockPos, context: net.minecraft.world.phys.shapes.CollisionContext): net.minecraft.world.phys.shapes.VoxelShape {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(LegacyProtocolVersion.b1_8tob1_8_1)) {
            return net.minecraft.world.phys.shapes.Shapes.block()
        } else if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)) {
            return this.`viaFabricPlus$shapes_r1_12_2`[this.`viaFabricPlus$getShapeIndex`(state)]
        } else {
            return super.getShape(state, world, pos, context)
        }
    }
    
    public override fun getCollisionShape(state: net.minecraft.world.level.block.state.BlockState, world: net.minecraft.world.level.BlockGetter, pos: net.minecraft.core.BlockPos, context: net.minecraft.world.phys.shapes.CollisionContext): net.minecraft.world.phys.shapes.VoxelShape {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(LegacyProtocolVersion.b1_8tob1_8_1)) {
            return `viaFabricPlus$shape_b1_8_1`
        } else if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_4_6tor1_4_7)) {
            return this.`viaFabricPlus$collision_shapes_r1_4_7`[this.`viaFabricPlus$getShapeIndex`(state)]
        } else {
            return super.getCollisionShape(state, world, pos, context)
        }
    }
    
    @org.spongepowered.asm.mixin.Unique private fun `viaFabricPlus$createShapes1_12_2`(collision: kotlin.Boolean): kotlin.Array<net.minecraft.world.phys.shapes.VoxelShape> {
        return kotlin.arrayOf<net.minecraft.world.phys.shapes.VoxelShape>(net.minecraft.world.phys.shapes.Shapes.box(0.375, 0.0, 0.375, 0.625, if (collision) 1.5 else 1.0, 0.625), 
        net.minecraft.world.phys.shapes.Shapes.box(0.375, 0.0, 0.375, 0.625, if (collision) 1.5 else 1.0, 1.0), 
        net.minecraft.world.phys.shapes.Shapes.box(0.0, 0.0, 0.375, 0.625, if (collision) 1.5 else 1.0, 0.625), 
        net.minecraft.world.phys.shapes.Shapes.box(0.0, 0.0, 0.375, 0.625, if (collision) 1.5 else 1.0, 1.0), 
        net.minecraft.world.phys.shapes.Shapes.box(0.375, 0.0, 0.0, 0.625, if (collision) 1.5 else 1.0, 0.625), 
        net.minecraft.world.phys.shapes.Shapes.box(0.375, 0.0, 0.0, 0.625, if (collision) 1.5 else 1.0, 1.0), 
        net.minecraft.world.phys.shapes.Shapes.box(0.0, 0.0, 0.0, 0.625, if (collision) 1.5 else 1.0, 0.625), 
        net.minecraft.world.phys.shapes.Shapes.box(0.0, 0.0, 0.0, 0.625, if (collision) 1.5 else 1.0, 1.0), 
        net.minecraft.world.phys.shapes.Shapes.box(0.375, 0.0, 0.375, 1.0, if (collision) 1.5 else 1.0, 0.625), 
        net.minecraft.world.phys.shapes.Shapes.box(0.375, 0.0, 0.375, 1.0, if (collision) 1.5 else 1.0, 1.0), 
        net.minecraft.world.phys.shapes.Shapes.box(0.0, 0.0, 0.375, 1.0, if (collision) 1.5 else 1.0, 0.625), 
        net.minecraft.world.phys.shapes.Shapes.box(0.0, 0.0, 0.375, 1.0, if (collision) 1.5 else 1.0, 1.0), 
        net.minecraft.world.phys.shapes.Shapes.box(0.375, 0.0, 0.0, 1.0, if (collision) 1.5 else 1.0, 0.625), 
        net.minecraft.world.phys.shapes.Shapes.box(0.375, 0.0, 0.0, 1.0, if (collision) 1.5 else 1.0, 1.0), 
        net.minecraft.world.phys.shapes.Shapes.box(0.0, 0.0, 0.0, 1.0, if (collision) 1.5 else 1.0, 0.625), 
        net.minecraft.world.phys.shapes.Shapes.box(0.0, 0.0, 0.0, 1.0, if (collision) 1.5 else 1.0, 1.0)
        )
    }
    
}
