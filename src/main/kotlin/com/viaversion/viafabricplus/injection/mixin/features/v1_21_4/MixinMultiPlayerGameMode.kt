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
package com.viaversion.viafabricplus.injection.mixin.features.v1_21_4

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.injection.ModifyVariable
import org.spongepowered.asm.mixin.injection.At
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper
import com.viaversion.viaversion.protocols.v1_21_2to1_21_4.packet.ServerboundPackets1_21_4
import com.viaversion.viaversion.api.type.types.version.VersionedTypes
import com.viaversion.viaversion.protocols.v1_21_4to1_21_5.Protocol1_21_4To1_21_5
import com.viaversion.viaversion.protocols.v1_16_1to1_16_2.packet.ServerboundPackets1_16_2
import com.viaversion.viafabricplus.injection.access.v1_21_4.IAbstractContainerMenu
import com.viaversion.viaversion.protocols.v1_16_4to1_17.Protocol1_16_4To1_17

@Mixin(net.minecraft.client.multiplayer.MultiPlayerGameMode::class) 
abstract class MixinMultiPlayerGameMode {

    @org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField val minecraft: net.minecraft.client.Minecraft = uninitialized()
    @org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$oldCursorStack`: net.minecraft.world.item.ItemStack = uninitialized()
    @org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$oldItems`: kotlin.collections.MutableList<net.minecraft.world.item.ItemStack> = uninitialized()
    @ModifyVariable(method = ["handleContainerInput"], at = [At(value = "STORE")], name = ["itemsBeforeClick"]) private fun captureOldItems(itemsBeforeClick: kotlin.collections.MutableList<net.minecraft.world.item.ItemStack>): kotlin.collections.MutableList<net.minecraft.world.item.ItemStack> {
        `viaFabricPlus$oldCursorStack` = minecraft.player.containerMenu.getCarried().copy()
        return itemsBeforeClick.also { this.`viaFabricPlus$oldItems` = it }
    }
    
