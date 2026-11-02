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

import com.viaversion.viafabricplus.ViaFabricPlusImpl

class ClientPlayerInteractionManager1_18_2 {

    private val unAckedActions: it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap<org.apache.commons.lang3.tuple.Pair<net.minecraft.core.BlockPos, net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action>, org.apache.commons.lang3.tuple.Pair<net.minecraft.world.phys.Vec3, net.minecraft.world.phys.Vec2>> = it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap<org.apache.commons.lang3.tuple.Pair<net.minecraft.core.BlockPos, net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action>, org.apache.commons.lang3.tuple.Pair<net.minecraft.world.phys.Vec3, net.minecraft.world.phys.Vec2>>()
    
    fun trackPlayerAction(action: net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action, blockPos: net.minecraft.core.BlockPos) {
        val player: net.minecraft.client.player.LocalPlayer = net.minecraft.client.Minecraft.getInstance().player
        
        val rotation: net.minecraft.world.phys.Vec2?
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_16_1)) {
            rotation = null
        } else {
            rotation = net.minecraft.world.phys.Vec2(player.getYRot(), player.getXRot())
        }
        unAckedActions.put(org.apache.commons.lang3.tuple.Pair.of<net.minecraft.core.BlockPos, net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action>(blockPos, action), org.apache.commons.lang3.tuple.Pair.of<net.minecraft.world.phys.Vec3, net.minecraft.world.phys.Vec2>(player.position(), rotation))
    }
    
    fun handleBlockBreakAck(blockPos: net.minecraft.core.BlockPos, expectedState: net.minecraft.world.level.block.state.BlockState, action: net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action, allGood: kotlin.Boolean) {
        val player: net.minecraft.client.player.LocalPlayer = net.minecraft.client.Minecraft.getInstance().player
        if (player == null) {
            return 
        }
        
        val world: net.minecraft.client.multiplayer.ClientLevel = net.minecraft.client.Minecraft.getInstance().getConnection().getLevel()
        
        val oldPlayerState: org.apache.commons.lang3.tuple.Pair<net.minecraft.world.phys.Vec3, net.minecraft.world.phys.Vec2> = unAckedActions.remove(org.apache.commons.lang3.tuple.Pair.of<net.minecraft.core.BlockPos, net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action>(blockPos, action))!!
        val actualState: net.minecraft.world.level.block.state.BlockState = world.getBlockState(blockPos)
        
        if ((oldPlayerState == null || !allGood || action != net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK && actualState !== expectedState) && (actualState !== expectedState || com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_15_2))) {
            world.setBlock(blockPos, expectedState, net.minecraft.world.level.block.Block.UPDATE_ALL or net.minecraft.world.level.block.Block.UPDATE_KNOWN_SHAPE)
            if (oldPlayerState != null && (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_16_1) || (world === player.level() && player.isColliding(blockPos, expectedState)))) {
                val oldPlayerPosition: net.minecraft.world.phys.Vec3 = oldPlayerState.key
                if (oldPlayerState.value != null) {
                    player.absSnapTo(oldPlayerPosition.x, oldPlayerPosition.y, oldPlayerPosition.z, oldPlayerState.value.x, oldPlayerState.value.y)
                } else {
                    player.absSnapTo(oldPlayerPosition.x, oldPlayerPosition.y, oldPlayerPosition.z)
                }
            }
        }
        
        while (unAckedActions.size >= 50) {
            ViaFabricPlusImpl.impl().logger().warn("Too many unacked block actions, dropping {}", unAckedActions.firstKey())
            unAckedActions.removeFirst()
        }
    }
    
}
