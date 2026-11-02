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
package com.viaversion.viafabricplus.injection.mixin.features.v1_18_2

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import com.viaversion.viafabricplus.injection.access.v1_18_2.IMultiPlayerGameMode
import org.spongepowered.asm.mixin.Final
import com.viaversion.viafabricplus.features.v1_18_2.ClientPlayerInteractionManager1_18_2
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(net.minecraft.client.multiplayer.MultiPlayerGameMode::class) 
abstract class MixinMultiPlayerGameMode : IMultiPlayerGameMode {

    @field:org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField val connection: net.minecraft.client.multiplayer.ClientPacketListener = uninitialized()
    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField val `viaFabricPlus$1_18_2InteractionManager`: ClientPlayerInteractionManager1_18_2 = ClientPlayerInteractionManager1_18_2()
    
    @Inject(method = ["startPrediction"], at = [At("HEAD")]) private fun trackPlayerAction(level: net.minecraft.client.multiplayer.ClientLevel, predictiveAction: net.minecraft.client.multiplayer.prediction.PredictiveAction, ci: CallbackInfo) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().betweenInclusive(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_14_4, com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_18_2) && predictiveAction is net.minecraft.network.protocol.game.ServerboundPlayerActionPacket) {
            this.`viaFabricPlus$1_18_2InteractionManager`.trackPlayerAction(predictiveAction.getAction(), predictiveAction.getPos())
        }
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["startDestroyBlock", "stopDestroyBlock"], at = [At(value = "NEW", target = "(Lnet/minecraft/network/protocol/game/ServerboundPlayerActionPacket\$Action;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Lnet/minecraft/network/protocol/game/ServerboundPlayerActionPacket;")]) private fun trackPlayerAction(action: net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action, pos: net.minecraft.core.BlockPos, direction: net.minecraft.core.Direction): net.minecraft.network.protocol.game.ServerboundPlayerActionPacket {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().betweenInclusive(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_14_4, com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_18_2)) {
            this.`viaFabricPlus$1_18_2InteractionManager`.trackPlayerAction(action, pos)
        }
        return net.minecraft.network.protocol.game.ServerboundPlayerActionPacket(action, pos, direction)
    }
    
    @com.llamalad7.mixinextras.injector.v2.WrapWithCondition(method = ["useItem"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;startPrediction(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/client/multiplayer/prediction/PredictiveAction;)V")]) private fun fixPacketOrder(instance: net.minecraft.client.multiplayer.MultiPlayerGameMode, level: net.minecraft.client.multiplayer.ClientLevel, predictiveAction: net.minecraft.client.multiplayer.prediction.PredictiveAction, player: net.minecraft.world.entity.player.Player, hand: net.minecraft.world.InteractionHand): kotlin.Boolean {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_18_2)) {
            this.connection.send(net.minecraft.network.protocol.game.ServerboundUseItemPacket(hand, 0, player.getYRot(), player.getXRot()))
            predictiveAction.predict(0)
            return false
        } else {
            return true
        }
    }
    
    override fun `viaFabricPlus$get1_18_2InteractionManager`(): ClientPlayerInteractionManager1_18_2 {
        return this.`viaFabricPlus$1_18_2InteractionManager`
    }
    
}
