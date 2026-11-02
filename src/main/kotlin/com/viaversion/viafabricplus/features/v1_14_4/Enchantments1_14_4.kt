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
package com.viaversion.viafabricplus.features.v1_14_4

import java.util.HashMap
import com.viaversion.viafabricplus.features.v1_14_4.Enchantments1_14_4

object Enchantments1_14_4 {
    private val ENCHANTMENT_REGISTRY: kotlin.collections.MutableMap<kotlin.String, net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment>> = HashMap<kotlin.String, net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment>>()
    
    init {
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("protection", net.minecraft.world.item.enchantment.Enchantments.PROTECTION)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("fire_protection", net.minecraft.world.item.enchantment.Enchantments.FIRE_PROTECTION)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("feather_falling", net.minecraft.world.item.enchantment.Enchantments.FEATHER_FALLING)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("blast_protection", net.minecraft.world.item.enchantment.Enchantments.BLAST_PROTECTION)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("projectile_protection", net.minecraft.world.item.enchantment.Enchantments.PROJECTILE_PROTECTION)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("respiration", net.minecraft.world.item.enchantment.Enchantments.RESPIRATION)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("aqua_affinity", net.minecraft.world.item.enchantment.Enchantments.AQUA_AFFINITY)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("thorns", net.minecraft.world.item.enchantment.Enchantments.THORNS)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("depth_strider", net.minecraft.world.item.enchantment.Enchantments.DEPTH_STRIDER)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("frost_walker", net.minecraft.world.item.enchantment.Enchantments.FROST_WALKER)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("binding_curse", net.minecraft.world.item.enchantment.Enchantments.BINDING_CURSE)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("sharpness", net.minecraft.world.item.enchantment.Enchantments.SHARPNESS)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("smite", net.minecraft.world.item.enchantment.Enchantments.SMITE)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("bane_of_arthropods", net.minecraft.world.item.enchantment.Enchantments.BANE_OF_ARTHROPODS)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("knockback", net.minecraft.world.item.enchantment.Enchantments.KNOCKBACK)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("fire_aspect", net.minecraft.world.item.enchantment.Enchantments.FIRE_ASPECT)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("looting", net.minecraft.world.item.enchantment.Enchantments.LOOTING)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("sweeping", net.minecraft.world.item.enchantment.Enchantments.SWEEPING_EDGE)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("efficiency", net.minecraft.world.item.enchantment.Enchantments.EFFICIENCY)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("silk_touch", net.minecraft.world.item.enchantment.Enchantments.SILK_TOUCH)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("unbreaking", net.minecraft.world.item.enchantment.Enchantments.UNBREAKING)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("fortune", net.minecraft.world.item.enchantment.Enchantments.FORTUNE)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("power", net.minecraft.world.item.enchantment.Enchantments.POWER)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("punch", net.minecraft.world.item.enchantment.Enchantments.PUNCH)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("flame", net.minecraft.world.item.enchantment.Enchantments.FLAME)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("infinity", net.minecraft.world.item.enchantment.Enchantments.INFINITY)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("luck_of_the_sea", net.minecraft.world.item.enchantment.Enchantments.LUCK_OF_THE_SEA)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("lure", net.minecraft.world.item.enchantment.Enchantments.LURE)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("loyalty", net.minecraft.world.item.enchantment.Enchantments.LOYALTY)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("impaling", net.minecraft.world.item.enchantment.Enchantments.IMPALING)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("riptide", net.minecraft.world.item.enchantment.Enchantments.RIPTIDE)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("channeling", net.minecraft.world.item.enchantment.Enchantments.CHANNELING)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("multishot", net.minecraft.world.item.enchantment.Enchantments.MULTISHOT)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("quick_charge", net.minecraft.world.item.enchantment.Enchantments.QUICK_CHARGE)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("piercing", net.minecraft.world.item.enchantment.Enchantments.PIERCING)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("mending", net.minecraft.world.item.enchantment.Enchantments.MENDING)
        Enchantments1_14_4.ENCHANTMENT_REGISTRY.put("vanishing_curse", net.minecraft.world.item.enchantment.Enchantments.VANISHING_CURSE)
    }
    
    fun getOrEmpty(identifier: kotlin.String): java.util.Optional<net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment>> {
        return java.util.Optional.ofNullable<net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment>>(Enchantments1_14_4.ENCHANTMENT_REGISTRY.get(com.viaversion.viaversion.util.Key.stripMinecraftNamespace(identifier)))
    }
    
}
