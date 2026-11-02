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
package com.viaversion.viafabricplus.protocoltranslator.impl.command.classic

import com.viaversion.viafabricplus.protocoltranslator.impl.command.VFPSubCommand
import net.raphimc.vialegacy.api.LegacyProtocolVersion
import com.viaversion.viaversion.api.command.ViaCommandSender
import net.raphimc.vialegacy.protocol.alpha.a1_0_16_2toa1_0_17_1_0_17_4.storage.TimeLockStorage

class SetTimeCommand : VFPSubCommand {

    override fun name(): kotlin.String {
        return "settime"
    }
    
    override fun description(): kotlin.String {
        return "Changes the time (Only for <= " + LegacyProtocolVersion.a1_0_16toa1_0_16_2.getName() + ")"
    }
    
    override fun usage(): kotlin.String {
        return name() + " " + "<Time (Long)>"
    }
    
    override fun execute(sender: ViaCommandSender, args: kotlin.Array<kotlin.String>): kotlin.Boolean {
        if (getUser() == null || !getUser().has(TimeLockStorage::class.java)) {
            sendMessage(sender, net.minecraft.ChatFormatting.RED.toString() + "Only for <= " + LegacyProtocolVersion.a1_0_16toa1_0_16_2.getName())
            return true
        }
        try {
            if (args.size == 1) {
                val time: kotlin.Long = args[0].toLong() % 24000L
                getUser().get<TimeLockStorage>(TimeLockStorage::class.java)!!.setTime(time)
                sendMessage(sender, net.minecraft.ChatFormatting.GREEN.toString() + "Time has been set to " + net.minecraft.ChatFormatting.GOLD + time)
            } else {
                return false
            }
        }catch (ignored: Throwable) {
            return false
        }
        return true
    }
    
}
