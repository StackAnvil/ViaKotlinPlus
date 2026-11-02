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
package com.viaversion.viafabricplus.injection.mixin.core.integration

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
import org.spongepowered.asm.mixin.injection.At
import com.viaversion.viafabricplus.injection.access.core.IServerData
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen::class) 
abstract class MixinJoinMultiplayerScreen (component: net.minecraft.network.chat.Component) : net.minecraft.client.gui.screens.Screen(component) {

    @org.spongepowered.asm.mixin.Shadow 
    @JvmField val editingServer: net.minecraft.client.multiplayer.ServerData = uninitialized()
    @WrapOperation(method = ["directJoinCallback"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/multiplayer/JoinMultiplayerScreen;join(Lnet/minecraft/client/multiplayer/ServerData;)V")]) private fun storeDirectConnectionPhase(instance: net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen, data: net.minecraft.client.multiplayer.ServerData, original: com.llamalad7.mixinextras.injector.wrapoperation.Operation<java.lang.Void>) {
        (data as IServerData).`viaFabricPlus$passDirectConnectScreen`(true)
        original.call(instance, data)
    }
    
    @Inject(method = ["editServerCallback"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ServerList;save()V")]) private fun storeForcedVersion(result: kotlin.Boolean, ci: CallbackInfo, @com.llamalad7.mixinextras.sugar.Local current: net.minecraft.client.multiplayer.ServerData) {
        (current as IServerData).`viaFabricPlus$forceVersion`((this.editingServer as IServerData).`viaFabricPlus$forcedVersion`())
    }
    
}
