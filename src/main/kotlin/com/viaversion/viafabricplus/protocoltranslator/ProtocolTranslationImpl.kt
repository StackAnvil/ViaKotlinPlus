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
package com.viaversion.viafabricplus.protocoltranslator

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslationImpl
import com.viaversion.viafabricplus.injection.access.core.IConnection
import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viafabricplus.injection.access.core.IServerData
import io.netty.util.concurrent.GenericFutureListener
import java.util.concurrent.CompletableFuture
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import com.viaversion.viafabricplus.protocoltranslator.impl.command.ViaFabricPlusCommandHandler
import com.viaversion.viaversion.api.Via
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.viaversion.viaversion.ViaManagerImpl
import com.viaversion.viafabricplus.protocoltranslator.impl.platform.ViaFabricPlusViaVersionPlatform
import com.viaversion.viaversion.platform.NoopInjector
import com.viaversion.viafabricplus.protocoltranslator.impl.viaversion.ViaFabricPlusPlatformLoader
import com.viaversion.viabackwards.ViaBackwardsPlatformImpl
import com.viaversion.viafabricplus.protocoltranslator.impl.platform.ViaFabricPlusViaLegacyPlatform
import com.viaversion.viaaprilfools.ViaAprilFoolsPlatformImpl
import com.viaversion.viafabricplus.protocoltranslator.protocol.ViaFabricPlusProtocol
import io.netty.util.AttributeKey

class ProtocolTranslationImpl : com.viaversion.viafabricplus.api.protocoltranslator.ProtocolTranslation {

    private val changeVersionListeners: kotlin.collections.MutableList<java.util.function.BiConsumer<com.viaversion.viaversion.api.protocol.version.ProtocolVersion, com.viaversion.viaversion.api.protocol.version.ProtocolVersion>> = java.util.ArrayList<java.util.function.BiConsumer<com.viaversion.viaversion.api.protocol.version.ProtocolVersion, com.viaversion.viaversion.api.protocol.version.ProtocolVersion>>()
    
    private var targetVersion: com.viaversion.viaversion.api.protocol.version.ProtocolVersion = ProtocolTranslationImpl.Companion.NATIVE_VERSION
    private var previousVersion: com.viaversion.viaversion.api.protocol.version.ProtocolVersion? = null
    override fun targetVersion(): com.viaversion.viaversion.api.protocol.version.ProtocolVersion {
        return targetVersion
    }
    
    override fun targetVersion(connection: net.minecraft.network.Connection): com.viaversion.viaversion.api.protocol.version.ProtocolVersion {
        return (connection as IConnection).`viaFabricPlus$getTargetVersion`()
    }
    
    override fun targetVersion(channel: io.netty.channel.Channel): com.viaversion.viaversion.api.protocol.version.ProtocolVersion {
        return channel.attr<com.viaversion.viaversion.api.protocol.version.ProtocolVersion>(ProtocolTranslationImpl.Companion.TARGET_VERSION_ATTRIBUTE_KEY).get()
    }
    
    override fun setTargetVersion(targetVersion: com.viaversion.viaversion.api.protocol.version.ProtocolVersion, revertOnDisconnect: kotlin.Boolean) {
        val oldVersion: com.viaversion.viaversion.api.protocol.version.ProtocolVersion = this.targetVersion
        this.targetVersion = targetVersion
        if (oldVersion !== targetVersion) {
            if (revertOnDisconnect) {
                this.previousVersion = oldVersion
            }
            for (listener in this.changeVersionListeners) {
                listener.accept(oldVersion, this.targetVersion)
            }
        }
    }
    
    override fun addChangeProtocolVersionListener(listener: java.util.function.BiConsumer<com.viaversion.viaversion.api.protocol.version.ProtocolVersion, com.viaversion.viaversion.api.protocol.version.ProtocolVersion>) {
        this.changeVersionListeners.add(listener)
    }
    
    override fun userConnection(): UserConnection? {
        val handler: net.minecraft.client.multiplayer.ClientPacketListener = net.minecraft.client.Minecraft.getInstance().getConnection()
        if (handler != null) {
            return (handler.getConnection() as IConnection).`viaFabricPlus$getUserConnection`()
        } else {
            return null
        }
    }
    
