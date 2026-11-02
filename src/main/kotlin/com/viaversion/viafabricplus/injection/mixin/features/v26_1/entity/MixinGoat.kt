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
package com.viaversion.viafabricplus.injection.mixin.features.v26_1.entity

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(net.minecraft.world.entity.animal.goat.Goat::class) 
abstract class MixinGoat (type: net.minecraft.world.entity.EntityType<out net.minecraft.world.entity.animal.Animal>, level: net.minecraft.world.level.Level) : net.minecraft.world.entity.animal.Animal(type, level) {

    @org.spongepowered.asm.mixin.Shadow abstract override fun getAgeScale(): kotlin.Float
    
    @Inject(method = ["getDefaultDimensions"], at = [At("HEAD")], cancellable = true) private fun replaceDimensions(pose: net.minecraft.world.entity.Pose, cir: CallbackInfoReturnable<net.minecraft.world.entity.EntityDimensions>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v26_1)) {
            cir.setReturnValue(if (pose == net.minecraft.world.entity.Pose.LONG_JUMPING) com.viaversion.viafabricplus.injection.mixin.features.v26_1.entity.MixinGoat.Companion.`viaFabricPlus$dimensions_r26_1`.scale(this.getAgeScale()) else super.getDefaultDimensions(pose))
        }
    }
    
    private companion object {
        @field:org.spongepowered.asm.mixin.Unique 
        @JvmField val `viaFabricPlus$dimensions_r26_1`: net.minecraft.world.entity.EntityDimensions = net.minecraft.world.entity.EntityDimensions.scalable(0.9f, 1.3f).scale(0.7f)
        
    }}
