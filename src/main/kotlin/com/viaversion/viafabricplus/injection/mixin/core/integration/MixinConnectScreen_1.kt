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
import org.spongepowered.asm.mixin.Final
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
import org.spongepowered.asm.mixin.injection.At
import java.net.InetSocketAddress
import com.viaversion.viafabricplus.injection.access.core.IServerData
import com.viaversion.viafabricplus.protocoltranslator.util.ProtocolVersionDetector
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslationImpl
import java.io.IOException
import com.viaversion.viafabricplus.protocoltranslator.impl.provider.vialegacy.ViaFabricPlusClassicMPPassProvider
import io.netty.channel.ChannelFuture
import com.viaversion.viafabricplus.ViaFabricPlusImpl
import com.viaversion.viafabricplus.features.global.ClassiCubeAccount

@Mixin(targets = ["net.minecraft.client.gui.screens.ConnectScreen$1"]) 
abstract class MixinConnectScreen_1 {

    @field:org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField var `val$server`: net.minecraft.client.multiplayer.ServerData = uninitialized()
    @field:Final @field:org.spongepowered.asm.mixin.Shadow 
    @JvmField var `val$hostAndPort`: net.minecraft.client.multiplayer.resolver.ServerAddress = uninitialized()
    @field:org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField var `this$0`: net.minecraft.client.gui.screens.ConnectScreen = uninitialized()
    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$useClassiCubeAccount`: kotlin.Boolean = false
    
    @WrapOperation(method = ["run"], at = [At(value = "INVOKE", target = "Ljava/util/Optional;get()Ljava/lang/Object;", remap = false)]) @Throws(java.lang.Exception::class) private fun setServerInfoAndProtocolVersion(instance: java.util.Optional<InetSocketAddress>, original: com.llamalad7.mixinextras.injector.wrapoperation.Operation<kotlin.Any>): kotlin.Any {
        val address: InetSocketAddress = original.call(instance) as InetSocketAddress
        val mixinServerInfo: IServerData = this.`val$server` as IServerData
        
        var targetVersion: com.viaversion.viaversion.api.protocol.version.ProtocolVersion = com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion()
        if (mixinServerInfo.`viaFabricPlus$forcedVersion`() != null && !mixinServerInfo.`viaFabricPlus$passedDirectConnectScreen`()) {
            targetVersion = mixinServerInfo.`viaFabricPlus$forcedVersion`()
            mixinServerInfo.`viaFabricPlus$passDirectConnectScreen`(false) // reset state
        }
        if (targetVersion === com.viaversion.viafabricplus.api.protocoltranslator.ProtocolTranslation.AUTO_DETECT_VERSION) {
            // If the server got already pinged, try to use that version if it's valid. Otherwise, perform auto-detect
            val serverPinged: kotlin.Boolean = this.`val$server`.state() == net.minecraft.client.multiplayer.ServerData.State.SUCCESSFUL || this.`val$server`.state() == net.minecraft.client.multiplayer.ServerData.State.INCOMPATIBLE
            if (serverPinged) {
                targetVersion = com.viaversion.viaversion.api.protocol.version.ProtocolVersion.getProtocol(this.`val$server`.protocol)
            }
            if (!serverPinged || !targetVersion.isKnown()) {
                this.`this$0`.updateStatus(net.minecraft.network.chat.Component.translatable("base.viafabricplus.detecting_server_version"))
                try {
                    targetVersion = ProtocolVersionDetector.get(this.`val$hostAndPort`, address, ProtocolTranslationImpl.NATIVE_VERSION)
                }catch (ignored: IOException) {
                    // Don't let this one through as not relevant
                }
            }
        }
        com.viaversion.viafabricplus.ViaFabricPlus.api().protocolTranslation().setTargetVersion(targetVersion, true)
        this.`viaFabricPlus$useClassiCubeAccount` = ViaFabricPlusClassicMPPassProvider.classicubeMPPass != null
        
        return address
    }
    
    @WrapOperation(method = ["run"], at = [At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;connect(Ljava/net/InetSocketAddress;Lnet/minecraft/server/network/EventLoopGroupHolder;Lnet/minecraft/network/Connection;)Lio/netty/channel/ChannelFuture;")]) private fun resetProtocolVersionAfterDisconnect(address: InetSocketAddress, eventLoopGroupHolder: net.minecraft.server.network.EventLoopGroupHolder, connection: net.minecraft.network.Connection, original: com.llamalad7.mixinextras.injector.wrapoperation.Operation<ChannelFuture>): ChannelFuture {
        val future: ChannelFuture = original.call(address, eventLoopGroupHolder, connection)
        ViaFabricPlusImpl.impl().protocolTranslation().injectionPreviousVersionHandler(future.channel())
        return future
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["run"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/User;getName()Ljava/lang/String;")]) private fun useClassiCubeUsername(instance: net.minecraft.client.User): kotlin.String {
        if (this.`viaFabricPlus$useClassiCubeAccount` && ClassiCubeAccount.get() != null) {
            return ClassiCubeAccount.get().username()
        }
        return instance.getName()
    }
    
}
