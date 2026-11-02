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
package com.viaversion.viafabricplus.injection.mixin.features.v1_17

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.injection.At
import java.net.InetSocketAddress

@Mixin(targets = ["net.minecraft.client.gui.screens.ConnectScreen$1"]) 
abstract class MixinConnectScreen_1 {

    @Final @org.spongepowered.asm.mixin.Shadow 
    @JvmField var `val$hostAndPort`: net.minecraft.client.multiplayer.resolver.ServerAddress = uninitialized()
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["run"], at = [At(value = "INVOKE", target = "Ljava/net/InetSocketAddress;getHostName()Ljava/lang/String;", remap = false)]) private fun getRealAddress(instance: InetSocketAddress): kotlin.String {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_17)) {
            return `val$hostAndPort`.getHost()
        } else {
            return instance.getHostName()
        }
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["run"], at = [At(value = "INVOKE", target = "Ljava/net/InetSocketAddress;getPort()I", remap = false)]) private fun getRealPort(instance: InetSocketAddress): kotlin.Int {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_17)) {
            return `val$hostAndPort`.getPort()
        } else {
            return instance.getPort()
        }
    }
    
}
