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
package com.viaversion.viafabricplus.injection.mixin.features.v26_2.entity

import org.spongepowered.asm.mixin.Mixin
import com.viaversion.viafabricplus.injection.access.v26_2.ILivingEntity
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper
import com.viaversion.viaversion.protocols.v1_21_11to26_1.packet.ServerboundPackets26_1
import com.viaversion.viaversion.protocols.v26_2to26_3.Protocol26_2To26_3

@Mixin(net.minecraft.world.entity.LivingEntity::class) 
abstract class MixinLivingEntity : ILivingEntity {

    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$skipSwingPacket`: kotlin.Boolean = false
    
    @Inject(method = ["swing"], at = [At("HEAD")]) private fun sendSwingPacket(hand: net.minecraft.world.InteractionHand, animation: net.minecraft.world.item.component.SwingAnimation, sendToSwingingEntity: kotlin.Boolean, cir: CallbackInfoReturnable<kotlin.Boolean>) {
        // 26.3 dropped the swing packet, 26.2 sent one for every swing of the local player
        if (!this.`viaFabricPlus$skipSwingPacket` && this as kotlin.Any is net.minecraft.client.player.LocalPlayer && com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v26_2)) {
            val swing: PacketWrapper = PacketWrapper.create(ServerboundPackets26_1.SWING, com.viaversion.viafabricplus.ViaFabricPlus.api().userConnection())
            swing.write<kotlin.Int>(com.viaversion.viaversion.api.type.Types.VAR_INT, hand.ordinal)
            swing.scheduleSendToServer(Protocol26_2To26_3::class.java)
        }
    }
    
    override fun `viaFabricPlus$setSkipSwingPacket`(skipSwingPacket: kotlin.Boolean) {
        this.`viaFabricPlus$skipSwingPacket` = skipSwingPacket
    }
    
}
