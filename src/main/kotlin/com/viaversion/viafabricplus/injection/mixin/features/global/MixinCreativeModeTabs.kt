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
package com.viaversion.viafabricplus.injection.mixin.features.global

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import com.viaversion.viafabricplus.injection.mixin.features.global.MixinCreativeModeTabs
import com.viaversion.viafabricplus.ViaFabricPlusImpl

@Mixin(net.minecraft.world.item.CreativeModeTabs::class) 
class MixinCreativeModeTabs {
private companion object {
        @JvmStatic @Inject(method = ["tryRebuildTabContents"], at = [At("HEAD")]) fun trackLastVersion(enabledFeatures: net.minecraft.world.flag.FeatureFlagSet, hasPermissions: kotlin.Boolean, lookup: net.minecraft.core.HolderLookup.Provider, cir: CallbackInfoReturnable<kotlin.Boolean>) {
            if (`viaFabricPlus$version` !== com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion() || `viaFabricPlus$state` != ViaFabricPlusImpl.impl().options().removeNotAvailableItemsFromCreativeTab().value()) {
                `viaFabricPlus$version` = com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion()
                `viaFabricPlus$state` = ViaFabricPlusImpl.impl().options().removeNotAvailableItemsFromCreativeTab().value()
            
                CACHED_PARAMETERS = uninitialized() // Makes vanilla rebuild the tab contents
            }
        }
    

        @JvmField
        @org.spongepowered.asm.mixin.Shadow
        var CACHED_PARAMETERS: net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters = uninitialized()

        @JvmField
        @org.spongepowered.asm.mixin.Unique
        var `viaFabricPlus$version`: com.viaversion.viaversion.api.protocol.version.ProtocolVersion = uninitialized()

        @JvmField
        @org.spongepowered.asm.mixin.Unique
        var `viaFabricPlus$state`: com.viaversion.viafabricplus.api.settings.impl.ItemFilter = uninitialized()
    
    }
}
