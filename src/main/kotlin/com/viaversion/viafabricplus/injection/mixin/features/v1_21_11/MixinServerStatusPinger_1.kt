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
package com.viaversion.viafabricplus.injection.mixin.features.v1_21_11

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.injection.At
import com.viaversion.viafabricplus.ViaFabricPlusImpl
import com.viaversion.viafabricplus.injection.access.core.IServerData

@Mixin(targets = ["net.minecraft.client.multiplayer.ServerStatusPinger$1"]) 
abstract class MixinServerStatusPinger_1 {

    @Final @org.spongepowered.asm.mixin.Shadow 
    @JvmField var `val$data`: net.minecraft.client.multiplayer.ServerData = uninitialized()
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["handleStatusResponse"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ServerStatusPinger$1;sanitizeDescription(Lnet/minecraft/network/chat/Component;)Lnet/minecraft/network/chat/Component;")]) private fun removeSanitizeDescription(component: net.minecraft.network.chat.Component): net.minecraft.network.chat.Component {
        if (ViaFabricPlusImpl.impl().advanced().removeServerDescriptionSanitize().isActive((`val$data` as IServerData).`viaFabricPlus$translatingVersion`())) {
            return component
        } else {
            return com.viaversion.viafabricplus.injection.mixin.features.v1_21_11.MixinServerStatusPinger_1.Companion.sanitizeDescription(component)
        }
    }
    
    private companion object {
        @JvmStatic @org.spongepowered.asm.mixin.Shadow fun sanitizeDescription(original: net.minecraft.network.chat.Component): net.minecraft.network.chat.Component {
            return uninitialized()
        }
        
    }}
