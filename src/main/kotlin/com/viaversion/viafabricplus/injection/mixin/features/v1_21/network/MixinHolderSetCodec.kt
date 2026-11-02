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

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import com.mojang.serialization.DataResult

@Mixin(net.minecraft.core.registries.codec.HolderSetCodec::class) 
class MixinHolderSetCodec {
private companion object {
        @JvmStatic @Inject(method = ["lookupTag"], at = [At("RETURN")], cancellable = true) fun workaroundValidation(registry: net.minecraft.core.HolderGetter<*>, key: net.minecraft.tags.TagKey<*>, cir: CallbackInfoReturnable<DataResult<net.minecraft.core.HolderSet<*>>>) {
            if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21)) {
                // The client now throws if the server references tags, which weren't previously sent via protocol,
                // instead of implementing verification on protocol level, we just skip the error and return an empty registry list
                if (cir.getReturnValue().isError()) {
                    cir.setReturnValue(DataResult.success<net.minecraft.core.HolderSet<*>>(net.minecraft.core.HolderSet.empty<kotlin.Any>()))
                }
            }
        }
    
    }
}
