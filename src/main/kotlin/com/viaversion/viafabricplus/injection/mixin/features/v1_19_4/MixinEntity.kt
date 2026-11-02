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
package com.viaversion.viafabricplus.injection.mixin.features.v1_19_4

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(net.minecraft.world.entity.Entity::class) 
abstract class MixinEntity {

    @org.spongepowered.asm.mixin.Shadow 
    @JvmField val position: net.minecraft.world.phys.Vec3 = uninitialized()
    @org.spongepowered.asm.mixin.Shadow 
    @JvmField val level: net.minecraft.world.level.Level = uninitialized()
    @org.spongepowered.asm.mixin.Shadow abstract fun getBoundingBox(): net.minecraft.world.phys.AABB
    
    @org.spongepowered.asm.mixin.Shadow abstract fun level(): net.minecraft.world.level.Level
    
    @Inject(method = ["getOnPos(F)Lnet/minecraft/core/BlockPos;"], at = [At("HEAD")], cancellable = true) private fun modifyPosWithYOffset(offset: kotlin.Float, cir: CallbackInfoReturnable<net.minecraft.core.BlockPos>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_19_4)) {
            val i: kotlin.Int = net.minecraft.util.Mth.floor(this.position.x)
            val j: kotlin.Int = net.minecraft.util.Mth.floor(this.position.y - (if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_18_2) && offset == 1.0E-5f) 0.2f else offset).toDouble())
            val k: kotlin.Int = net.minecraft.util.Mth.floor(this.position.z)
            val blockPos: net.minecraft.core.BlockPos = net.minecraft.core.BlockPos(i, j, k)
            if (this.level.getBlockState(blockPos).isAir()) {
                val downPos: net.minecraft.core.BlockPos = blockPos.below()
                val blockState: net.minecraft.world.level.block.state.BlockState = this.level.getBlockState(downPos)
                if (blockState.`is`(net.minecraft.tags.BlockTags.FENCES) || blockState.`is`(net.minecraft.tags.BlockTags.WALLS) || blockState.getBlock() is net.minecraft.world.level.block.FenceGateBlock) {
                    cir.setReturnValue(downPos)
                    return 
                }
            }
            
            cir.setReturnValue(blockPos)
        }
    }
    
    @Inject(method = ["getBlockPosBelowThatAffectsMyMovement"], at = [At("HEAD")], cancellable = true) private fun modifyVelocityAffectingPos(cir: CallbackInfoReturnable<net.minecraft.core.BlockPos>) {
        val target: com.viaversion.viaversion.api.protocol.version.ProtocolVersion = com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion()
        
        if (target.olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_19_4)) {
            cir.setReturnValue(net.minecraft.core.BlockPos.containing(position.x, getBoundingBox().minY - (if (target.olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_14_4)) 1.0 else 0.5000001), position.z))
        }
    }
    
}
