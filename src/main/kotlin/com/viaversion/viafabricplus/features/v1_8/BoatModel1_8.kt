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

class BoatModel1_8 (root: net.minecraft.client.model.geom.ModelPart) : net.minecraft.client.model.EntityModel<net.minecraft.client.renderer.entity.state.BoatRenderState>(root) {

    override fun setupAnim(state: net.minecraft.client.renderer.entity.state.BoatRenderState) {
    }
    
    companion object {
        val MODEL_LAYER: net.minecraft.client.model.geom.ModelLayerLocation = net.minecraft.client.model.geom.ModelLayerLocation(net.minecraft.resources.Identifier.fromNamespaceAndPath("viafabricplus", "boat1_8"), "main")
        
        val texturedModelData: net.minecraft.client.model.geom.builders.LayerDefinition
get() {
            val modelData: net.minecraft.client.model.geom.builders.MeshDefinition = net.minecraft.client.model.geom.builders.MeshDefinition()
            val root: net.minecraft.client.model.geom.builders.PartDefinition = modelData.getRoot()
            val width: kotlin.Float = 24f
            val wallHeight: kotlin.Float = 6f
            val baseWidth: kotlin.Float = 20f
            val pivotY: kotlin.Float = 4f
            root.addOrReplaceChild("bottom", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 8).addBox(-width / 2, -baseWidth / 2 + 2, -3f, width, baseWidth - 4, 4f), net.minecraft.client.model.geom.PartPose.offsetAndRotation(0f, pivotY, 0f, java.lang.Math.PI.toFloat() / 2, 0f, 0f))
            root.addOrReplaceChild("back", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 0).addBox(-width / 2 + 2, -wallHeight - 1, -1f, width - 4, wallHeight, 2f), net.minecraft.client.model.geom.PartPose.offsetAndRotation(-width / 2 + 1, pivotY, 0f, 0f, java.lang.Math.PI.toFloat() * 1.5f, 0f))
            root.addOrReplaceChild("front", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 0).addBox(-width / 2 + 2, -wallHeight - 1, -1f, width - 4, wallHeight, 2f), net.minecraft.client.model.geom.PartPose.offsetAndRotation(width / 2 - 1, pivotY, 0f, 0f, java.lang.Math.PI.toFloat() / 2, 0f))
            root.addOrReplaceChild("right", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 0).addBox(-width / 2 + 2, -wallHeight - 1, -1f, width - 4, wallHeight, 2f), net.minecraft.client.model.geom.PartPose.offsetAndRotation(0f, pivotY, -baseWidth / 2 + 1, 0f, java.lang.Math.PI.toFloat(), 0f))
            root.addOrReplaceChild("left", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 0).addBox(-width / 2 + 2, -wallHeight - 1, -1f, width - 4, wallHeight, 2f), net.minecraft.client.model.geom.PartPose.offset(0f, pivotY, baseWidth / 2 - 1))
            return net.minecraft.client.model.geom.builders.LayerDefinition.create(modelData, 64, 32)
        }
        
    }}
