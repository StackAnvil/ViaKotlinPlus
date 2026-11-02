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
package com.viaversion.viafabricplus.injection.mixin.features.v1_21.network

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import java.util.OptionalInt

@Mixin(net.minecraft.client.multiplayer.ClientPacketListener::class) 
abstract class MixinClientPacketListener protected constructor(minecraft: net.minecraft.client.Minecraft, connection: net.minecraft.network.Connection, cookie: net.minecraft.client.multiplayer.CommonListenerCookie) : net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl(minecraft, connection, cookie) {

    @org.spongepowered.asm.mixin.Shadow 
    @JvmField val level: net.minecraft.client.multiplayer.ClientLevel = uninitialized()
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["handleTeleportEntity"], at = [At(value = "INVOKE", target = "Ljava/util/OptionalInt;isPresent()Z")]) private fun dontHandleRemovedVehiclePositionChange(instance: OptionalInt): kotlin.Boolean {
        return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21) && instance.isPresent()
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["handleOpenSignEditor"], at = [At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V", remap = false)]) private fun openEmptySignEditor(instance: org.slf4j.Logger, format: kotlin.String, arg1: kotlin.Any, arg2: kotlin.Any, @com.llamalad7.mixinextras.sugar.Local(argsOnly = true) packet: net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21)) {
            val pos: net.minecraft.core.BlockPos = packet.pos()
            
            val emptySignBlockEntity: net.minecraft.world.level.block.entity.SignBlockEntity = net.minecraft.world.level.block.entity.SignBlockEntity(pos, this.level.getBlockState(pos))
            emptySignBlockEntity.setLevel(this.level)
            this.minecraft.player.openTextEdit(emptySignBlockEntity, packet.slot())
        } else {
            instance.warn(format, arg1, arg2)
        }
    }
    
    @com.llamalad7.mixinextras.injector.v2.WrapWithCondition(method = ["handleRespawn"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeMap;assignBaseValues(Lnet/minecraft/world/entity/ai/attributes/AttributeMap;)V")]) private fun dontApplyBaseValues(instance: net.minecraft.world.entity.ai.attributes.AttributeMap, other: net.minecraft.world.entity.ai.attributes.AttributeMap): kotlin.Boolean {
        return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21)
    }
    
}
