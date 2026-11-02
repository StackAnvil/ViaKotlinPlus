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
package com.viaversion.viafabricplus.api

import com.viaversion.viaversion.api.connection.UserConnection

 /**
 * General API point for mods. Get an instance via [ViaFabricPlus.api].
 */
interface ViaFabricPlusAPI {

     /**
 * Returns an **internally based API version** incremented with meaningful API changes.
 * This includes breaking changes to the existing API and larger additions.
 * 
 * @return API version incremented with meaningful API changes
 */
    fun apiVersion(): kotlin.Int {
        return 8
    }
    
     /**
 * The version of the mod displayed in ModMenu and other places (e.g., 5.0.0)
 * 
 * @return the version of the mod
 */
    fun version(): kotlin.String
    
     /**
 * The implementation version of the mod formatted as "git-ViaFabricPlus-[.version]:{commit hash}".
 * 
 * @return the implementation version of the mod
 */
    fun implVersion(): kotlin.String
    
     /**
 * The root path inside the config folder where configuration files are stored.
 * 
 * @return The root path of the mod
 */
    fun path(): java.nio.file.Path
    
     /**
 * The logger of the mod.
 * 
 * @return The logger of the mod
 */
    fun logger(): org.apache.logging.log4j.Logger
    
     /**
 * The settings of the mod. Allows accessing and registering settings.
 * 
 * @return The settings of the mod
 */
    fun settings(): com.viaversion.viafabricplus.api.settings.Settings
    
     /**
 * The protocol translation instance. Manages the injection of new connections and the translation of packets.
 * 
 * @return The protocol translation instance
 */
    fun protocolTranslation(): com.viaversion.viafabricplus.api.protocoltranslator.ProtocolTranslation
    
     /**
 * @see ProtocolTranslation.targetVersion
 */
    fun targetVersion(): com.viaversion.viaversion.api.protocol.version.ProtocolVersion {
        return this.protocolTranslation().targetVersion()
    }
    
     /**
 * @see ProtocolTranslation.setTargetVersion
 */
    fun setTargetVersion(targetVersion: com.viaversion.viaversion.api.protocol.version.ProtocolVersion) {
        this.protocolTranslation().setTargetVersion(targetVersion)
    }
    
     /**
 * @see ProtocolTranslation.addChangeProtocolVersionListener
 */
    fun addChangeProtocolVersionListener(listener: java.util.function.BiConsumer<com.viaversion.viaversion.api.protocol.version.ProtocolVersion, com.viaversion.viaversion.api.protocol.version.ProtocolVersion>) {
        this.protocolTranslation().addChangeProtocolVersionListener(listener)
    }
    
     /**
 * @see ProtocolTranslation.userConnection
 */
    fun userConnection(): UserConnection? {
        return this.protocolTranslation().userConnection()
    }
    
     /**
 * Protocol translation conversions. Can be used to convert Items and other data types between versions and
 * Minecraft and ViaVersion formats.
 * 
 * @return Protocol translation conversions
 */
    fun conversions(): com.viaversion.viafabricplus.api.protocoltranslator.Conversions
    
     /**
 * Version-dependent limitations. Can be used to check if a certain feature is supported by the current version.
 * 
 * @return Version-dependent limitations
 */
    fun limitations(): com.viaversion.viafabricplus.api.protocoltranslator.Limitations
    
     /**
 * Screen handling. Allows accessing ViaFabricPlus screens.
 * 
 * @return Screen handling
 */
    fun screens(): com.viaversion.viafabricplus.api.screen.Screens
    
}
