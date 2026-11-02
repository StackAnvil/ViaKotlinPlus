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
package com.viaversion.viafabricplus.injection.mixin.core.gui

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.viaversion.viaversion.api.connection.UserConnection
import net.raphimc.vialegacy.protocol.classic.c0_28_30toa1_0_15.storage.ClassicProgressStorage

@Mixin(net.minecraft.client.gui.screens.LevelLoadingScreen::class) 
abstract class MixinLevelLoadingScreen (title: net.minecraft.network.chat.Component) : net.minecraft.client.gui.screens.Screen(title) {

    @Inject(method = ["extractRenderState"], at = [At("RETURN")]) private fun renderClassicProgress(graphics: net.minecraft.client.gui.GuiGraphicsExtractor, mouseX: kotlin.Int, mouseY: kotlin.Int, a: kotlin.Float, ci: CallbackInfo) {
        val connection: UserConnection? = com.viaversion.viafabricplus.ViaFabricPlus.api().userConnection()
        if (connection != null) {
            val storage: ClassicProgressStorage? = connection.get<ClassicProgressStorage>(ClassicProgressStorage::class.java)
            if (storage != null) {
                graphics.centeredText(minecraft.font, com.viaversion.viafabricplus.util.ChatUtil.prefixText(storage.getStatus()), width / 2, height / 2 - 30, -1)
            }
        }
    }
    
}
