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
package com.viaversion.viafabricplus.protocoltranslator.util

import java.net.InetSocketAddress
import java.io.DataOutputStream
import java.io.DataInputStream
import com.viaversion.viafabricplus.protocoltranslator.util.ProtocolVersionDetector
import com.viaversion.viafabricplus.util.JsonSave
import java.io.IOException

 /**
 * This class can be used to detect the protocol version of a server without connecting to it.
 */
object ProtocolVersionDetector {
    private const val TIMEOUT: kotlin.Int = 3000
    
     /**
 * Detects the protocol version of a server
 * 
 * @param serverAddress The address of the server
 * @param clientVersion The version of the client
 * @return The protocol version of the server
 */
    @Throws(java.lang.Exception::class) fun get(serverAddress: net.minecraft.client.multiplayer.resolver.ServerAddress, socketAddress: InetSocketAddress, clientVersion: com.viaversion.viaversion.api.protocol.version.ProtocolVersion): com.viaversion.viaversion.api.protocol.version.ProtocolVersion {
        java.net.Socket(serverAddress.getHost(), serverAddress.getPort()).use {socket -> DataOutputStream(socket.getOutputStream()).use {dataOutputStream -> DataInputStream(socket.getInputStream()).use {dataInputStream -> java.io.ByteArrayOutputStream().use {byteArrayOutputStream -> DataOutputStream(byteArrayOutputStream).use {
        handshakePacket -> socket.setTcpNoDelay(true)
        socket.setSoTimeout(ProtocolVersionDetector.TIMEOUT)
        
                    // Write handshake packet
        handshakePacket.writeByte(0) // Packet ID
        
        ProtocolVersionDetector.writeVarInt(handshakePacket, clientVersion.getOriginalVersion())
        if (clientVersion.olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_17)) {
            ProtocolVersionDetector.writeString(handshakePacket, serverAddress.getHost())
            handshakePacket.writeShort(serverAddress.getPort())
        } else {
            ProtocolVersionDetector.writeString(handshakePacket, socketAddress.getHostString())
            handshakePacket.writeShort(socketAddress.getPort())
        }
        ProtocolVersionDetector.writeVarInt(handshakePacket, net.minecraft.network.protocol.handshake.ClientIntent.STATUS.id())
        
        ProtocolVersionDetector.writeVarInt(dataOutputStream, byteArrayOutputStream.size())
        dataOutputStream.write(byteArrayOutputStream.toByteArray())
        
                    // Write ping request packet
        dataOutputStream.writeByte(1)
        dataOutputStream.writeByte(0)
        
                    // Receive ping response packet
        val size: kotlin.Int = ProtocolVersionDetector.readVarInt(dataInputStream)
        kotlin.check(size > 0) {"Invalid packet size"} 
        val id: kotlin.Int = ProtocolVersionDetector.readVarInt(dataInputStream)
        kotlin.check(id == 0) {"Invalid packet ID"} 
        
        val response: kotlin.String = ProtocolVersionDetector.readString(dataInputStream)
        val `object`: com.google.gson.JsonObject = JsonSave.GSON.fromJson<com.google.gson.JsonObject>(response, com.google.gson.JsonObject::class.java)
        kotlin.check(`object`.has("version")) {"Invalid ping response"} 
        
        val version: com.google.gson.JsonObject = `object`.getAsJsonObject("version")
        kotlin.check(!(!version.has("name") || !version.has("protocol"))) {"Invalid ping response"} 
        
        val serverVersion: kotlin.Int = version.get("protocol").getAsInt()
        
                    // If the server is on the same version as the client, we can just connect
        if (clientVersion.getOriginalVersion() == serverVersion) {
            return clientVersion
        }
        
                    // If the protocol is registered, we can use it
        if (com.viaversion.viaversion.api.protocol.version.ProtocolVersion.isRegistered(serverVersion)) {
            return com.viaversion.viaversion.api.protocol.version.ProtocolVersion.getProtocol(serverVersion)
        }
        
                    // Fallback with the name
        val name: kotlin.String = version.get("name").getAsString()
        for (protocol in com.viaversion.viaversion.api.protocol.version.ProtocolVersion.getReversedProtocols()) {
            for (includedVersion in protocol.getIncludedVersions()) {
                if (name.contains(includedVersion)) {
                    return protocol
                }
            }
        }
        
        throw java.lang.RuntimeException("Unable to detect the server version\nServer sent an invalid protocol id: " + serverAddress + " (" + name + net.minecraft.ChatFormatting.RESET + ")")
        
        } } } } } }
    
    @Throws(IOException::class) private fun readVarInt(`in`: DataInputStream): kotlin.Int {
        var i: kotlin.Int = 0
        var j: kotlin.Int = 0
        var b: kotlin.Byte = 0
        do {
            b = `in`.readByte()
            i = i or ((b.toInt() and 127) shl j++ * 7)
            if (j > 5) {
                throw IOException("Var int too big")
            }
        } while ((b.toInt() and 128) == 128)
        
        return i
    }
    
    @Throws(IOException::class) private fun readString(`in`: DataInputStream): kotlin.String {
        val length: kotlin.Int = ProtocolVersionDetector.readVarInt(`in`)
        if (length > kotlin.Short.Companion.MAX_VALUE * 4) {
            throw IOException("Cannot receive string longer than Short.MAX_VALUE * 4 bytes (got " + length + " bytes)")
        }
        if (length < 0) {
            throw IOException("Cannot receive string shorter than 0 bytes (got " + length + " bytes)")
        }
        
        val bytes: ByteArray = ByteArray(length)
        `in`.readFully(bytes)
        val string: kotlin.String = kotlin.text.String(bytes, java.nio.charset.StandardCharsets.UTF_8)
        if (string.length > kotlin.Short.Companion.MAX_VALUE) {
            throw IOException("Cannot receive string longer than Short.MAX_VALUE characters (got " + string.length + " bytes)")
        }
        
        return string
    }
    
    @Throws(IOException::class) private fun writeVarInt(out: DataOutputStream, value: kotlin.Int) {
        var value: kotlin.Int = value
        while ((value and -128) != 0) {
            out.writeByte(value and 127 or 128)
            value = value ushr 7
        }
        out.writeByte(value)
    }
    
    @Throws(IOException::class) private fun writeString(out: DataOutputStream, value: kotlin.String) {
        val bytes: ByteArray = value.toByteArray(java.nio.charset.StandardCharsets.UTF_8)
        ProtocolVersionDetector.writeVarInt(out, bytes.size)
        out.write(bytes)
    }
    
}
