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
package com.viaversion.viafabricplus.injection.mixin.features.v1_1

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import net.raphimc.vialegacy.api.LegacyProtocolVersion
import com.viaversion.viafabricplus.injection.mixin.features.v1_1.MixinPistonBaseBlock
import org.spongepowered.asm.mixin.Final

@Mixin(net.minecraft.world.level.block.piston.PistonBaseBlock::class) 
abstract class MixinPistonBaseBlock protected constructor(settings: net.minecraft.world.level.block.state.BlockBehaviour.Properties) : net.minecraft.world.level.block.DirectionalBlock(settings) {

    @Inject(method = ["getShape"], at = [At("HEAD")], cancellable = true) private fun changeOutlineShape(state: net.minecraft.world.level.block.state.BlockState, level: net.minecraft.world.level.BlockGetter, pos: net.minecraft.core.BlockPos, context: net.minecraft.world.phys.shapes.CollisionContext, cir: CallbackInfoReturnable<net.minecraft.world.phys.shapes.VoxelShape>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_1)) {
            cir.setReturnValue(net.minecraft.world.phys.shapes.Shapes.block())
        }
    }
    
    public override fun getOcclusionShape(state: net.minecraft.world.level.block.state.BlockState): net.minecraft.world.phys.shapes.VoxelShape {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_1)) {
            if (state.getValue<kotlin.Boolean>(net.minecraft.world.level.block.piston.PistonBaseBlock.EXTENDED)) {
                return MixinPistonBaseBlock.Companion.SHAPES!!.get(state.getValue<net.minecraft.core.Direction>(net.minecraft.world.level.block.DirectionalBlock.FACING))!!
            } else {
                return net.minecraft.world.phys.shapes.Shapes.block()
            }
        } else {
            return super.getOcclusionShape(state)
        }
    }
    
    private companion object {
        @field:org.spongepowered.asm.mixin.Shadow @Final 
        @JvmField val SHAPES: kotlin.collections.MutableMap<net.minecraft.core.Direction, net.minecraft.world.phys.shapes.VoxelShape> = uninitialized()
    }}
