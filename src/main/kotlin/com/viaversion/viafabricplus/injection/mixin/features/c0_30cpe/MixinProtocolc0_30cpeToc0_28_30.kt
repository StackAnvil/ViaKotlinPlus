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
package com.viaversion.viafabricplus.injection.mixin.features.c0_30cpe

import org.spongepowered.asm.mixin.Mixin
import net.raphimc.vialegacy.protocol.classic.c0_30cpetoc0_28_30.Protocolc0_30cpeToc0_28_30
import com.viaversion.viaversion.api.protocol.AbstractProtocol
import net.raphimc.vialegacy.protocol.classic.c0_30cpetoc0_28_30.packet.ClientboundPacketsc0_30cpe
import net.raphimc.vialegacy.protocol.classic.c0_28_30toa1_0_15.packet.ClientboundPacketsc0_28
import net.raphimc.vialegacy.protocol.classic.c0_30cpetoc0_28_30.packet.ServerboundPacketsc0_30cpe
import net.raphimc.vialegacy.protocol.classic.c0_28_30toa1_0_15.packet.ServerboundPacketsc0_28
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.viaversion.viafabricplus.features.c0_30cpe.CPEAdditions
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper
import com.viaversion.viaversion.protocols.v1_19_3to1_19_4.packet.ClientboundPackets1_19_4
import com.viaversion.viaversion.protocols.v1_19_3to1_19_4.Protocol1_19_3To1_19_4

@Mixin(value = [Protocolc0_30cpeToc0_28_30::class], remap = false) 
abstract class MixinProtocolc0_30cpeToc0_28_30 : AbstractProtocol<ClientboundPacketsc0_30cpe, ClientboundPacketsc0_28, ServerboundPacketsc0_30cpe, ServerboundPacketsc0_28>() {

    @Inject(method = ["registerPackets"], at = [At("RETURN")]) private fun extendPackets(ci: CallbackInfo) {
        this.registerClientbound(CPEAdditions.EXT_WEATHER_TYPE, null, object : PacketHandlers() {
            public override fun register() {
                handler(PacketHandler {
                wrapper: PacketWrapper -> wrapper.cancel()
                val weatherType: kotlin.Byte = wrapper.read<kotlin.Byte>(com.viaversion.viaversion.api.type.Types.BYTE)
                
                val changeRainState: PacketWrapper = PacketWrapper.create(ClientboundPackets1_19_4.GAME_EVENT, wrapper.user())
                changeRainState.write<kotlin.Short>(com.viaversion.viaversion.api.type.Types.UNSIGNED_BYTE, if (weatherType.toInt() == 0 /* sunny */) 1.toShort() else 2.toShort()) // start raining
                changeRainState.write<kotlin.Float>(com.viaversion.viaversion.api.type.Types.FLOAT, 0f) // unused
                changeRainState.send(Protocol1_19_3To1_19_4::class.java)
                
                if (weatherType.toInt() == 1 /* raining */ || weatherType.toInt() == 2 /* snowing */) {
                    val changeRainType: PacketWrapper = PacketWrapper.create(ClientboundPackets1_19_4.GAME_EVENT, wrapper.user())
                    changeRainType.write<kotlin.Short>(com.viaversion.viaversion.api.type.Types.UNSIGNED_BYTE, 7.toShort()) // set rain gradient
                    changeRainType.write<kotlin.Float>(com.viaversion.viaversion.api.type.Types.FLOAT, 1f)
                    changeRainType.send(Protocol1_19_3To1_19_4::class.java)
                }
                CPEAdditions.setSnowing(weatherType.toInt() == 2)
                
                })
            }
        })
    }
    
    @Inject(method = ["init"], at = [At("HEAD")]) private fun resetSnowing(ci: CallbackInfo) {
        CPEAdditions.setSnowing(false)
    }
    
}
