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
import org.spongepowered.asm.mixin.Final
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
import org.spongepowered.asm.mixin.injection.At
import com.viaversion.viafabricplus.ViaFabricPlusImpl

@Mixin(targets = ["net.minecraft.world.item.CreativeModeTab\$ItemDisplayBuilder"]) 
abstract class MixinCreativeModeTab_ItemDisplayBuilder {

    @org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField val tab: net.minecraft.world.item.CreativeModeTab = uninitialized()
    @WrapOperation(method = ["accept"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;isEnabled(Lnet/minecraft/world/flag/FeatureFlagSet;)Z")]) private fun removeUnknownItems(instance: net.minecraft.world.item.Item, featureSet: net.minecraft.world.flag.FeatureFlagSet, original: com.llamalad7.mixinextras.injector.wrapoperation.Operation<kotlin.Boolean>, @com.llamalad7.mixinextras.sugar.Local(argsOnly = true) stack: net.minecraft.world.item.ItemStack): kotlin.Boolean {
        val originalValue: kotlin.Boolean = original.call(instance, featureSet)
        val filter: com.viaversion.viafabricplus.api.settings.impl.ItemFilter = ViaFabricPlusImpl.impl().options().removeNotAvailableItemsFromCreativeTab().value()
        
        if (filter == com.viaversion.viafabricplus.api.settings.impl.ItemFilter.OFF || net.minecraft.client.Minecraft.getInstance().isLocalServer()) {
            return originalValue
        } else if (filter == com.viaversion.viafabricplus.api.settings.impl.ItemFilter.VANILLA_ONLY && net.minecraft.core.registries.BuiltInRegistries.CREATIVE_MODE_TAB.getKey(this.tab).getNamespace() != net.minecraft.resources.Identifier.DEFAULT_NAMESPACE) {
            return originalValue
        } else {
            return com.viaversion.viafabricplus.ViaFabricPlus.api().limitations().itemExistsInConnection(stack) && originalValue
        }
    }
    
}
