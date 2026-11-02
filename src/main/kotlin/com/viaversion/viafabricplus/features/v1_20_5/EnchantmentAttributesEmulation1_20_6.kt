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
package com.viaversion.viafabricplus.features.v1_20_5

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import com.viaversion.viafabricplus.features.v1_20_5.EnchantmentAttributesEmulation1_20_6

object EnchantmentAttributesEmulation1_20_6 {
    fun init() {
        ClientTickEvents.START_LEVEL_TICK.register(ClientTickEvents.StartLevelTick {
        world: net.minecraft.client.multiplayer.ClientLevel -> if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20_5)) {
            return@StartLevelTick 
        }
        
                    // Update generic attributes for all entities
        for (entity in world.entitiesForRendering()) {
            if (entity.isLocalInstanceAuthoritative() && entity is net.minecraft.world.entity.LivingEntity) {
                EnchantmentAttributesEmulation1_20_6.setAttribute(entity, net.minecraft.world.entity.ai.attributes.Attributes.WATER_MOVEMENT_EFFICIENCY, EnchantmentAttributesEmulation1_20_6.getEquipmentLevel(net.minecraft.world.item.enchantment.Enchantments.DEPTH_STRIDER, entity) / 3.0)
                EnchantmentAttributesEmulation1_20_6.setGenericMovementEfficiencyAttribute(entity)
            }
        }
        
                    // Update player-specific attributes for all players
        for (player in world.players()) {
            if (!player.isLocalInstanceAuthoritative()) {
                continue 
            }
            
            val efficiencyLevel: kotlin.Int = EnchantmentAttributesEmulation1_20_6.getEquipmentLevel(net.minecraft.world.item.enchantment.Enchantments.EFFICIENCY, player)
            EnchantmentAttributesEmulation1_20_6.setAttribute(player, net.minecraft.world.entity.ai.attributes.Attributes.MINING_EFFICIENCY, if (efficiencyLevel > 0) efficiencyLevel * efficiencyLevel + 1.0 else 0.0)
            EnchantmentAttributesEmulation1_20_6.setAttribute(player, net.minecraft.world.entity.ai.attributes.Attributes.SNEAKING_SPEED, 0.3 + EnchantmentAttributesEmulation1_20_6.getEquipmentLevel(net.minecraft.world.item.enchantment.Enchantments.SWIFT_SNEAK, player) * 0.15)
            EnchantmentAttributesEmulation1_20_6.setAttribute(player, net.minecraft.world.entity.ai.attributes.Attributes.SUBMERGED_MINING_SPEED, if (EnchantmentAttributesEmulation1_20_6.getEquipmentLevel(net.minecraft.world.item.enchantment.Enchantments.AQUA_AFFINITY, player) <= 0) 0.2 else 1.0)
            EnchantmentAttributesEmulation1_20_6.setAttribute(player, net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_KNOCKBACK, EnchantmentAttributesEmulation1_20_6.getEquipmentLevel(net.minecraft.world.item.enchantment.Enchantments.KNOCKBACK, player).toDouble())
        }
        
        })
    }
    
     /**
 * Called from MixinLivingEntity as well to ensure the attribute value is set at the correct place in the entity tick logic.
 * Called above just as a fallback if a mod accesses the raw attribute value directly.
 */
    fun setGenericMovementEfficiencyAttribute(entity: net.minecraft.world.entity.LivingEntity) {
        val isOnSoulSpeedBlock: kotlin.Boolean = entity.level().getBlockState(entity.getBlockPosBelowThatAffectsMyMovement()).`is`(net.minecraft.tags.BlockTags.SOUL_SPEED_BLOCKS)
        EnchantmentAttributesEmulation1_20_6.setAttribute(entity, net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_EFFICIENCY, (if (isOnSoulSpeedBlock && EnchantmentAttributesEmulation1_20_6.getEquipmentLevel(net.minecraft.world.item.enchantment.Enchantments.SOUL_SPEED, entity) > 0) 1 else 0).toDouble())
    }
    
    private fun getEquipmentLevel(enchantment: net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment>, livingEntity: net.minecraft.world.entity.LivingEntity): kotlin.Int {
        return net.minecraft.world.item.enchantment.EnchantmentHelper.getEnchantmentLevel(livingEntity.level().registryAccess().getOrThrow<net.minecraft.world.item.enchantment.Enchantment>(enchantment), livingEntity)
    }
    
    private fun setAttribute(entity: net.minecraft.world.entity.LivingEntity, attribute: net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute>, value: kotlin.Double) {
        val attributeInstance: net.minecraft.world.entity.ai.attributes.AttributeInstance = entity.getAttribute(attribute)
        attributeInstance.removeModifiers() // Minecraft is applying attribute modifiers in some situations, remove them before we set the base value
        attributeInstance.setBaseValue(value)
    }
    
}
