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
package com.viaversion.viafabricplus.injection.mixin.core.connection

import org.spongepowered.asm.mixin.Mixin
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
import org.spongepowered.asm.mixin.injection.At
import java.net.InetSocketAddress
import com.viaversion.viafabricplus.injection.access.core.IServerData
import com.viaversion.viafabricplus.injection.access.core.ILocalSampleLogger

@Mixin(net.minecraft.client.multiplayer.ServerStatusPinger::class) 
abstract class MixinServerStatusPinger {

    @WrapOperation(method = ["pingServer"], at = [At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;connectToServer(Ljava/net/InetSocketAddress;Lnet/minecraft/server/network/EventLoopGroupHolder;Lnet/minecraft/util/debugchart/LocalSampleLogger;)Lnet/minecraft/network/Connection;")]) private fun setForcedVersion(address: InetSocketAddress, eventLoopGroupHolder: net.minecraft.server.network.EventLoopGroupHolder, localSampleLogger: net.minecraft.util.debugchart.LocalSampleLogger, original: com.llamalad7.mixinextras.injector.wrapoperation.Operation<net.minecraft.network.Connection>, @com.llamalad7.mixinextras.sugar.Local(argsOnly = true) data: net.minecraft.client.multiplayer.ServerData): net.minecraft.network.Connection {
        var localSampleLogger: net.minecraft.util.debugchart.LocalSampleLogger = localSampleLogger
        val mixinServerInfo: IServerData = data as IServerData
        
        if (mixinServerInfo.`viaFabricPlus$forcedVersion`() != null && !mixinServerInfo.`viaFabricPlus$passedDirectConnectScreen`()) {
            // We use the PerformanceLog field to store the forced version since it's always null when pinging a server
            // So we can create a dummy instance, store the forced version in it and later destroy the instance again
            // To avoid any side effects, we also support cases where a mod is also creating a PerformanceLog instance
            if (localSampleLogger == null) {
                localSampleLogger = net.minecraft.util.debugchart.LocalSampleLogger(1)
            }
            
                        // Attach the forced version to the PerformanceLog instance
            (localSampleLogger as ILocalSampleLogger).`viaFabricPlus$setForcedVersion`(mixinServerInfo.`viaFabricPlus$forcedVersion`())
            mixinServerInfo.`viaFabricPlus$passDirectConnectScreen`(false)
        }
        
        return original.call(address, eventLoopGroupHolder, localSampleLogger)
    }
    
}
