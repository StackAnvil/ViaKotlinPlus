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
package com.viaversion.viafabricplus.api.entrypoint

 /**
 * Entrypoint for ViaFabricPlus. Registered in the fabric.mod.json via the "viafabricplus" key. Can be used to hook
 * custom settings via [Settings].
 */
interface ViaFabricPlusEntrypoint {
 /**
 * Preloading hook. Called before Minecraft is loaded.
 */
    fun onPreLoading() {
    }
    
     /**
 * Called before the settings are loaded.
 */
    fun onPreSettingsLoading() {
    }
    
     /**
 * Called after the settings have been loaded and registered.
 */
    fun onPostSettingsLoading() {
    }
    
     /**
 * Called after all ViaVersion components have been loaded.
 */
    fun onPostProtocolTranslationLoading() {
    }
    
     /**
 * Called after Minecraft has filled its registries. Can be used to hook custom registry elements.
 */
    fun onPostRegistryLoading() {
    }
    
     /**
 * Called after the game has finished loading.
 */
    fun onPostGameLoading() {
    }
 }
