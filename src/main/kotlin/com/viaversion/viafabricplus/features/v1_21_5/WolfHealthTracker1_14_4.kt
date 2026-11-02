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
package com.viaversion.viafabricplus.features.v1_21_5

import com.viaversion.viaversion.api.connection.StorableObject
import it.unimi.dsi.fastutil.ints.Int2FloatMap
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap
import com.viaversion.viafabricplus.features.v1_21_5.WolfHealthTracker1_14_4

class WolfHealthTracker1_14_4 : StorableObject {

    private val healthDataMap: Int2FloatMap = Int2FloatOpenHashMap()
    
    fun getWolfHealth(entityId: kotlin.Int, fallback: kotlin.Float): kotlin.Float {
        return this.healthDataMap.getOrDefault(entityId, fallback)
    }
    
    fun setWolfHealth(entityId: kotlin.Int, wolfHealth: kotlin.Float) {
        this.healthDataMap.put(entityId, wolfHealth)
    }
    
    companion object {
        fun getWolfHealth(entity: net.minecraft.world.entity.LivingEntity): kotlin.Float {
            val tracker: WolfHealthTracker1_14_4 = com.viaversion.viafabricplus.ViaFabricPlus.api().userConnection()!!.get<WolfHealthTracker1_14_4>(WolfHealthTracker1_14_4::class.java)!!
            if (tracker != null) {
                return tracker.getWolfHealth(entity.getId(), entity.getHealth())
            } else {
            // Mods might get the wolf health before our tracker is set, so returning back to the original value.
                return entity.getHealth()
            }
        }
        
    }}
