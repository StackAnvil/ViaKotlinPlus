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
package com.viaversion.viafabricplus.injection.mixin.features.v1_18

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.viaversion.viafabricplus.injection.access.v1_20_2.ILevelLoadingScreen
import org.spongepowered.asm.mixin.injection.ModifyConstant

@Mixin(net.minecraft.client.multiplayer.ClientPacketListener::class) 
abstract class MixinClientPacketListener protected constructor(client: net.minecraft.client.Minecraft, connection: net.minecraft.network.Connection, connectionState: net.minecraft.client.multiplayer.CommonListenerCookie) : net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl(client, connection, connectionState) {

    @Inject(method = ["handleMovePlayer"], at = [At("RETURN")]) private fun closeDownloadingTerrain(packet: net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket, ci: CallbackInfo) {
        val screen = this.minecraft.gui.screen()
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_18) && screen is ILevelLoadingScreen) {
            screen.`viaFabricPlus$setReady`()
        }
    }
    
    @ModifyConstant(method = ["handleSetEntityPassengersPacket"], constant = [org.spongepowered.asm.mixin.injection.Constant(classValue = net.minecraft.world.entity.vehicle.boat.AbstractBoat::class)]) private fun dontChangeYawWhenMountingBoats(entity: kotlin.Any, boatClass: java.lang.Class<*>): kotlin.Boolean {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_18)) {
            return false
        } else {
            return boatClass.isInstance(entity)
        }
    }
    
}
