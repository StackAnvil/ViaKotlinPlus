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

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.viaversion.viafabricplus.ViaFabricPlusImpl

@Mixin(net.minecraft.client.renderer.entity.LivingEntityRenderer::class) 
abstract class MixinLivingEntityRenderer {

    @Inject(method = ["submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V"], at = [At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V", ordinal = 1)]) private fun <S : net.minecraft.client.renderer.entity.state.LivingEntityRenderState>addOldWalkAnimation(state: S, poseStack: com.mojang.blaze3d.vertex.PoseStack, submitNodeCollector: net.minecraft.client.renderer.SubmitNodeCollector, camera: net.minecraft.client.renderer.state.level.CameraRenderState, ci: CallbackInfo) {
        if (ViaFabricPlusImpl.impl().visuals().oldWalkingAnimation().isActive()) {
            val limbSwingAnimationProgress: kotlin.Float = state.walkAnimationPos
            val limbSwingAmplitude: kotlin.Float = state.walkAnimationSpeed
            
            poseStack.translate(0.0, -kotlin.math.abs(kotlin.math.cos((limbSwingAnimationProgress * 0.6662f).toDouble())) * 0.3125f * limbSwingAmplitude, 0.0)
        }
    }
    
}
