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
package com.viaversion.viafabricplus.injection.mixin.features.v1_4_6

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import net.raphimc.vialegacy.api.LegacyProtocolVersion

@Mixin(net.minecraft.world.level.block.state.BlockBehaviour::class) 
abstract class MixinBlockBehaviour {

    @Inject(method = ["getDestroyProgress"], at = [At("HEAD")], cancellable = true) private fun changeMiningSpeedCalculation(state: net.minecraft.world.level.block.state.BlockState, player: net.minecraft.world.entity.player.Player, level: net.minecraft.world.level.BlockGetter, pos: net.minecraft.core.BlockPos, cir: CallbackInfoReturnable<kotlin.Float>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_4_6tor1_4_7)) {
            val hardness: kotlin.Float = state.getDestroySpeed(level, pos)
            if (hardness == -1.0f) {
                cir.setReturnValue(0.0f)
            } else {
                if (!player.hasCorrectToolForDrops(state)) {
                    cir.setReturnValue(1.0f / hardness / 100f)
                } else {
                    cir.setReturnValue(player.getDestroySpeed(state) / hardness / 30f)
                }
            }
        }
    }
    
}
