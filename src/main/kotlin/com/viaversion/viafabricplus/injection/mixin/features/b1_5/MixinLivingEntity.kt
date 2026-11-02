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
package com.viaversion.viafabricplus.injection.mixin.features.b1_5

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import net.raphimc.vialegacy.api.LegacyProtocolVersion

@Mixin(net.minecraft.world.entity.LivingEntity::class) 
abstract class MixinLivingEntity (type: net.minecraft.world.entity.EntityType<*>, world: net.minecraft.world.level.Level) : net.minecraft.world.entity.Entity(type, world) {

    @org.spongepowered.asm.mixin.Shadow 
    @JvmField var lastClimbablePos: java.util.Optional<net.minecraft.core.BlockPos> = uninitialized()
    @org.spongepowered.asm.mixin.Shadow protected abstract fun trapdoorUsableAsLadder(pos: net.minecraft.core.BlockPos, state: net.minecraft.world.level.block.state.BlockState): kotlin.Boolean
    
    @Inject(method = ["onClimbable"], at = [At("RETURN")], cancellable = true) private fun allowGappedLadderClimb(cir: CallbackInfoReturnable<kotlin.Boolean>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThan(LegacyProtocolVersion.b1_5tob1_5_2) && !cir.getReturnValueZ() && !this.isSpectator()) {
            val blockPos: net.minecraft.core.BlockPos = this.blockPosition().above()
            val blockState: net.minecraft.world.level.block.state.BlockState = this.level().getBlockState(blockPos)
            if (blockState.`is`(net.minecraft.tags.BlockTags.CLIMBABLE)) {
                this.lastClimbablePos = java.util.Optional.of<net.minecraft.core.BlockPos>(blockPos)
                cir.setReturnValue(true)
            } else if (blockState.getBlock() is net.minecraft.world.level.block.TrapDoorBlock && this.trapdoorUsableAsLadder(blockPos, blockState)) {
                this.lastClimbablePos = java.util.Optional.of<net.minecraft.core.BlockPos>(blockPos)
                cir.setReturnValue(true)
            }
        }
    }
    
}
