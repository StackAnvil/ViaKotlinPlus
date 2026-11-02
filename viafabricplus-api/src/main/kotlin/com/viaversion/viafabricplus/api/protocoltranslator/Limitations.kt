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
package com.viaversion.viafabricplus.api.protocoltranslator

import com.viaversion.viafabricplus.ViaFabricPlus
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import net.minecraft.core.Holder
import net.minecraft.resources.ResourceKey
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.level.block.entity.BannerPattern

 /**
 * Version-dependent limitations.
 */
interface Limitations {
 /**
 * Calculates the maximum chat length for the current target version.
 * 
 * @return The maximum chat length
 */
    fun maxChatLength(): Int {
        return maxChatLength(ViaFabricPlus.api().targetVersion())
    }
    
     /**
 * Calculates the maximum chat length for given [ProtocolVersion] instance.
 * 
 * @return The maximum chat length
 */
    fun maxChatLength(version: ProtocolVersion): Int
    
     /**
 * @param item    The item to check
 * @param version The version to check for
 * @return true if the item exists in the given version, false otherwise; this will also check for CPE items (CustomBlocks V1 extension)
 */
    fun itemExists(item: Item, version: ProtocolVersion): Boolean
    
     /**
 * @param enchantment The enchantment to check
 * @param version     The version to check for
 * @return true if the enchantment exists in the given version, false otherwise
 */
    fun enchantmentExists(enchantment: ResourceKey<Enchantment>, version: ProtocolVersion): Boolean
    
     /**
 * @param effect  The status effect to check
 * @param version The version to check for
 * @return true if the status effect exists in the given version, false otherwise
 */
    fun effectExists(effect: Holder<MobEffect>, version: ProtocolVersion): Boolean
    
     /**
 * @param pattern The banner pattern to check
 * @param version The version to check for
 * @return true if the banner pattern exists in the given version, false otherwise
 */
    fun bannerPatternExists(pattern: ResourceKey<BannerPattern>, version: ProtocolVersion): Boolean
    
     /**
 * Similar to [.itemExists], but takes in the current connection details (e.g., classic protocol extensions being loaded)
 * 
 * @param item The item to check
 * @return true if the item exists in the current connection, false otherwise
 */
    fun itemExistsInConnection(item: Item): Boolean
    
     /**
 * Same as [.itemExists], but for item stacks. This also compares against certain data components like enchantments or banner patterns.
 * 
 * @param stack The item stack to check
 * @return true if the item stack exists in the given version, false otherwise
 */
    fun itemExistsInConnection(stack: ItemStack): Boolean
    
     /**
 * Similar to [ItemStack.getCount], but also handles negative item counts in pre 1.11 versions
 * 
 * @param stack The item stack to get the count of
 * @return the count of the item stack can be negative in pre 1.11 versions
 */
    fun getStackCount(stack: ItemStack): Int
 }
