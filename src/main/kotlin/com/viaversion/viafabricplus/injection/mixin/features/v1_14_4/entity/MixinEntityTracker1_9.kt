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
package com.viaversion.viafabricplus.injection.mixin.features.v1_14_4.entity

import org.spongepowered.asm.mixin.Mixin
import com.viaversion.viaversion.protocols.v1_8to1_9.storage.EntityTracker1_9
import org.spongepowered.asm.mixin.injection.At

@Mixin(value = [EntityTracker1_9::class], remap = false) 
abstract class MixinEntityTracker1_9 {

    @org.spongepowered.asm.mixin.injection.Redirect(method = ["handleEntityData"], at = [At(value = "INVOKE", target = "Ljava/lang/Math;min(FF)F")], slice = [org.spongepowered.asm.mixin.injection.Slice(from = At(value = "INVOKE", target = "Lcom/viaversion/viaversion/api/configuration/ViaVersionConfig;isBossbarAntiflicker()Z"))]) private fun removeMin(a: kotlin.Float, b: kotlin.Float): kotlin.Float {
        return a
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["handleEntityData"], at = [At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F")], slice = [org.spongepowered.asm.mixin.injection.Slice(from = At(value = "INVOKE", target = "Lcom/viaversion/viaversion/api/configuration/ViaVersionConfig;isBossbarAntiflicker()Z"))]) private fun removeMax(a: kotlin.Float, b: kotlin.Float): kotlin.Float {
        return b
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["handleEntityData"], at = [At(value = "INVOKE", target = "Lcom/viaversion/viaversion/api/minecraft/entitydata/EntityData;getValue()Ljava/lang/Object;")], slice = [org.spongepowered.asm.mixin.injection.Slice(from = At(value = "INVOKE", target = "Lcom/viaversion/viaversion/api/configuration/ViaVersionConfig;isBossbarAntiflicker()Z"))]) private fun remapNaNToZero(instance: com.viaversion.viaversion.api.minecraft.entitydata.EntityData): kotlin.Any {
        if (instance.getValue() is kotlin.Float && (instance.getValue() as kotlin.Float).isNaN()) {
            return 0f
        } else {
            return instance.getValue()!!
        }
    }
    
}
