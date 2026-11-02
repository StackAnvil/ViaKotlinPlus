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
package com.viaversion.viafabricplus.injection.mixin.features.c0_28

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.objectweb.asm.Opcodes
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.viaversion.viafabricplus.ViaFabricPlusImpl

@Mixin(net.minecraft.client.model.HumanoidModel::class) 
abstract class MixinHumanoidModel<T : net.minecraft.client.renderer.entity.state.HumanoidRenderState> {

    @org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField var rightArm: net.minecraft.client.model.geom.ModelPart = uninitialized()
    @org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField var leftArm: net.minecraft.client.model.geom.ModelPart = uninitialized()
    @Inject(method = ["setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V"], at = [At(value = "FIELD", target = "Lnet/minecraft/client/model/geom/ModelPart;zRot:F", ordinal = 1, shift = At.Shift.AFTER, opcode = Opcodes.PUTFIELD)]) private fun addOldWalkAnimation(state: T, ci: CallbackInfo) {
        if (ViaFabricPlusImpl.impl().visuals().oldWalkingAnimation().isActive()) {
            val limbSwingAnimationProgress: kotlin.Float = state.walkAnimationPos
            val limbSwingAmplitude: kotlin.Float = state.walkAnimationSpeed
            
            this.rightArm.xRot = net.minecraft.util.Mth.cos((limbSwingAnimationProgress * 0.6662f + 3.1415927f).toDouble()) * 2.0f * limbSwingAmplitude
            this.rightArm.zRot = (net.minecraft.util.Mth.cos((limbSwingAnimationProgress * 0.2312f).toDouble()) + 1.0f) * 1.0f * limbSwingAmplitude
            
            this.leftArm.xRot = net.minecraft.util.Mth.cos((limbSwingAnimationProgress * 0.6662f).toDouble()) * 2.0f * limbSwingAmplitude
            this.leftArm.zRot = (net.minecraft.util.Mth.cos((limbSwingAnimationProgress * 0.2812f).toDouble()) - 1.0f) * 1.0f * limbSwingAmplitude
        }
    }
    
}