    override fun userConnection(connection: net.minecraft.network.Connection): UserConnection {
        return (connection as IConnection).`viaFabricPlus$getUserConnection`()
    }
    
    override fun serverVersion(serverData: net.minecraft.client.multiplayer.ServerData): com.viaversion.viaversion.api.protocol.version.ProtocolVersion {
        return (serverData as IServerData).`viaFabricPlus$forcedVersion`()
    }
    
    override fun setServerVersion(serverData: net.minecraft.client.multiplayer.ServerData, version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion) {
        (serverData as IServerData).`viaFabricPlus$forceVersion`(version)
    }
    
    fun injectionPreviousVersionHandler(channel: io.netty.channel.Channel) {
        if (this.previousVersion != null) {
            channel.closeFuture().addListener(GenericFutureListener {
            `_`: io.netty.util.concurrent.Future<in java.lang.Void> -> setTargetVersion(this.previousVersion!!)
            this.previousVersion = null
            
            })
        }
    }
    
    fun init(path: java.nio.file.Path): CompletableFuture<java.lang.Void> {
        kotlin.check(net.minecraft.SharedConstants.getProtocolVersion() == ProtocolTranslationImpl.Companion.NATIVE_VERSION.getOriginalVersion()) {"Native version is not the same as the current version"} 
        
                // Register command callback for /viafabricplus
        ClientCommandRegistrationCallback.EVENT.register(ClientCommandRegistrationCallback {
        dispatcher: CommandDispatcher<FabricClientCommandSource>, registryAccess: net.minecraft.commands.CommandBuildContext -> val commandHandler: ViaFabricPlusCommandHandler = Via.getManager().getCommandHandler() as ViaFabricPlusCommandHandler
        val executor: RequiredArgumentBuilder<FabricClientCommandSource, kotlin.String> = RequiredArgumentBuilder.argument<FabricClientCommandSource, kotlin.String>("args", StringArgumentType.greedyString()).executes(com.mojang.brigadier.Command {ctx: CommandContext<FabricClientCommandSource> -> commandHandler.execute(ctx)}).suggests(SuggestionProvider {ctx: CommandContext<FabricClientCommandSource>, builder: SuggestionsBuilder -> commandHandler.suggestion(ctx, builder)})
        
        dispatcher.register(LiteralArgumentBuilder.literal<FabricClientCommandSource>("viafabricplus").then(executor).executes(com.mojang.brigadier.Command {ctx: CommandContext<FabricClientCommandSource> -> commandHandler.execute(ctx)}))
        
        })
        
        return CompletableFuture.runAsync(java.lang.Runnable {
        
            // Load ViaVersion and register all platforms and their components
        ViaManagerImpl.initAndLoad(
        ViaFabricPlusViaVersionPlatform(path.toFile()), 
        NoopInjector(), 
        ViaFabricPlusCommandHandler(), 
        ViaFabricPlusPlatformLoader(), 
        java.lang.Runnable {
        ViaBackwardsPlatformImpl()
        ViaFabricPlusViaLegacyPlatform()
        ViaAprilFoolsPlatformImpl()
        
        }
        )
        com.viaversion.viaversion.api.protocol.version.ProtocolVersion.register(com.viaversion.viafabricplus.api.protocoltranslator.ProtocolTranslation.AUTO_DETECT_VERSION)
        ViaFabricPlusProtocol.INSTANCE.initialize()
        
        }, net.minecraft.util.Util.backgroundExecutor())
    }
    
    companion object {
        val MINECRAFT_CONNECTION_ATTRIBUTE_KEY: AttributeKey<net.minecraft.network.Connection> = AttributeKey.newInstance<net.minecraft.network.Connection>("viafabricplus-minecraft-connection")
        val TARGET_VERSION_ATTRIBUTE_KEY: AttributeKey<com.viaversion.viaversion.api.protocol.version.ProtocolVersion> = AttributeKey.newInstance<com.viaversion.viaversion.api.protocol.version.ProtocolVersion>("viafabricplus-target-version")
        
        val NATIVE_VERSION: com.viaversion.viaversion.api.protocol.version.ProtocolVersion = com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v26_3
        
    }}
