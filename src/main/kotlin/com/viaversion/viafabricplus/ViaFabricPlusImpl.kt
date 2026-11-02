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
package com.viaversion.viafabricplus

import com.viaversion.viafabricplus.settings.SettingsImpl
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslationImpl
import com.viaversion.viafabricplus.protocoltranslator.ConversionsImpl
import com.viaversion.viafabricplus.protocoltranslator.LimitationsImpl
import com.viaversion.viafabricplus.screen.ScreensImpl
import java.util.concurrent.CompletableFuture
import com.viaversion.viafabricplus.ViaFabricPlusImpl
import net.fabricmc.loader.api.metadata.ModMetadata
import java.io.IOException
import com.viaversion.viafabricplus.util.ClassLoaderPriorityUtil
import com.viaversion.viafabricplus.features.FeaturesLoading

class ViaFabricPlusImpl : com.viaversion.viafabricplus.api.ViaFabricPlusAPI, com.viaversion.viafabricplus.api.entrypoint.ViaFabricPlusEntrypoint {

    private val logger: org.apache.logging.log4j.Logger = org.apache.logging.log4j.LogManager.getLogger("ViaFabricPlus")
    private val path: java.nio.file.Path = net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().resolve("viafabricplus")
    
    private val settings: SettingsImpl = SettingsImpl()
    private val protocolTranslation: ProtocolTranslationImpl = ProtocolTranslationImpl()
    private val conversions: ConversionsImpl = ConversionsImpl()
    private val limitations: LimitationsImpl = LimitationsImpl()
    private lateinit var screens: ScreensImpl
    
    private val version: kotlin.String
    private val implVersion: kotlin.String
    private lateinit var loadingFuture: CompletableFuture<java.lang.Void>
    
    init {
        ViaFabricPlusImpl.Companion.INSTANCE = this
        val metadata: ModMetadata = net.fabricmc.loader.api.FabricLoader.getInstance().getModContainer("viafabricplus").get().getMetadata()
        this.version = metadata.getVersion().getFriendlyString()
        this.implVersion = metadata.getCustomValue("vfp:implVersion").getAsString()
    }
    
    override fun onPreLoading() {
        com.viaversion.viafabricplus.ViaFabricPlus.init(this)
        try {
            java.nio.file.Files.createDirectories(this.path)
        }catch (e: IOException) {
            this.logger.error("Failed to create ViaFabricPlus directory", e)
        }
        
        ClassLoaderPriorityUtil.loadOverridingJars(this.path, logger)
        this.settings.init()
        FeaturesLoading.onPreLoading()
        
        this.loadingFuture = this.protocolTranslation.init(this.path)
    }
    
    override fun onPostRegistryLoading() {
        FeaturesLoading.onPostRegistryLoading()
    }
    
    override fun onPostGameLoading() {
        this.limitations.init()
        this.screens = ScreensImpl()
        
        FeaturesLoading.onPostGameLoading()
        this.loadingFuture.join()
        this.settings.postInit()
        System.getProperty("viakotlinplus.test.targetVersion")?.let { version ->
            this.setTargetVersion(
                com.viaversion.viaversion.api.protocol.version.ProtocolVersion.getClosest(version)!!
            )
        }
    }
    
    override fun version(): kotlin.String {
        return this.version
    }
    
    override fun implVersion(): kotlin.String {
        return this.implVersion
    }
    
    override fun path(): java.nio.file.Path {
        return this.path
    }
    
    override fun logger(): org.apache.logging.log4j.Logger {
        return this.logger
    }
    
    override fun settings(): SettingsImpl {
        return this.settings
    }
    
    fun options(): com.viaversion.viafabricplus.api.settings.impl.GeneralSettings {
        return this.settings.general()
    }
    
    fun visuals(): com.viaversion.viafabricplus.api.settings.impl.VisualSettings {
        return this.settings.visual()
    }
    
    fun advanced(): com.viaversion.viafabricplus.api.settings.impl.AdvancedSettings {
        return this.settings.advanced()
    }
    
    override fun protocolTranslation(): ProtocolTranslationImpl {
        return this.protocolTranslation
    }
    
    override fun conversions(): ConversionsImpl {
        return this.conversions
    }
    
    override fun limitations(): LimitationsImpl {
        return this.limitations
    }
    
    override fun screens(): ScreensImpl {
        return this.screens
    }
    
    companion object {
        private lateinit var INSTANCE: ViaFabricPlusImpl
        
        fun impl(): ViaFabricPlusImpl {
            return ViaFabricPlusImpl.Companion.INSTANCE
        }
        
    }}
