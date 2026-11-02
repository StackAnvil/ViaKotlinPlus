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

import org.spongepowered.asm.mixin.Mixin
import com.viaversion.viaversion.protocols.v1_21_4to1_21_5.Protocol1_21_4To1_21_5
import com.viaversion.viaversion.rewriter.StructuredItemRewriter
import com.viaversion.viaversion.protocols.v1_21to1_21_2.packet.ClientboundPacket1_21_2
import com.viaversion.viaversion.protocols.v1_21_4to1_21_5.packet.ServerboundPacket1_21_5
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.viaversion.viaversion.protocols.v1_21_4to1_21_5.packet.ServerboundPackets1_21_5
import com.viaversion.viaversion.protocols.v1_21_2to1_21_4.packet.ServerboundPackets1_21_4
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper
import com.viaversion.viafabricplus.protocoltranslator.util.NotificationUtil

@Mixin(value = [com.viaversion.viaversion.protocols.v1_21_4to1_21_5.rewriter.BlockItemPacketRewriter1_21_5::class], remap = false) 
abstract class MixinBlockItemPacketRewriter1_21_5 (protocol: Protocol1_21_4To1_21_5) : StructuredItemRewriter<ClientboundPacket1_21_2, ServerboundPacket1_21_5, Protocol1_21_4To1_21_5>(protocol) {

    @Inject(method = ["registerPackets"], at = [At("RETURN")]) private fun removeContainerClickHandler(ci: CallbackInfo) {
        // Don't allow mods to directly send window interactions which would skip our clientside fix in MixinClientPlayerInteractionManager
        // Use ClientPlayerInteractionManager#clickSlot instead
        protocol.registerServerbound(ServerboundPackets1_21_5.CONTAINER_CLICK, ServerboundPackets1_21_4.CONTAINER_CLICK, PacketHandler {
        wrapper: PacketWrapper -> NotificationUtil.warnIncompatibilityPacket("1.21.5", "CONTAINER_CLICK", "MultiPlayerGameMode#handleInventoryMouseClick")
        wrapper.cancel()
        
        }, true)
    }
    
}
