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
package com.viaversion.viafabricplus.features.v1_21_2

import com.mojang.logging.LogUtils
import com.viaversion.viafabricplus.features.v1_21_2.ItemPick1_21_3
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper
import com.viaversion.viaversion.protocols.v1_21to1_21_2.packet.ServerboundPackets1_21_2
import com.viaversion.viaversion.protocols.v1_21_2to1_21_4.Protocol1_21_2To1_21_4

object ItemPick1_21_3 {
    private val LOGGER: org.slf4j.Logger = LogUtils.getLogger()
    
    private fun addPickBlock(inventory: net.minecraft.world.entity.player.Inventory, stack: net.minecraft.world.item.ItemStack) {
        val index: kotlin.Int = inventory.findSlotMatchingItem(stack)
        if (net.minecraft.world.entity.player.Inventory.isHotbarSlot(index)) {
            inventory.setSelectedSlot(index)
        } else if (index != -1) {
            inventory.pickSlot(index)
        } else {
            inventory.addAndPickItem(stack)
        }
    }
    
    private fun addBlockEntityNbt(stack: net.minecraft.world.item.ItemStack, blockEntity: net.minecraft.world.level.block.entity.BlockEntity, manager: net.minecraft.core.RegistryAccess) {
        net.minecraft.util.ProblemReporter.ScopedCollector(blockEntity.problemPath(), ItemPick1_21_3.LOGGER).use {
        logging -> val view: net.minecraft.world.level.storage.TagValueOutput = net.minecraft.world.level.storage.TagValueOutput.createWithContext(logging, manager)
        blockEntity.saveMetadata(view)
        net.minecraft.world.item.BlockItem.setBlockEntityData(stack, blockEntity.getType(), view)
        stack.applyComponents(blockEntity.collectComponents())
        
        } }
    
    fun doItemPick(client: net.minecraft.client.Minecraft) {
        val creativeMode: kotlin.Boolean = client.player.getAbilities().instabuild
        
        val itemStack: net.minecraft.world.item.ItemStack
        val crosshairTarget: net.minecraft.world.phys.HitResult = client.hitResult
        if (crosshairTarget.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK) {
            val blockPos: net.minecraft.core.BlockPos = (crosshairTarget as net.minecraft.world.phys.BlockHitResult).getBlockPos()
            val blockState: net.minecraft.world.level.block.state.BlockState = client.level.getBlockState(blockPos)
            if (blockState.isAir()) {
                return 
            }
            
            val block: net.minecraft.world.level.block.Block = blockState.getBlock()
            itemStack = block.getCloneItemStack(client.level, blockPos, blockState, false)
            if (itemStack.isEmpty()) {
                return 
            }
            
            if (creativeMode && client.hasControlDown() && blockState.hasBlockEntity()) {
                val blockEntity: net.minecraft.world.level.block.entity.BlockEntity = client.level.getBlockEntity(blockPos)
                if (blockEntity != null) {
                    ItemPick1_21_3.addBlockEntityNbt(itemStack, blockEntity, client.level.registryAccess())
                }
            }
        } else {
            if (crosshairTarget.getType() != net.minecraft.world.phys.HitResult.Type.ENTITY || !creativeMode) {
                return 
            }
            
            val entity: net.minecraft.world.entity.Entity = (crosshairTarget as net.minecraft.world.phys.EntityHitResult).getEntity()
            itemStack = entity.getPickResult()
            if (itemStack == null) {
                return 
            }
        }
        
        if (itemStack.isEmpty()) {
            return 
        }
        
        val inventory: net.minecraft.world.entity.player.Inventory = client.player.getInventory()
        val index: kotlin.Int = inventory.findSlotMatchingItem(itemStack)
        if (creativeMode) {
            ItemPick1_21_3.addPickBlock(inventory, itemStack)
            client.gameMode.handleCreativeModeItemAdd(client.player.getItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND), 36 + inventory.getSelectedSlot())
        } else if (index != -1) {
            if (net.minecraft.world.entity.player.Inventory.isHotbarSlot(index)) {
                inventory.setSelectedSlot(index)
                return 
            }
            
            val pickFromInventory: PacketWrapper = PacketWrapper.create(ServerboundPackets1_21_2.PICK_ITEM, com.viaversion.viafabricplus.ViaFabricPlus.api().userConnection())
            pickFromInventory.write<kotlin.Int>(com.viaversion.viaversion.api.type.Types.VAR_INT, index)
            pickFromInventory.scheduleSendToServer(Protocol1_21_2To1_21_4::class.java)
        }
    }
    
}
