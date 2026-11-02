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
package com.viaversion.viafabricplus.api.protocoltranslator

import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viaversion.api.protocol.version.VersionType

 /**
 * Protocol translator.
 */
interface ProtocolTranslation {

     /**
 * The target version either selected by the user in the global menu or set per server. Note that this method
 * only works after the initial connection has been established.
 * 
 * @return the target version
 */
    fun targetVersion(): com.viaversion.viaversion.api.protocol.version.ProtocolVersion
    
     /**
 * The target version for the given connection. This method is useful for retrieving the version before the global
 * version has been set ([.targetVersion]) and might work in earlier stages of the connection. Note that
 * there is no guarantee for the value returned here.
 * 
 * @param connection the connection
 * @return the target version
 */
    fun targetVersion(connection: net.minecraft.network.Connection): com.viaversion.viaversion.api.protocol.version.ProtocolVersion
    
     /**
 * Same as [.targetVersion] but for the given channel.
 * 
 * @param channel the channel
 * @return the target version
 */
    fun targetVersion(channel: io.netty.channel.Channel): com.viaversion.viaversion.api.protocol.version.ProtocolVersion
    
     /**
 * Sets the global target version permanently.
 * 
 * @param targetVersion the target version
 */
    fun setTargetVersion(targetVersion: com.viaversion.viaversion.api.protocol.version.ProtocolVersion) {
        this.setTargetVersion(targetVersion, false)
    }
    
     /**
 * Sets the global target version. If the client disconnects, the version will be reverted to the previous version.
 * 
 * @param targetVersion      the target version
 * @param revertOnDisconnect whether to revert the version on disconnect
 */
    fun setTargetVersion(targetVersion: com.viaversion.viaversion.api.protocol.version.ProtocolVersion, revertOnDisconnect: kotlin.Boolean)
    
     /**
 * Adds a listener for when the protocol version changes.
 * 
 * @param listener The listener to add, which will be called when the protocol version changes.
 */
    fun addChangeProtocolVersionListener(listener: java.util.function.BiConsumer<com.viaversion.viaversion.api.protocol.version.ProtocolVersion, com.viaversion.viaversion.api.protocol.version.ProtocolVersion>)
    
     /**
 * ViaVersion's user connection object. This is set after the initial connection has been established. Useful
 * for when sending raw packets or modifying the client's state. Only valid as soon as the client switched its
 * network state to PLAY (!)
 * 
 * @return the user connection
 */
    fun userConnection(): UserConnection?
    
     /**
 * Similar to [.userConnection], but for the given connection. This method also works in earlier stages
 * than PLAY (e.g., LOGIN; CONFIGURATION).
 * 
 * @param connection the connection
 * @return the user connection
 */
    fun userConnection(connection: net.minecraft.network.Connection): UserConnection
    
     /**
 * The pre-server version of the server. This is only valid if the user has set a specific version for the given server.
 * 
 * @param serverData the server data
 * @return the pre-server version of the server
 */
    fun serverVersion(serverData: net.minecraft.client.multiplayer.ServerData): com.viaversion.viaversion.api.protocol.version.ProtocolVersion
    
     /**
 * Sets the pre-server version of the server.
 * 
 * @param serverData the server data
 * @param version    the version
 */
    fun setServerVersion(serverData: net.minecraft.client.multiplayer.ServerData, version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion)
    
    companion object {
         /**
 * The protocol version used when version auto-detection is enabled. Note that this won't be returned on any
 * version getters after the initial connection has been established (e.g., [.targetVersion]).
 */
        val AUTO_DETECT_VERSION: com.viaversion.viaversion.api.protocol.version.ProtocolVersion = object : com.viaversion.viaversion.api.protocol.version.ProtocolVersion(VersionType.SPECIAL, -2, -1, "version_title.viafabricplus.auto_detect", null) {
            override fun customComparator(): java.util.Comparator<com.viaversion.viaversion.api.protocol.version.ProtocolVersion> {
                return java.util.Comparator {o1: com.viaversion.viaversion.api.protocol.version.ProtocolVersion, o2: com.viaversion.viaversion.api.protocol.version.ProtocolVersion -> if (o1 === com.viaversion.viafabricplus.api.protocoltranslator.ProtocolTranslation.Companion.AUTO_DETECT_VERSION) {
                    return@Comparator 1
                } else if (o2 === com.viaversion.viafabricplus.api.protocoltranslator.ProtocolTranslation.Companion.AUTO_DETECT_VERSION) {
                    return@Comparator -1
                } else {
                    return@Comparator 0
                }
                }
            }
            
            override fun getName(): kotlin.String {
                return net.minecraft.network.chat.Component.translatable(super.getName()).getString()
            }
            
            override fun isKnown(): kotlin.Boolean {
                return false
            }
        }
        
    }}
