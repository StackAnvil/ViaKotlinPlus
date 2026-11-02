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
package com.viaversion.viafabricplus.injection.mixin.features.v1_20_5.item

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import com.viaversion.viafabricplus.injection.access.v1_20_5.IDisplayDefault
import org.spongepowered.asm.mixin.injection.At

@Mixin(net.minecraft.world.item.component.ItemAttributeModifiers.Display.Default::class) 
abstract class MixinItemAttributeModifiers_Display_Default : IDisplayDefault {

    @org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$itemEnchantments`: net.minecraft.world.item.enchantment.ItemEnchantments = uninitialized()
    override fun `viaFabricPlus$setItemEnchantments`(itemEnchantments: net.minecraft.world.item.enchantment.ItemEnchantments) {
        `viaFabricPlus$itemEnchantments` = itemEnchantments
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["apply"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getAttributeBaseValue(Lnet/minecraft/core/Holder;)D", ordinal = 0)]) private fun fixAttackDamageCalculation(instance: net.minecraft.world.entity.player.Player, registryEntry: net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute>): kotlin.Double {
        var value: kotlin.Double = 0.0
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20_5)) {
            for (enchantment in `viaFabricPlus$itemEnchantments`.keySet()) {
                if (enchantment.`is`(net.minecraft.world.item.enchantment.Enchantments.SHARPNESS)) {
                    val level: kotlin.Int = `viaFabricPlus$itemEnchantments`.getLevel(enchantment)
                    if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8)) {
                        value = (level * 1.25f).toDouble()
                    } else {
                        value = (1.0f + kotlin.math.max(0, level - 1).toFloat() * 0.5f).toDouble()
                    }
                    break
                }
            }
        }
        
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8)) {
            return value
        } else {
            return instance.getAttributeBaseValue(registryEntry) + value
        }
    }
    
}
