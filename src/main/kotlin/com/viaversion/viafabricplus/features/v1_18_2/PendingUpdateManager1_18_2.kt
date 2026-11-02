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

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.multiplayer.prediction.BlockStatePredictionHandler
import net.minecraft.client.player.LocalPlayer
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

class PendingUpdateManager1_18_2 : BlockStatePredictionHandler() {
 override fun retainKnownServerState(pos: BlockPos, state: BlockState, player: LocalPlayer) {
    }
    
    override fun updateKnownServerState(pos: BlockPos, state: BlockState): Boolean {
        return false
    }
    
    override fun endPredictionsUpTo(maxProcessableSequence: Int, world: ClientLevel) {
    }
    
    override fun startPredicting(): BlockStatePredictionHandler {
        return this
    }
    
    override fun close() {
    }
    
    override fun currentSequence(): Int {
        return 0
    }
    
    override fun isPredicting(): Boolean {
        return false
    }
 }
