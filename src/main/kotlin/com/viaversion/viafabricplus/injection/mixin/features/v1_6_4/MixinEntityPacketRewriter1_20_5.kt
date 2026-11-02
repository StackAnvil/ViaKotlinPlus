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
package com.viaversion.viafabricplus.injection.mixin.features.v1_6_4

import org.spongepowered.asm.mixin.Mixin
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper
import java.util.UUID
import org.spongepowered.asm.mixin.injection.At
import net.raphimc.vialegacy.api.LegacyProtocolVersion

@Mixin(value = [com.viaversion.viaversion.protocols.v1_20_3to1_20_5.rewriter.EntityPacketRewriter1_20_5::class], remap = false) 
abstract class MixinEntityPacketRewriter1_20_5 {

    @org.spongepowered.asm.mixin.Shadow protected abstract fun writeAttribute(wrapper: PacketWrapper, attributeId: kotlin.String, base: kotlin.Double, modifierId: UUID, amount: kotlin.Double)
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["sendRangeAttributes"], at = [At(value = "INVOKE", target = "Lcom/viaversion/viaversion/protocols/v1_20_3to1_20_5/rewriter/EntityPacketRewriter1_20_5;writeAttribute(Lcom/viaversion/viaversion/api/protocol/packet/PacketWrapper;Ljava/lang/String;DLjava/util/UUID;D)V")]) private fun useLegacyValues(instance: com.viaversion.viaversion.protocols.v1_20_3to1_20_5.rewriter.EntityPacketRewriter1_20_5, wrapper: PacketWrapper, attributeId: kotlin.String, base: kotlin.Double, modifierId: UUID, amount: kotlin.Double) {
        if (attributeId == "player.block_interaction_range" && wrapper.user().getProtocolInfo().serverProtocolVersion().olderThan(LegacyProtocolVersion.r1_0_0tor1_0_1)) {
            this.writeAttribute(wrapper, attributeId, 4.0, modifierId, 1.0)
        } else if (attributeId == "player.entity_interaction_range" && wrapper.user().getProtocolInfo().serverProtocolVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
            this.writeAttribute(wrapper, attributeId, 3.0, modifierId, 3.0)
        } else {
            this.writeAttribute(wrapper, attributeId, base, modifierId, amount)
        }
    }
    
}
