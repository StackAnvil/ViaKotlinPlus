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
package com.viaversion.viafabricplus.injection.mixin.features.v1_20_2

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(net.minecraft.world.level.block.DecoratedPotBlock::class) 
abstract class MixinDecoratedPotBlock {

    @Inject(method = ["useItemOn"], at = [At("HEAD")], cancellable = true) private fun alwaysPass(itemStack: net.minecraft.world.item.ItemStack, state: net.minecraft.world.level.block.state.BlockState, level: net.minecraft.world.level.Level, pos: net.minecraft.core.BlockPos, player: net.minecraft.world.entity.player.Player, hand: net.minecraft.world.InteractionHand, hitResult: net.minecraft.world.phys.BlockHitResult, cir: CallbackInfoReturnable<net.minecraft.world.InteractionResult>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20_2)) {
            cir.setReturnValue(net.minecraft.world.InteractionResult.PASS)
        }
    }
    
}
