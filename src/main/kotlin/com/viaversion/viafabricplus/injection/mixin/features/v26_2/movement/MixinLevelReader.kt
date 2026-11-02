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
package com.viaversion.viafabricplus.injection.mixin.features.v26_2.movement

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(net.minecraft.world.level.LevelReader::class) 
interface MixinLevelReader {
    private companion object {

            @JvmStatic @Inject(method = ["containsAnyLiquid"], at = [At("HEAD")], cancellable = true) fun excludeMaxEdgeBlocks(box: net.minecraft.world.phys.AABB, cir: CallbackInfoReturnable<kotlin.Boolean>) {
                if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v26_3)) {
                    return 
                }
        
                        // 26.3 also checks the blocks touching the max faces of the box
                val pos: net.minecraft.core.BlockPos.MutableBlockPos = net.minecraft.core.BlockPos.MutableBlockPos()
                for (x in net.minecraft.util.Mth.floor(box.minX) ..< net.minecraft.util.Mth.ceil(box.maxX)) {
                    for (y in net.minecraft.util.Mth.floor(box.minY) ..< net.minecraft.util.Mth.ceil(box.maxY)) {
                        for (z in net.minecraft.util.Mth.floor(box.minZ) ..< net.minecraft.util.Mth.ceil(box.maxZ)) {
                            if (!(this as net.minecraft.world.level.LevelReader).getBlockState(pos.set(x, y, z)).getFluidState().isEmpty()) {
                                cir.setReturnValue(true)
                                return 
                            }
                        }
                    }
                }
                cir.setReturnValue(false)
            }
    }
}
