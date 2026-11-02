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
package com.viaversion.viafabricplus.features.v1_21_5

import net.raphimc.vialegacy.api.LegacyProtocolVersion

object SignedCommands1_21_6 {
    fun sendGameMode(gameMode: net.minecraft.world.level.GameType) {
        val command: kotlin.String
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_2_4tor1_2_5)) {
            val username: kotlin.String = net.minecraft.client.Minecraft.getInstance().getUser().getName()
            command = "gamemode " + username + " " + (if (gameMode.getId() > 1) 0 else gameMode.getName())
        } else {
            command = "gamemode " + gameMode.getName()
        }
        
        net.minecraft.client.Minecraft.getInstance().getConnection().sendCommand(command)
    }
    
}
