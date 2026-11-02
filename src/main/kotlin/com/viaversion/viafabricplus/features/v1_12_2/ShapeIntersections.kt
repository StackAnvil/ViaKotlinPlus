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
package com.viaversion.viafabricplus.features.v1_12_2

import net.minecraft.world.phys.AABB

object ShapeIntersections {

    @JvmStatic
    fun intersectX(box: AABB, shapeBox: AABB, maxDist: Double): Double {
        var maxDist = maxDist
        if (box.maxY <= shapeBox.minY || box.minY >= shapeBox.maxY || box.maxZ <= shapeBox.minZ || box.minZ >= shapeBox.maxZ) {
            return maxDist
        }
        if (maxDist > 0.0 && box.maxX <= shapeBox.minX) {
            val d = shapeBox.minX - box.maxX
            if (d < maxDist) maxDist = d
        } else if (maxDist < 0.0 && box.minX >= shapeBox.maxX) {
            val e = shapeBox.maxX - box.minX
            if (e > maxDist) maxDist = e
        }
        return maxDist
    }

    @JvmStatic
    fun intersectY(playerBox: AABB, shapeBox: AABB, maxDist: Double): Double {
        var maxDist = maxDist
        if (playerBox.maxX <= shapeBox.minX || playerBox.minX >= shapeBox.maxX || playerBox.maxZ <= shapeBox.minZ || playerBox.minZ >= shapeBox.maxZ) {
            return maxDist
        }
        if (maxDist > 0.0 && playerBox.maxY <= shapeBox.minY) {
            val d = shapeBox.minY - playerBox.maxY
            if (d < maxDist) maxDist = d
        } else if (maxDist < 0.0 && playerBox.minY >= shapeBox.maxY) {
            val e = shapeBox.maxY - playerBox.minY
            if (e > maxDist) maxDist = e
        }
        return maxDist
    }

    @JvmStatic
    fun intersectZ(playerBox: AABB, shapeBox: AABB, maxDist: Double): Double {
        var maxDist = maxDist
        if (playerBox.maxX <= shapeBox.minX || playerBox.minX >= shapeBox.maxX || playerBox.maxY <= shapeBox.minY || playerBox.minY >= shapeBox.maxY) {
            return maxDist
        }
        if (maxDist > 0.0 && playerBox.maxZ <= shapeBox.minZ) {
            val d = shapeBox.minZ - playerBox.maxZ
            if (d < maxDist) maxDist = d
        } else if (maxDist < 0.0 && playerBox.minZ >= shapeBox.maxZ) {
            val e = shapeBox.maxZ - playerBox.minZ
            if (e > maxDist) maxDist = e
        }
        return maxDist
    }
}
