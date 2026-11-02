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
package com.viaversion.viafabricplus.injection.mixin.features.v1_13_2.screen

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import com.viaversion.viafabricplus.injection.mixin.features.v1_13_2.screen.MixinMerchantMenu

@Mixin(net.minecraft.world.inventory.MerchantMenu::class) 
abstract class MixinMerchantMenu protected constructor(type: net.minecraft.world.inventory.MenuType<*>, syncId: kotlin.Int) : net.minecraft.world.inventory.AbstractContainerMenu(type, syncId) {

    @org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField val tradeContainer: net.minecraft.world.inventory.MerchantContainer = uninitialized()
    @org.spongepowered.asm.mixin.Shadow abstract fun getOffers(): net.minecraft.world.item.trading.MerchantOffers
    
    @Inject(method = ["tryMoveItems"], at = [At("HEAD")], cancellable = true) private fun onSwitchTo(newTradeIndex: kotlin.Int, ci: CallbackInfo) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_13_2)) {
            ci.cancel()
            
            if (newTradeIndex >= this.getOffers().size) {
                return 
            }
            
            val interactionManager: net.minecraft.client.multiplayer.MultiPlayerGameMode = net.minecraft.client.Minecraft.getInstance().gameMode
            val player: net.minecraft.client.player.LocalPlayer = net.minecraft.client.Minecraft.getInstance().player
            
                        // move 1st input slot to inventory
            if (!this.tradeContainer.getItem(0).isEmpty()) {
                val count: kotlin.Int = this.tradeContainer.getItem(0).getCount()
                interactionManager.handleContainerInput(containerId, 0, 0, net.minecraft.world.inventory.ContainerInput.QUICK_MOVE, player)
                if (count == this.tradeContainer.getItem(0).getCount()) {
                    return 
                }
            }
            
                        // move 2nd input slot to inventory
            if (!this.tradeContainer.getItem(1).isEmpty()) {
                val count: kotlin.Int = this.tradeContainer.getItem(1).getCount()
                interactionManager.handleContainerInput(containerId, 1, 0, net.minecraft.world.inventory.ContainerInput.QUICK_MOVE, player)
                if (count == this.tradeContainer.getItem(1).getCount()) {
                    return 
                }
            }
            
                        // refill the slots
            if (this.tradeContainer.getItem(0).isEmpty() && this.tradeContainer.getItem(1).isEmpty()) {
                val tradeOffer: net.minecraft.world.item.trading.MerchantOffer = this.getOffers().get(newTradeIndex)
                this.`viaFabricPlus$autofill`(interactionManager, player, 0, tradeOffer.getItemCostA())
                tradeOffer.getItemCostB().ifPresent(java.util.function.Consumer {item: net.minecraft.world.item.trading.ItemCost -> this.`viaFabricPlus$autofill`(interactionManager, player, 1, item)})
            }
        }
    }
    
    @Inject(method = ["canTakeItemForPickAll"], at = [At("HEAD")], cancellable = true) private fun modifyCanInsertIntoSlot(carried: net.minecraft.world.item.ItemStack, target: net.minecraft.world.inventory.Slot, cir: CallbackInfoReturnable<kotlin.Boolean>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_13_2)) {
            cir.setReturnValue(true)
        }
    }
    
    @org.spongepowered.asm.mixin.Unique private fun `viaFabricPlus$autofill`(interactionManager: net.minecraft.client.multiplayer.MultiPlayerGameMode, player: net.minecraft.client.player.LocalPlayer, inputSlot: kotlin.Int, stackNeeded: net.minecraft.world.item.trading.ItemCost) {
        var slot: kotlin.Int = 0
        slot = MixinMerchantMenu.Companion.INV_SLOT_START
        while (slot < MixinMerchantMenu.Companion.USE_ROW_SLOT_END) {
            val itemStack: net.minecraft.world.item.ItemStack = this.slots.get(slot).getItem()
            if (!itemStack.isEmpty() && stackNeeded.test(itemStack)) {
                val itemStack2: net.minecraft.world.item.ItemStack = this.tradeContainer.getItem(inputSlot)
                if (itemStack2.isEmpty() || net.minecraft.world.item.ItemStack.isSameItemSameComponents(itemStack, itemStack2)) {
                    break
                }
            }
            slot++}
        if (slot == MixinMerchantMenu.Companion.USE_ROW_SLOT_END) {
            return 
        }
        
        val wasHoldingItem: kotlin.Boolean = !player.containerMenu.getCarried().isEmpty()
        interactionManager.handleContainerInput(containerId, slot, 0, net.minecraft.world.inventory.ContainerInput.PICKUP, player)
        interactionManager.handleContainerInput(containerId, slot, 0, net.minecraft.world.inventory.ContainerInput.PICKUP_ALL, player)
        interactionManager.handleContainerInput(containerId, inputSlot, 0, net.minecraft.world.inventory.ContainerInput.PICKUP, player)
        if (wasHoldingItem) {
            interactionManager.handleContainerInput(containerId, slot, 0, net.minecraft.world.inventory.ContainerInput.PICKUP, player)
        }
    }
    
    private companion object {
        @field:org.spongepowered.asm.mixin.Shadow @Final 
        @JvmField val INV_SLOT_START: kotlin.Int = 0
        
        @field:org.spongepowered.asm.mixin.Shadow @Final 
        @JvmField val USE_ROW_SLOT_END: kotlin.Int = 0
        
    }}
