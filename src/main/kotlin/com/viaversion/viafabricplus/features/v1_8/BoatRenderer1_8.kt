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
package com.viaversion.viafabricplus.features.v1_8

import com.viaversion.viafabricplus.features.v1_8.BoatRenderer1_8
import com.viaversion.viafabricplus.features.v1_8.BoatModel1_8

class BoatRenderer1_8 (ctx: net.minecraft.client.renderer.entity.EntityRendererProvider.Context) : net.minecraft.client.renderer.entity.AbstractBoatRenderer(ctx, BoatRenderer1_8.Companion.TEXTURE) {

    private val model: BoatModel1_8
    
    init {
        shadowRadius = 0.5f
        model = BoatModel1_8(ctx.bakeLayer(BoatModel1_8.MODEL_LAYER))
    }
    
    override fun model(): net.minecraft.client.model.EntityModel<net.minecraft.client.renderer.entity.state.BoatRenderState> {
        return this.model
    }
    
    override fun submit(state: net.minecraft.client.renderer.entity.state.BoatRenderState, matrices: com.mojang.blaze3d.vertex.PoseStack, submitNodeCollector: net.minecraft.client.renderer.SubmitNodeCollector, camera: net.minecraft.client.renderer.state.level.CameraRenderState) {
        matrices.pushPose()
        matrices.translate(0.0, 0.25, 0.0)
        matrices.rotateDegrees(com.mojang.math.Axis.YP, 180 - state.yRot)
        
        if (state.hurtTime > 0) {
            matrices.rotateDegrees(com.mojang.math.Axis.XP, net.minecraft.util.Mth.sin(state.hurtTime.toDouble()) * state.hurtTime * state.damageTime / 10 * state.hurtDir)
        }
        
        matrices.scale(-1f, -1f, 1f)
        model.setupAnim(state)
        submitNodeCollector.submitModel<net.minecraft.client.renderer.entity.state.BoatRenderState>(model(), state, matrices, this.texture, state.lightCoords, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, state.outlineColor)
        
        matrices.popPose()
    }
    
    companion object {
        private val TEXTURE: net.minecraft.resources.Identifier = net.minecraft.resources.Identifier.fromNamespaceAndPath("viafabricplus", "textures/boat1_8.png")
    }}
