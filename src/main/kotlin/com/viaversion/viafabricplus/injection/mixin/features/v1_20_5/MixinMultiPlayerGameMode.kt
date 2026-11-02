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
package com.viaversion.viafabricplus.injection.mixin.features.v1_20_5

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(net.minecraft.client.multiplayer.MultiPlayerGameMode::class) 
abstract class MixinMultiPlayerGameMode {

    @org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField val connection: net.minecraft.client.multiplayer.ClientPacketListener = uninitialized()
    @Inject(method = ["useItem"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;ensureHasSentCarriedItem()V", shift = At.Shift.AFTER)]) private fun sendPlayerPosPacket(player: net.minecraft.world.entity.player.Player, hand: net.minecraft.world.InteractionHand, cir: CallbackInfoReturnable<net.minecraft.world.InteractionResult>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().betweenInclusive(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_17, com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20_5)) {
            this.connection.send(net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.PosRot(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), player.onGround(), player.horizontalCollision))
        }
    }
    
}
