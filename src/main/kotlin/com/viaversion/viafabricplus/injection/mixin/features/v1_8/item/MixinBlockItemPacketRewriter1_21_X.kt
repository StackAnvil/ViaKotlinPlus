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
package com.viaversion.viafabricplus.injection.mixin.features.v1_8.item

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import net.raphimc.vialegacy.api.LegacyProtocolVersion

@Mixin(value = [com.viaversion.viaversion.protocols.v1_21_2to1_21_4.rewriter.BlockItemPacketRewriter1_21_4::class, com.viaversion.viaversion.protocols.v1_21_4to1_21_5.rewriter.BlockItemPacketRewriter1_21_5::class], remap = false) 
abstract class MixinBlockItemPacketRewriter1_21_X {

    @org.spongepowered.asm.mixin.injection.Redirect(method = ["appendItemDataFixComponents"], at = [At(value = "INVOKE", target = "Lcom/viaversion/viaversion/api/protocol/version/ProtocolVersion;olderThanOrEqualTo(Lcom/viaversion/viaversion/api/protocol/version/ProtocolVersion;)Z")]) private fun changeSwordFixVersionRange(instance: com.viaversion.viaversion.api.protocol.version.ProtocolVersion, other: com.viaversion.viaversion.api.protocol.version.ProtocolVersion): kotlin.Boolean {
        if (other === com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8) {
            return instance.betweenInclusive(LegacyProtocolVersion.b1_8tob1_8_1, com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8)
        } else {
            return instance.olderThanOrEqualTo(other)
        }
    }
    
    
    
    
}
