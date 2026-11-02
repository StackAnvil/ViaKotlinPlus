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
package com.viaversion.viafabricplus.injection.mixin.features.v1_12_2.movement

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(net.minecraft.world.entity.Entity::class) 
abstract class MixinEntity {

    @org.spongepowered.asm.mixin.injection.Redirect(method = ["getFluidInteractionBox"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/phys/AABB;deflate(D)Lnet/minecraft/world/phys/AABB;")]) private fun inflate(instance: net.minecraft.world.phys.AABB, amount: kotlin.Double): net.minecraft.world.phys.AABB {
        var instance: net.minecraft.world.phys.AABB = instance
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)) {
            instance = instance.inflate(0.0, -0.4, 0.0)
        }
        
        return instance.deflate(amount)
    }
    
    @Inject(method = ["setSwimming"], at = [At("HEAD")], cancellable = true) private fun cancelSwimming(swimming: kotlin.Boolean, ci: CallbackInfo) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2) && swimming) {
            ci.cancel()
        }
    }
    
    private companion object {
        @JvmStatic @Inject(method = ["calculateViewVector(FF)Lnet/minecraft/world/phys/Vec3;"], at = [At("HEAD")], cancellable = true) fun revertCalculation(xRot: kotlin.Float, yRot: kotlin.Float, cir: CallbackInfoReturnable<net.minecraft.world.phys.Vec3>) {
            if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)) {
                cir.setReturnValue(net.minecraft.world.phys.Vec3.directionFromRotation(xRot, yRot))
            }
        }
        
    }}
