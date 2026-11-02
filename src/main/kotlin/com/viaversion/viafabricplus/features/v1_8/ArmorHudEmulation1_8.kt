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
package com.viaversion.viafabricplus.features.v1_8

import java.util.UUID
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import com.viaversion.viafabricplus.features.v1_8.ArmorHudEmulation1_8
import com.viaversion.viafabricplus.ViaFabricPlusImpl
import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viaversion.protocols.v1_8to1_9.data.ArmorTypes1_8
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper
import com.viaversion.viaversion.protocols.v1_8to1_9.packet.ClientboundPackets1_9
import com.viaversion.viaversion.protocols.v1_8to1_9.Protocol1_8To1_9

object ArmorHudEmulation1_8 {
    private val ARMOR_POINTS_UUID: UUID = UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")
    private val ARMOR_SLOTS: kotlin.Array<net.minecraft.world.entity.EquipmentSlot> = kotlin.arrayOf<net.minecraft.world.entity.EquipmentSlot>(net.minecraft.world.entity.EquipmentSlot.HEAD, net.minecraft.world.entity.EquipmentSlot.CHEST, net.minecraft.world.entity.EquipmentSlot.LEGS, net.minecraft.world.entity.EquipmentSlot.FEET)
    
    private var previousArmorPoints: kotlin.Double = 0.0
    
    fun init() {
        ClientTickEvents.START_LEVEL_TICK.register(ClientTickEvents.StartLevelTick {`_`: net.minecraft.client.multiplayer.ClientLevel -> if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8)) {
            if (net.minecraft.client.Minecraft.getInstance().player != null) {
                try {
                    ArmorHudEmulation1_8.sendArmorUpdate(com.viaversion.viafabricplus.ViaFabricPlus.api().userConnection()!!)
                }catch (t: Throwable) {
                    ViaFabricPlusImpl.impl().logger().error("Error sending armor update", t)
                }
            } else {
                ArmorHudEmulation1_8.previousArmorPoints = 0.0
            }
        }
        })
    }
    
    private fun sendArmorUpdate(connection: UserConnection) {
        // Calculate the armor points.
        var armor: kotlin.Int = 0
        for (slot in ArmorHudEmulation1_8.ARMOR_SLOTS) {
            val stack: net.minecraft.world.item.ItemStack = net.minecraft.client.Minecraft.getInstance().player.getInventory().equipment.get(slot)
            if (stack.isEmpty()) {
                continue 
            }
            
            val identifier: kotlin.String = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).toString()
            armor += ArmorTypes1_8.findByType(identifier).getArmorPoints()
        }
        
                // We only want to update the armor points if they actually changed.
        if (armor.toDouble() == ArmorHudEmulation1_8.previousArmorPoints) {
            return 
        }
        ArmorHudEmulation1_8.previousArmorPoints = armor.toDouble()
        
        val updateAttributes: PacketWrapper = PacketWrapper.create(ClientboundPackets1_9.UPDATE_ATTRIBUTES, connection)
        updateAttributes.write<kotlin.Int>(com.viaversion.viaversion.api.type.Types.VAR_INT, net.minecraft.client.Minecraft.getInstance().player.getId())
        updateAttributes.write<kotlin.Int>(com.viaversion.viaversion.api.type.Types.INT, 1)
        updateAttributes.write<kotlin.String>(com.viaversion.viaversion.api.type.Types.STRING, "generic.armor")
        updateAttributes.write<kotlin.Double>(com.viaversion.viaversion.api.type.Types.DOUBLE, 0.0)
        updateAttributes.write<kotlin.Int>(com.viaversion.viaversion.api.type.Types.VAR_INT, 1)
        updateAttributes.write<UUID>(com.viaversion.viaversion.api.type.Types.UUID, ArmorHudEmulation1_8.ARMOR_POINTS_UUID)
        updateAttributes.write<kotlin.Double>(com.viaversion.viaversion.api.type.Types.DOUBLE, armor.toDouble())
        updateAttributes.write<kotlin.Byte>(com.viaversion.viaversion.api.type.Types.BYTE, 0.toByte())
        updateAttributes.scheduleSend(Protocol1_8To1_9::class.java)
    }
    
}
