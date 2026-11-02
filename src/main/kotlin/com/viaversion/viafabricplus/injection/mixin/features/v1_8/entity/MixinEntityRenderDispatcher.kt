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
package com.viaversion.viafabricplus.injection.mixin.features.v1_8.entity

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import com.viaversion.viafabricplus.features.v1_8.BoatRenderer1_8
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(net.minecraft.client.renderer.entity.EntityRenderDispatcher::class) 
abstract class MixinEntityRenderDispatcher {

    @org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$boatRenderer`: BoatRenderer1_8? = null
    @Inject(method = ["onResourceManagerReload"], at = [At("TAIL")]) private fun createBoatRenderer1_8(resourceManager: net.minecraft.server.packs.resources.ResourceManager, ci: CallbackInfo, @com.llamalad7.mixinextras.sugar.Local(name = ["context"]) context: net.minecraft.client.renderer.entity.EntityRendererProvider.Context) {
        `viaFabricPlus$boatRenderer` = try {
            BoatRenderer1_8(context)
        } catch (ignored: IllegalArgumentException) {
            null
        }
    }
    
    @Inject(method = ["getRenderer(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;)Lnet/minecraft/client/renderer/entity/EntityRenderer;"], at = [At("HEAD")], cancellable = true) private fun <S : net.minecraft.client.renderer.entity.state.EntityRenderState>useBoatRenderer1_8(entityRenderState: S, cir: CallbackInfoReturnable<net.minecraft.client.renderer.entity.EntityRenderer<*, in S>>) {
        val renderer = `viaFabricPlus$boatRenderer`
        if (renderer != null && com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8) && entityRenderState is net.minecraft.client.renderer.entity.state.BoatRenderState) {
            cir.setReturnValue(renderer as net.minecraft.client.renderer.entity.EntityRenderer<*, in S>)
        }
    }
    
}
