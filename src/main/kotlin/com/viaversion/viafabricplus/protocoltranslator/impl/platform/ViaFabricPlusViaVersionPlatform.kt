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
package com.viaversion.viafabricplus.protocoltranslator.impl.platform

import com.viaversion.viaversion.platform.UserConnectionViaVersionPlatform
import com.viaversion.viafabricplus.protocoltranslator.util.JLoggerToSLF4J
import com.viaversion.viaversion.configuration.AbstractViaConfig
import com.viaversion.viafabricplus.protocoltranslator.impl.viaversion.ViaFabricPlusConfig
import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper
import com.viaversion.viafabricplus.protocoltranslator.protocol.ViaFabricPlusProtocol
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslationImpl
import net.fabricmc.loader.api.metadata.ModMetadata
import com.viaversion.viafabricplus.ViaFabricPlusImpl
import com.viaversion.viaversion.util.GsonUtil
import com.viaversion.viafabricplus.util.ClassLoaderPriorityUtil

class ViaFabricPlusViaVersionPlatform (dataFolder: java.io.File) : UserConnectionViaVersionPlatform(dataFolder) {

    override fun createLogger(name: kotlin.String): java.util.logging.Logger {
        return JLoggerToSLF4J(org.slf4j.LoggerFactory.getLogger(name))
    }
    
    override fun getPlatformName(): kotlin.String {
        return "ViaFabricPlus"
    }
    
    override fun getPlatformVersion(): kotlin.String {
        return com.viaversion.viafabricplus.ViaFabricPlus.api().version()
    }
    
    override fun createConfig(): AbstractViaConfig {
        return ViaFabricPlusConfig(java.io.File(getDataFolder(), "viaversion.yml"), this.getLogger())
    }
    
    override fun sendCustomPayload(connection: UserConnection, channel: kotlin.String, message: ByteArray) {
        val customPayload: PacketWrapper = PacketWrapper.create(ViaFabricPlusProtocol.INSTANCE.getCustomPayloadPacketType(), connection)
        customPayload.write<kotlin.String>(com.viaversion.viaversion.api.type.Types.STRING, channel)
        customPayload.write<ByteArray>(com.viaversion.viaversion.api.type.Types.REMAINING_BYTES, message)
        customPayload.scheduleSendToServer(ViaFabricPlusProtocol::class.java)
    }
    
    override fun sendCustomPayloadToClient(connection: UserConnection, channel: kotlin.String, message: ByteArray) {
        val customPayload: PacketWrapper = PacketWrapper.create(ViaFabricPlusProtocol.INSTANCE.getClientboundCustomPayloadPacketType(), connection)
        customPayload.write<kotlin.String>(com.viaversion.viaversion.api.type.Types.STRING, channel)
        customPayload.write<ByteArray>(com.viaversion.viaversion.api.type.Types.REMAINING_BYTES, message)
        customPayload.scheduleSend(ViaFabricPlusProtocol::class.java)
    }
    
    override fun getDump(): com.viaversion.viaversion.libs.gson.JsonObject {
        val platformDump: com.viaversion.viaversion.libs.gson.JsonObject = com.viaversion.viaversion.libs.gson.JsonObject()
        platformDump.addProperty("impl_version", com.viaversion.viafabricplus.ViaFabricPlus.api().implVersion())
        platformDump.addProperty("native_version", ProtocolTranslationImpl.NATIVE_VERSION.toString())
        platformDump.addProperty("target_version", com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().toString())
        platformDump.addProperty("in_world", net.minecraft.client.Minecraft.getInstance().level != null)
        
        val allMods: kotlin.collections.MutableCollection<net.fabricmc.loader.api.ModContainer> = net.fabricmc.loader.api.FabricLoader.getInstance().getAllMods()
        val mods: com.viaversion.viaversion.libs.gson.JsonArray = com.viaversion.viaversion.libs.gson.JsonArray(allMods.size)
        for (modContainer in allMods) {
            val metadata: ModMetadata = modContainer.getMetadata()
            val mod: com.viaversion.viaversion.libs.gson.JsonObject = com.viaversion.viaversion.libs.gson.JsonObject()
            mod.addProperty("id", metadata.getId())
            mod.addProperty("name", metadata.getName())
            mod.addProperty("version", metadata.getVersion().getFriendlyString())
            val authors: com.viaversion.viaversion.libs.gson.JsonArray = com.viaversion.viaversion.libs.gson.JsonArray(metadata.getAuthors().size)
            for (person in metadata.getAuthors()) {
                val info: com.viaversion.viaversion.libs.gson.JsonObject = com.viaversion.viaversion.libs.gson.JsonObject()
                val contactMap: kotlin.collections.MutableMap<kotlin.String, kotlin.String> = person.getContact().asMap()
                if (!contactMap.isEmpty()) {
                    val contact: com.viaversion.viaversion.libs.gson.JsonObject = com.viaversion.viaversion.libs.gson.JsonObject()
                    contactMap.forEach {(property: kotlin.String, value: kotlin.String) -> contact.addProperty(property, value)} 
                    info.add("contact", contact)
                }
                info.addProperty("name", person.getName())
                authors.add(info)
            }
            mod.add("authors", authors)
            
            mods.add(mod)
        }
        
        platformDump.add("mods", mods)
        
        val settings: com.google.gson.JsonObject = com.google.gson.JsonObject()
        for (group in ViaFabricPlusImpl.impl().settings().groups()) {
            group.write(settings)
        }
        platformDump.add("settings", GsonUtil.getGson().fromJson<com.viaversion.viaversion.libs.gson.JsonObject>(settings.toString(), com.viaversion.viaversion.libs.gson.JsonObject::class.java))
        
        if (!ClassLoaderPriorityUtil.getOverridingJars().isEmpty()) {
            val overridingJars: com.viaversion.viaversion.libs.gson.JsonArray = com.viaversion.viaversion.libs.gson.JsonArray()
            for (jar in ClassLoaderPriorityUtil.getOverridingJars()) {
                overridingJars.add(jar)
            }
            platformDump.add("overriding_jars", overridingJars)
        }
        
        return platformDump
    }
    
}
