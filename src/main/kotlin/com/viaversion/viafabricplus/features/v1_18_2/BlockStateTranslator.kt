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
package com.viaversion.viafabricplus.features.v1_18_2

import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslationImpl
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper
import com.viaversion.viaversion.protocols.v1_17_1to1_18.packet.ClientboundPackets1_18
import com.viaversion.viafabricplus.features.v1_18_2.BlockStateTranslator
import com.viaversion.viaversion.api.minecraft.BlockPosition
import com.viaversion.viafabricplus.ViaFabricPlusImpl

object BlockStateTranslator {
    private val DUMMY_USER_CONNECTION: UserConnection = com.viaversion.viafabricplus.ViaFabricPlus.api().conversions().createDummyUserConnection(ProtocolTranslationImpl.NATIVE_VERSION, com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_18_2)
    
     /**
 * Converts a 1.18.2-block state to a native block state (the current version)
 * 
 * @param blockStateId The block state id
 * @return The native block state
 */
    fun via1_18_2toMc(blockStateId: kotlin.Int): net.minecraft.world.level.block.state.BlockState {
        try {
            val levelEvent: PacketWrapper = PacketWrapper.create(ClientboundPackets1_18.LEVEL_EVENT, BlockStateTranslator.DUMMY_USER_CONNECTION)
            levelEvent.write<kotlin.Int>(com.viaversion.viaversion.api.type.Types.INT, 2001) // eventId
            levelEvent.write<BlockPosition>(com.viaversion.viaversion.api.type.Types.BLOCK_POSITION1_14, BlockPosition(0, 0, 0)) // position
            levelEvent.write<kotlin.Int>(com.viaversion.viaversion.api.type.Types.INT, blockStateId) // data
            levelEvent.write<kotlin.Boolean>(com.viaversion.viaversion.api.type.Types.BOOLEAN, false) // global
            
            levelEvent.resetReader()
            levelEvent.user().getProtocolInfo().getPipeline().transform(com.viaversion.viaversion.api.protocol.packet.Direction.CLIENTBOUND, com.viaversion.viaversion.api.protocol.packet.State.PLAY, levelEvent)
            
            levelEvent.read<kotlin.Int>(com.viaversion.viaversion.api.type.Types.INT) // eventId
            levelEvent.read<BlockPosition>(com.viaversion.viaversion.api.type.Types.BLOCK_POSITION1_14) // position
            return net.minecraft.world.level.block.Block.stateById(levelEvent.read<kotlin.Int>(com.viaversion.viaversion.api.type.Types.INT)) // data
        }catch (t: Throwable) {
            ViaFabricPlusImpl.impl().logger().error("Error converting ViaVersion 1.18.2 block state to native block state", t)
            return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState()
        }
    }
    
}
