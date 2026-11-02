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
package com.viaversion.viafabricplus.injection

import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import com.viaversion.viafabricplus.injection.ViaFabricPlusMixinPlugin
import com.viaversion.viafabricplus.features.global.FabricAPIWorkaround
import com.viaversion.viafabricplus.features.global.LithiumWorkaround

class ViaFabricPlusMixinPlugin : IMixinConfigPlugin {

    override fun onLoad(mixinPackage: kotlin.String) {
        //org.spongepowered.asm.mixin.MixinEnvironment.getCurrentEnvironment().audit(); // Uncomment during Minecraft updates
        val loader: net.fabricmc.loader.api.FabricLoader = net.fabricmc.loader.api.FabricLoader.getInstance()
        ViaFabricPlusMixinPlugin.Companion.IPNEXT_PRESENT = loader.isModLoaded("inventoryprofilesnext")
        ViaFabricPlusMixinPlugin.Companion.MORE_CULLING_PRESENT = loader.isModLoaded("moreculling")
        ViaFabricPlusMixinPlugin.Companion.LITHIUM_PRESENT = loader.isModLoaded("lithium")
        ViaFabricPlusMixinPlugin.Companion.MOONRISE_PRESENT = loader.isModLoaded("moonrise")
        ViaFabricPlusMixinPlugin.Companion.LEGENDARYTOOLTIPS_PRESENT = loader.isModLoaded("legendarytooltips")
        ViaFabricPlusMixinPlugin.Companion.LEGACY_PRESENT = loader.isModLoaded("legacy")
        
        FabricAPIWorkaround.init()
        LithiumWorkaround.init()
    }
    
    override fun shouldApplyMixin(targetClassName: kotlin.String, mixinClassName: kotlin.String): kotlin.Boolean {
        return when (mixinClassName){ViaFabricPlusMixinPlugin.Companion.MIXINS_PACKAGE + "compat.ipnext.MixinAutoRefillHandler_ItemSlotMonitor" -> ViaFabricPlusMixinPlugin.Companion.IPNEXT_PRESENT
            ViaFabricPlusMixinPlugin.Companion.MIXINS_PACKAGE + "compat.lithium.MixinEntity" -> ViaFabricPlusMixinPlugin.Companion.LITHIUM_PRESENT && !ViaFabricPlusMixinPlugin.Companion.MOONRISE_PRESENT
            ViaFabricPlusMixinPlugin.Companion.MIXINS_PACKAGE + "features.v1_8.item.MixinItemStack" -> !ViaFabricPlusMixinPlugin.Companion.LEGENDARYTOOLTIPS_PRESENT
            ViaFabricPlusMixinPlugin.Companion.MIXINS_PACKAGE + "features.v1_10.MixinGuiGraphicsExtractor" -> !ViaFabricPlusMixinPlugin.Companion.LEGACY_PRESENT
            else -> true}
    }
    
    companion object {
        private const val MIXINS_PACKAGE: kotlin.String = "com.viaversion.viafabricplus.injection.mixin."
        
        var IPNEXT_PRESENT: kotlin.Boolean = false
        var MORE_CULLING_PRESENT: kotlin.Boolean = false
        var LITHIUM_PRESENT: kotlin.Boolean = false
        var MOONRISE_PRESENT: kotlin.Boolean = false
        var LEGENDARYTOOLTIPS_PRESENT: kotlin.Boolean = false
        var LEGACY_PRESENT: kotlin.Boolean = false
        
    }}
