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
package com.viaversion.viafabricplus.injection.mixin.features.v26_2

import org.spongepowered.asm.mixin.Mixin
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
import org.spongepowered.asm.mixin.injection.At

@Mixin(net.minecraft.client.renderer.MapRenderer::class) 
abstract class MixinMapRenderer {

    @WrapOperation(method = ["extractRenderState"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MapRenderer;extractDecorationRenderState(Lnet/minecraft/world/level/saveddata/maps/MapDecoration;)Lnet/minecraft/client/renderer/state/MapRenderState\$MapDecorationRenderState;")]) private fun rotateOffMapDecorations(instance: net.minecraft.client.renderer.MapRenderer, decoration: net.minecraft.world.level.saveddata.maps.MapDecoration, original: com.llamalad7.mixinextras.injector.wrapoperation.Operation<net.minecraft.client.renderer.state.MapRenderState.MapDecorationRenderState>, @com.llamalad7.mixinextras.sugar.Local(argsOnly = true) mapData: net.minecraft.world.level.saveddata.maps.MapItemSavedData): net.minecraft.client.renderer.state.MapRenderState.MapDecorationRenderState {
        val state: net.minecraft.client.renderer.state.MapRenderState.MapDecorationRenderState = original.call(instance, decoration)
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v26_2)) {
            // 26.2 servers always sent no rotation for players outside the map, 26.3 renders those as an arrow and uses the player rotation
            val type: net.minecraft.world.level.saveddata.maps.MapDecorationType = decoration.type().value()
            if (type === net.minecraft.world.level.saveddata.maps.MapDecorationTypes.PLAYER_OFF_MAP.value() || type === net.minecraft.world.level.saveddata.maps.MapDecorationTypes.PLAYER_OFF_LIMITS.value()) {
                val player: net.minecraft.client.player.LocalPlayer = net.minecraft.client.Minecraft.getInstance().player
                state.rot = mapData.calculateRotation(player.level(), net.minecraft.util.Mth.wrapDegrees(player.getYRot()).toDouble())
            }
        }
        return state
    }
    
}
