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
package com.viaversion.viafabricplus.injection.mixin.features.v1_12_2.particle

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
import org.spongepowered.asm.mixin.injection.At
import com.viaversion.viafabricplus.features.v1_12_2.FootStepParticle1_12_2

@Mixin(RegistrySyncManager::class) 
class MixinRegistrySyncManager {
private companion object {
        @JvmStatic @WrapOperation(method = ["createAndPopulateRegistryMap"], at = [At(value = "INVOKE", target = "Lnet/minecraft/core/Registry;getKey(Ljava/lang/Object;)Lnet/minecraft/resources/Identifier;")], require = 0) fun skipFootStepParticle(instance: net.minecraft.core.Registry<*>, t: kotlin.Any, original: com.llamalad7.mixinextras.injector.wrapoperation.Operation<net.minecraft.resources.Identifier>): net.minecraft.resources.Identifier {
            val id: net.minecraft.resources.Identifier = original.call(instance, t)
            if (id === FootStepParticle1_12_2.ID) {
                return uninitialized()
            } else {
                return id
            }
        }
    
    }
}
