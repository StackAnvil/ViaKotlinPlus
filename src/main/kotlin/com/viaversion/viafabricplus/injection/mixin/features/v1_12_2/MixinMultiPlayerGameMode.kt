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
package com.viaversion.viafabricplus.injection.mixin.features.v1_12_2

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import com.viaversion.viafabricplus.features.v1_12_2.ActionResultException1_12_2
import org.spongepowered.asm.mixin.Overwrite
import com.viaversion.viafabricplus.protocoltranslator.impl.provider.viaversion.ViaFabricPlusHandItemProvider

@Mixin(net.minecraft.client.multiplayer.MultiPlayerGameMode::class) 
abstract class MixinMultiPlayerGameMode {

    @org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField val minecraft: net.minecraft.client.Minecraft = uninitialized()
    @org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField val connection: net.minecraft.client.multiplayer.ClientPacketListener = uninitialized()
    @org.spongepowered.asm.mixin.Shadow 
    @JvmField val localPlayerMode: net.minecraft.world.level.GameType = uninitialized()
    @org.spongepowered.asm.mixin.Shadow protected abstract fun startPrediction(level: net.minecraft.client.multiplayer.ClientLevel, predictiveAction: net.minecraft.client.multiplayer.prediction.PredictiveAction)
    
    @org.spongepowered.asm.mixin.Shadow protected abstract fun performUseItemOn(player: net.minecraft.client.player.LocalPlayer, hand: net.minecraft.world.InteractionHand, blockHit: net.minecraft.world.phys.BlockHitResult): net.minecraft.world.InteractionResult
    
    @Inject(method = ["performUseItemOn"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 2)]) private fun interactBlock1_12_2(player: net.minecraft.client.player.LocalPlayer, hand: net.minecraft.world.InteractionHand, blockHit: net.minecraft.world.phys.BlockHitResult, cir: CallbackInfoReturnable<net.minecraft.world.InteractionResult>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)) {
            val itemStack: net.minecraft.world.item.ItemStack = player.getItemInHand(hand)
            var checkHitResult: net.minecraft.world.phys.BlockHitResult = blockHit
            if (itemStack.getItem() is net.minecraft.world.item.BlockItem) {
                val clickedBlock: net.minecraft.world.level.block.state.BlockState = this.minecraft.level.getBlockState(blockHit.getBlockPos())
                if (clickedBlock.getBlock() == net.minecraft.world.level.block.Blocks.SNOW) {
                    if (clickedBlock.getValue<kotlin.Int>(net.minecraft.world.level.block.SnowLayerBlock.LAYERS) == 1) {
                        checkHitResult = blockHit.withDirection(net.minecraft.core.Direction.UP)
                    }
                }
                val itemUsageContext: net.minecraft.world.item.context.UseOnContext = net.minecraft.world.item.context.UseOnContext(player, hand, checkHitResult)
                val itemPlacementContext: net.minecraft.world.item.context.BlockPlaceContext = net.minecraft.world.item.context.BlockPlaceContext(itemUsageContext)
                if (!itemPlacementContext.canPlace() || (itemPlacementContext.getItemInHand().getItem() as net.minecraft.world.item.BlockItem).getPlacementState(itemPlacementContext) == null) {
                    throw ActionResultException1_12_2(net.minecraft.world.InteractionResult.PASS)
                }
            }
            
            this.connection.send(net.minecraft.network.protocol.game.ServerboundUseItemOnPacket(hand, blockHit, 0))
            if (itemStack.isEmpty()) {
                throw ActionResultException1_12_2(net.minecraft.world.InteractionResult.PASS)
            }
            val itemUsageContext: net.minecraft.world.item.context.UseOnContext = net.minecraft.world.item.context.UseOnContext(player, hand, checkHitResult)
            var actionResult: net.minecraft.world.InteractionResult
            if (this.localPlayerMode.isCreative()) {
                val count: kotlin.Int = itemStack.getCount()
                actionResult = itemStack.useOn(itemUsageContext)
                itemStack.setCount(count)
            } else {
                actionResult = itemStack.useOn(itemUsageContext)
            }
            if (!actionResult.consumesAction()) {
                actionResult = net.minecraft.world.InteractionResult.PASS // In <= 1.12.2 FAIL is the same as PASS
            }
            throw ActionResultException1_12_2(actionResult)
        }
    }
    
     /**
 * @author RK_01
 * @reason Block place fix
 */
    @Overwrite private fun `lambda$useItemOn$0`(mutableObject: org.apache.commons.lang3.mutable.MutableObject<net.minecraft.world.InteractionResult>, clientPlayerEntity: net.minecraft.client.player.LocalPlayer, hand: net.minecraft.world.InteractionHand, blockHitResult: net.minecraft.world.phys.BlockHitResult, sequence: kotlin.Int): net.minecraft.network.protocol.Packet<*> {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8)) {
            ViaFabricPlusHandItemProvider.lastUsedItem = clientPlayerEntity.getItemInHand(hand).copy()
        }
        try {
            mutableObject.setValue(this.performUseItemOn(clientPlayerEntity, hand, blockHitResult))
            return net.minecraft.network.protocol.game.ServerboundUseItemOnPacket(hand, blockHitResult, sequence)
        }catch (e: ActionResultException1_12_2) {
            mutableObject.setValue(e.actionResult)
            throw e
        }
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["useItemOn"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;startPrediction(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/client/multiplayer/prediction/PredictiveAction;)V")]) private fun catchPacketCancelException(instance: net.minecraft.client.multiplayer.MultiPlayerGameMode, level: net.minecraft.client.multiplayer.ClientLevel, predictiveAction: net.minecraft.client.multiplayer.prediction.PredictiveAction) {
        try {
            this.startPrediction(level, predictiveAction)
        }catch (ignored: ActionResultException1_12_2) {
        }
    }
    
}
