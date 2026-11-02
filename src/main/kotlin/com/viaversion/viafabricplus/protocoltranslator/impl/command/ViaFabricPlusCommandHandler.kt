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

import com.viaversion.viaversion.commands.ViaCommandHandler
import com.viaversion.viafabricplus.protocoltranslator.impl.command.classic.ListExtensionsCommand
import com.viaversion.viafabricplus.protocoltranslator.impl.command.classic.SetTimeCommand
import com.viaversion.viafabricplus.protocoltranslator.impl.command.SettingsCommand
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import com.mojang.brigadier.arguments.StringArgumentType
import com.viaversion.viafabricplus.protocoltranslator.impl.command.ViaFabricPlusCommandSender
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.concurrent.CompletableFuture
import com.mojang.brigadier.suggestion.Suggestions

class ViaFabricPlusCommandHandler : ViaCommandHandler(false) {

    init {
        this.removeSubCommand("list")
        this.removeSubCommand("player")
        this.removeSubCommand("pps")
        
        this.registerSubCommand(ListExtensionsCommand())
        this.registerSubCommand(SetTimeCommand())
        this.registerSubCommand(SettingsCommand())
    }
    
    fun execute(ctx: CommandContext<FabricClientCommandSource>): kotlin.Int {
        var args: kotlin.Array<kotlin.String> = kotlin.emptyArray()
        try {
            args = StringArgumentType.getString(ctx, "args").split(" ".toRegex()).dropLastWhile {it.isEmpty()} .toTypedArray()
        }catch (ignored: java.lang.IllegalArgumentException) {
        }
        onCommand(ViaFabricPlusCommandSender(ctx.getSource()), args)
        return 1
    }
    
    fun suggestion(ctx: CommandContext<FabricClientCommandSource>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        lateinit var args: kotlin.Array<kotlin.String>
        try {
            args = StringArgumentType.getString(ctx, "args").split(" ".toRegex()).toTypedArray()
        }catch (ignored: java.lang.IllegalArgumentException) {
            args = kotlin.arrayOf<kotlin.String>("")
        }
        val pref: kotlin.Array<kotlin.String> = args.clone()
        pref[pref.size - 1] = ""
        
        val prefix: kotlin.String = java.lang.String.join(" ", *pref)
        onTabComplete(ViaFabricPlusCommandSender(ctx.getSource()), args).stream().map<SuggestionsBuilder> {
        it: kotlin.String -> val b: SuggestionsBuilder = SuggestionsBuilder(builder.getInput(), prefix.length + builder.getStart())
        b.suggest(it)
        b
        } .forEach {other: SuggestionsBuilder -> builder.add(other)} 
        return builder.buildFuture()
    }
    
}
