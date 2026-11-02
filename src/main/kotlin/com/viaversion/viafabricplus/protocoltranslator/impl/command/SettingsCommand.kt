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
package com.viaversion.viafabricplus.protocoltranslator.impl.command

import com.viaversion.viafabricplus.protocoltranslator.impl.command.VFPSubCommand
import com.viaversion.viaversion.api.command.ViaCommandSender
import com.viaversion.viafabricplus.ViaFabricPlusImpl

class SettingsCommand : VFPSubCommand {

    override fun name(): kotlin.String {
        return "settings"
    }
    
    override fun description(): kotlin.String {
        return "Opens ViaFabricPlus's configuration/options screen"
    }
    
    override fun execute(sender: ViaCommandSender, args: kotlin.Array<kotlin.String>): kotlin.Boolean {
        net.minecraft.client.Minecraft.getInstance().execute(java.lang.Runnable {ViaFabricPlusImpl.impl().screens().settingsScreen().open(null)})
        return true
    }
    
}
