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
package com.viaversion.viafabricplus.injection.mixin.features.v1_16_4.entity

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.injection.At
import java.util.UUID
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(net.minecraft.world.level.entity.EntityLookup::class) 
abstract class MixinEntityLookup<T> {

    @org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField val byId: it.unimi.dsi.fastutil.ints.Int2ObjectMap<T> = uninitialized()
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["add"], at = [At(value = "INVOKE", target = "Ljava/util/Map;containsKey(Ljava/lang/Object;)Z", remap = false)]) private fun allowDuplicateUuid(instance: kotlin.collections.MutableMap<UUID, T>, o: kotlin.Any): kotlin.Boolean {
        return instance.containsKey(o) && com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_16_4)
    }
    
    @Inject(method = ["count"], at = [At("HEAD")], cancellable = true) private fun returnRealSize(cir: CallbackInfoReturnable<kotlin.Int>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_16_4)) {
            cir.setReturnValue(this.byId.size)
        }
    }
    
}
