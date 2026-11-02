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
package com.viaversion.viafabricplus.injection.mixin.features.v1_20_2.network

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.viaversion.viafabricplus.injection.access.v1_20_2.ILevelLoadingScreen

@Mixin(net.minecraft.client.multiplayer.ClientPacketListener::class) 
abstract class MixinClientPacketListener protected constructor(client: net.minecraft.client.Minecraft, connection: net.minecraft.network.Connection, connectionState: net.minecraft.client.multiplayer.CommonListenerCookie) : net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl(client, connection, connectionState) {

    @Inject(method = ["handleSetSpawn"], at = [At("RETURN")]) private fun moveDownloadingTerrainClosing(packet: net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket, ci: CallbackInfo) {
        val screen = this.minecraft.gui.screen()
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().betweenInclusive(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_18_2, com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20_2) && screen is ILevelLoadingScreen) {
            screen.`viaFabricPlus$setReady`()
        }
    }
    
    @com.llamalad7.mixinextras.injector.v2.WrapWithCondition(method = ["handlePlayerChat"], at = [At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;)V", remap = false)]) private fun removeChatPacketError(instance: org.slf4j.Logger, s: kotlin.String, o: kotlin.Any): kotlin.Boolean {
        return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20_2)
    }
    
}