    @com.llamalad7.mixinextras.injector.v2.WrapWithCondition(method = ["handleContainerInput"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V")]) private fun handleWindowClick(instance: net.minecraft.client.multiplayer.ClientPacketListener, packet: net.minecraft.network.protocol.Packet<*>): kotlin.Boolean {
        val clickSlotPacket: net.minecraft.network.protocol.game.ServerboundContainerClickPacket = packet as net.minecraft.network.protocol.game.ServerboundContainerClickPacket
        
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_16_4)) {
            // Contains item before modification and not the actual item
            `viaFabricPlus$clickSlot1_16_5`(clickSlotPacket)
            return false
        } else if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_4)) {
            // Contains the actual item and not only the item hash
            `viaFabricPlus$clickSlot1_21_4`(clickSlotPacket)
            return false
        }
        
        return true
    }
    
    @org.spongepowered.asm.mixin.Unique private fun `viaFabricPlus$clickSlot1_21_4`(packet: net.minecraft.network.protocol.game.ServerboundContainerClickPacket) {
        val containerClick: PacketWrapper = PacketWrapper.create(ServerboundPackets1_21_4.CONTAINER_CLICK, com.viaversion.viafabricplus.ViaFabricPlus.api().userConnection())
        containerClick.write<kotlin.Int>(com.viaversion.viaversion.api.type.Types.VAR_INT, packet.containerId())
        containerClick.write<kotlin.Int>(com.viaversion.viaversion.api.type.Types.VAR_INT, packet.stateId())
        containerClick.write<kotlin.Short>(com.viaversion.viaversion.api.type.Types.SHORT, packet.slotNum())
        containerClick.write<kotlin.Byte>(com.viaversion.viaversion.api.type.Types.BYTE, packet.buttonNum())
        containerClick.write<kotlin.Int>(com.viaversion.viaversion.api.type.Types.VAR_INT, packet.containerInput().id())
        
        val modifiedStacks: it.unimi.dsi.fastutil.ints.Int2ObjectMap<net.minecraft.network.HashedStack> = packet.changedSlots()
        containerClick.write<kotlin.Int>(com.viaversion.viaversion.api.type.Types.VAR_INT, modifiedStacks.size)
        for (entry in modifiedStacks.int2ObjectEntrySet()) {
            val itemStack: net.minecraft.world.item.ItemStack = minecraft.player.containerMenu.slots.get(entry.getIntKey()).getItem()
            containerClick.write<kotlin.Short>(com.viaversion.viaversion.api.type.Types.SHORT, entry.getIntKey().toShort())
            containerClick.write<com.viaversion.viaversion.api.minecraft.item.Item>(VersionedTypes.V1_21_4.item, com.viaversion.viafabricplus.ViaFabricPlus.api().conversions().translateItem(itemStack, com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_4))
        }
        
        val cursorStack: net.minecraft.world.item.ItemStack = minecraft.player.containerMenu.getCarried()
        containerClick.write<com.viaversion.viaversion.api.minecraft.item.Item>(VersionedTypes.V1_21_4.item, com.viaversion.viafabricplus.ViaFabricPlus.api().conversions().translateItem(cursorStack, com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_4))
        containerClick.scheduleSendToServer(Protocol1_21_4To1_21_5::class.java)
    }
    
    @org.spongepowered.asm.mixin.Unique private fun `viaFabricPlus$clickSlot1_16_5`(packet: net.minecraft.network.protocol.game.ServerboundContainerClickPacket) {
        var slotItemBeforeModification: net.minecraft.world.item.ItemStack = uninitialized()
        if (this.`viaFabricPlus$shouldBeEmpty`(packet.containerInput(), packet.slotNum().toInt())) {
            slotItemBeforeModification = net.minecraft.world.item.ItemStack.EMPTY
        } else if (packet.slotNum() < 0 || packet.slotNum() >= `viaFabricPlus$oldItems`.size) {
            slotItemBeforeModification = `viaFabricPlus$oldCursorStack`
        } else {
            slotItemBeforeModification = `viaFabricPlus$oldItems`.get(packet.slotNum().toInt())
        }
        
        val containerClick: PacketWrapper = PacketWrapper.create(ServerboundPackets1_16_2.CONTAINER_CLICK, com.viaversion.viafabricplus.ViaFabricPlus.api().userConnection())
        containerClick.write<kotlin.Byte>(com.viaversion.viaversion.api.type.Types.BYTE, packet.containerId().toByte())
        containerClick.write<kotlin.Short>(com.viaversion.viaversion.api.type.Types.SHORT, packet.slotNum())
        containerClick.write<kotlin.Byte>(com.viaversion.viaversion.api.type.Types.BYTE, packet.buttonNum())
        containerClick.write<kotlin.Short>(com.viaversion.viaversion.api.type.Types.SHORT, (minecraft.player.containerMenu as IAbstractContainerMenu).`viaFabricPlus$incrementAndGetActionId`())
        containerClick.write<kotlin.Int>(com.viaversion.viaversion.api.type.Types.VAR_INT, packet.containerInput().ordinal)
        containerClick.write<com.viaversion.viaversion.api.minecraft.item.Item>(com.viaversion.viaversion.api.type.Types.ITEM1_13_2, com.viaversion.viafabricplus.ViaFabricPlus.api().conversions().translateItem(slotItemBeforeModification, com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_16_4))
        containerClick.scheduleSendToServer(Protocol1_16_4To1_17::class.java)
        
        `viaFabricPlus$oldCursorStack` = uninitialized()
        `viaFabricPlus$oldItems` = uninitialized()
    }
    
    @org.spongepowered.asm.mixin.Unique private fun `viaFabricPlus$shouldBeEmpty`(type: net.minecraft.world.inventory.ContainerInput, slot: kotlin.Int): kotlin.Boolean {
        // quick craft always uses empty stack for verification
        if (type == net.minecraft.world.inventory.ContainerInput.QUICK_CRAFT) return true
        
                // Special case: throw always uses empty stack for verification
        if (type == net.minecraft.world.inventory.ContainerInput.THROW) return true
        
                // quick move always uses empty stack for verification since 1.12
        if (type == net.minecraft.world.inventory.ContainerInput.QUICK_MOVE && com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_11_1)) return true
        
                // pickup with slot -999 (outside window) to throw items always uses empty stack for verification
        return type == net.minecraft.world.inventory.ContainerInput.PICKUP && slot == -999
    }
    
}
