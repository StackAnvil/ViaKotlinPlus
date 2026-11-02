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
package com.viaversion.viafabricplus.injection.mixin.features.v1_12_2

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.viaversion.viafabricplus.ViaFabricPlusImpl

@Mixin(net.minecraft.client.Camera::class) 
abstract class MixinCamera {

    @field:org.spongepowered.asm.mixin.Shadow 
    @JvmField var eyeHeight: kotlin.Float = 0f
    
    @field:org.spongepowered.asm.mixin.Shadow 
    @JvmField var eyeHeightOld: kotlin.Float = 0f
    
    @org.spongepowered.asm.mixin.Shadow 
    @JvmField val entity: net.minecraft.world.entity.Entity = uninitialized()
    @Inject(method = ["tick"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/attribute/EnvironmentAttributeProbe;tick(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/phys/Vec3;)V", shift = At.Shift.BEFORE)]) private fun sneakInstantly(ci: CallbackInfo) {
        if (ViaFabricPlusImpl.impl().visuals().sneakInstantly().isActive()) {
            eyeHeightOld = entity.getEyeHeight()
            eyeHeight = eyeHeightOld
        }
    }
    
}
