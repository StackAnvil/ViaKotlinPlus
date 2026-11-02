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
import com.viaversion.viafabricplus.ViaFabricPlusImpl

@Mixin(net.minecraft.client.gui.screens.DirectJoinServerScreen::class) 
abstract class MixinDirectJoinServerScreen (title: net.minecraft.network.chat.Component) : net.minecraft.client.gui.screens.Screen(title) {

    @Inject(method = ["init"], at = [At("RETURN")]) private fun addProtocolSelectionButton(ci: CallbackInfo) {
        val orientation: com.viaversion.viafabricplus.api.settings.impl.Orientation = ViaFabricPlusImpl.impl().options().directConnectScreenButtonOrientation().value()
        if (orientation != com.viaversion.viafabricplus.api.settings.impl.Orientation.NONE) {
            val builder: net.minecraft.client.gui.components.Button.Builder = net.minecraft.client.gui.components.Button.builder(net.minecraft.network.chat.Component.nullToEmpty("ViaFabricPlus"), net.minecraft.client.gui.components.Button.OnPress {`_`: net.minecraft.client.gui.components.Button -> com.viaversion.viafabricplus.ViaFabricPlus.api().screens().openViaFabricPlusScreen(this)}).size(98, net.minecraft.client.gui.components.Button.DEFAULT_HEIGHT)
            orientation.getPositioner().setPosition(this.addRenderableWidget<net.minecraft.client.gui.components.Button>(builder.build()), width, height)
        }
    }
    
}
