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

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.viaversion.viafabricplus.ViaFabricPlusImpl
import com.viaversion.viafabricplus.injection.access.core.IServerData
import com.viaversion.viafabricplus.screen.impl.PerServerVersionScreen

@Mixin(net.minecraft.client.gui.screens.ManageServerScreen::class) 
abstract class MixinManageServerScreen (title: net.minecraft.network.chat.Component) : net.minecraft.client.gui.screens.Screen(title) {

    @org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField val serverData: net.minecraft.client.multiplayer.ServerData = uninitialized()
    @org.spongepowered.asm.mixin.Shadow 
    @JvmField val nameEdit: net.minecraft.client.gui.components.EditBox = uninitialized()
    @org.spongepowered.asm.mixin.Shadow 
    @JvmField val ipEdit: net.minecraft.client.gui.components.EditBox = uninitialized()
    @org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$nameField`: kotlin.String = uninitialized()
    @org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$addressField`: kotlin.String = uninitialized()
    @Inject(method = ["init"], at = [At("RETURN")]) private fun addVersionSetterButton(ci: CallbackInfo) {
        val orientation: com.viaversion.viafabricplus.api.settings.impl.Orientation = ViaFabricPlusImpl.impl().options().addServerScreenButtonOrientation().value()
        if (orientation != com.viaversion.viafabricplus.api.settings.impl.Orientation.NONE) {
            val mixinServerInfo: IServerData = serverData as IServerData
            val forcedVersion: com.viaversion.viaversion.api.protocol.version.ProtocolVersion = mixinServerInfo.`viaFabricPlus$forcedVersion`()
            
                        // Restore input if the user cancels the version selection screen (or if the user is editing an existing server)
            if (`viaFabricPlus$nameField` != null && `viaFabricPlus$addressField` != null) {
                this.nameEdit.setValue(`viaFabricPlus$nameField`)
                this.ipEdit.setValue(`viaFabricPlus$addressField`)
                
                `viaFabricPlus$nameField` = uninitialized()
                `viaFabricPlus$addressField` = uninitialized()
            }
            
            val buttonBuilder: net.minecraft.client.gui.components.Button.Builder = net.minecraft.client.gui.components.Button.builder(if (forcedVersion == null) net.minecraft.network.chat.Component.translatable("base.viafabricplus.set_version") else net.minecraft.network.chat.Component.nullToEmpty(forcedVersion.getName()), net.minecraft.client.gui.components.Button.OnPress {
            `_`: net.minecraft.client.gui.components.Button -> 
                // Store current input in case the user cancels the version selection
            `viaFabricPlus$nameField` = nameEdit.getValue()
            `viaFabricPlus$addressField` = ipEdit.getValue()
            
            minecraft.gui.setScreen(PerServerVersionScreen(this, java.util.function.Consumer {version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion -> mixinServerInfo.`viaFabricPlus$forceVersion`(version)}, java.util.function.Supplier {mixinServerInfo.`viaFabricPlus$forcedVersion`()}))
            
            }).size(98, net.minecraft.client.gui.components.Button.DEFAULT_HEIGHT)
            orientation.getPositioner().setPosition(this.addRenderableWidget<net.minecraft.client.gui.components.Button>(buttonBuilder.build()), width, height)
        }
    }
    
}
