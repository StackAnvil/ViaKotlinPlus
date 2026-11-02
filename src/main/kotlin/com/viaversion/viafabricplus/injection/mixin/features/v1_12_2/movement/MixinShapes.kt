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
package com.viaversion.viafabricplus.injection.mixin.features.v1_12_2.movement

import com.viaversion.viafabricplus.ViaFabricPlus
import com.viaversion.viafabricplus.features.v1_12_2.ShapeIntersections
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import net.minecraft.core.Direction
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(Shapes::class)
class MixinShapes {

    private companion object {
        @JvmStatic
        @Inject(method = ["collide"], at = [At("HEAD")], cancellable = true)
        fun calculateMaxOffset1_12_2(
            axis: Direction.Axis,
            moving: AABB,
            shapes: Iterable<VoxelShape>,
            maxDist: Double,
            cir: CallbackInfoReturnable<Double>
        ) {
            var maxDist = maxDist
            if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
                for (shape in shapes) {
                    for (shapeBox in shape.toAabbs()) {
                        maxDist = when (axis) {
                            Direction.Axis.X -> ShapeIntersections.intersectX(moving, shapeBox, maxDist)
                            Direction.Axis.Y -> ShapeIntersections.intersectY(moving, shapeBox, maxDist)
                            Direction.Axis.Z -> ShapeIntersections.intersectZ(moving, shapeBox, maxDist)
                        }
                    }
                }
                cir.setReturnValue(maxDist)
            }
        }
    }
}
