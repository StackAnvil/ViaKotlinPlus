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
package com.viaversion.viafabricplus.injection.mixin.features.v1_16_4

import org.spongepowered.asm.mixin.Mixin
import com.viaversion.viaversion.protocols.v1_16_4to1_17.rewriter.ItemPacketRewriter1_17
import com.viaversion.viaversion.protocols.v1_16_4to1_17.Protocol1_16_4To1_17
import com.viaversion.viaversion.protocols.v1_16_1to1_16_2.packet.ClientboundPackets1_16_2
import com.viaversion.viaversion.protocols.v1_16_4to1_17.packet.ServerboundPackets1_17
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.viaversion.viaversion.protocols.v1_16_1to1_16_2.packet.ServerboundPackets1_16_2
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper
import com.viaversion.viafabricplus.protocoltranslator.util.NotificationUtil

@Mixin(value = [ItemPacketRewriter1_17::class], remap = false) 
abstract class MixinItemPacketRewriter1_17 (protocol: Protocol1_16_4To1_17, itemType: com.viaversion.viaversion.api.type.Type<com.viaversion.viaversion.api.minecraft.item.Item>, itemArrayType: com.viaversion.viaversion.api.type.Type<kotlin.Array<com.viaversion.viaversion.api.minecraft.item.Item>>) : com.viaversion.viaversion.rewriter.ItemRewriter<ClientboundPackets1_16_2, ServerboundPackets1_17, Protocol1_16_4To1_17>(protocol, itemType, itemArrayType) {

    @Inject(method = ["registerPackets"], at = [At("RETURN")]) private fun removeContainerClickHandler(ci: CallbackInfo) {
        // Don't allow mods to directly send window interactions which would skip our clientside fix in MixinClientPlayerInteractionManager
        // Use ClientPlayerInteractionManager#clickSlot instead
        this.protocol.registerServerbound(ServerboundPackets1_17.CONTAINER_CLICK, ServerboundPackets1_16_2.CONTAINER_CLICK, PacketHandler {
        wrapper: PacketWrapper -> NotificationUtil.warnIncompatibilityPacket("1.17", "CONTAINER_CLICK", "MultiPlayerGameMode#handleInventoryMouseClick")
        wrapper.cancel()
        
        }, true)
    }
    
}
