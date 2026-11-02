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
package com.viaversion.viafabricplus.injection.mixin.features.v1_13_2.screen

import org.spongepowered.asm.mixin.Mixin
import com.viaversion.viafabricplus.features.v1_13_2.MouseSensitivity1_13_2

@Mixin(net.minecraft.client.gui.screens.options.MouseSettingsScreen::class) 
abstract class MixinMouseSettingsScreen (parent: net.minecraft.client.gui.screens.Screen, gameOptions: net.minecraft.client.Options, title: net.minecraft.network.chat.Component) : net.minecraft.client.gui.screens.options.OptionsSubScreen(parent, gameOptions, title) {

    override fun extractRenderState(graphics: net.minecraft.client.gui.GuiGraphicsExtractor, mouseX: kotlin.Int, mouseY: kotlin.Int, a: kotlin.Float) {
        super.extractRenderState(graphics, mouseX, mouseY, a)
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_13_2) && this.list.findOption(this.options.sensitivity()).isHovered()) {
            graphics.setTooltipForNextFrame(font, net.minecraft.network.chat.Component.nullToEmpty("<=1.13.2 Sensitivity: " + MouseSensitivity1_13_2.get1_13SliderValue(this.options.sensitivity().get().toFloat()).valueInt() + "%"), mouseX, mouseY)
        }
    }
    
}
