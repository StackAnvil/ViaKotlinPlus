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
package com.viaversion.viafabricplus.injection.mixin.features.v1_21.network

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(net.minecraft.core.HolderSet.Named::class) 
abstract class MixinHolderSet_Named {

    @org.spongepowered.asm.mixin.Shadow 
    @JvmField val contents: kotlin.collections.MutableList<net.minecraft.core.Holder<*>> = uninitialized()
    @org.spongepowered.asm.mixin.Shadow abstract fun bind(contents: kotlin.collections.MutableList<net.minecraft.core.Holder<*>>)
    
    @Inject(method = ["contents"], at = [At("HEAD")]) private fun preventNullableEntries(cir: CallbackInfoReturnable<kotlin.collections.MutableList<net.minecraft.core.Holder<*>>>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21)) {
            // Previously didn't have unbounded entries
            if (this.contents == null) {
                this.bind(kotlin.collections.mutableListOf<net.minecraft.core.Holder<*>>())
            }
        }
    }
    
    @Inject(method = ["isBound"], at = [At("HEAD")], cancellable = true) private fun alwaysBound(cir: CallbackInfoReturnable<kotlin.Boolean>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21)) {
            // Previously didn't exist
            cir.setReturnValue(true)
        }
    }
    
}
