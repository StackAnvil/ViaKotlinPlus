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
package com.viaversion.viafabricplus.injection.mixin.features.v1_21_7

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import com.viaversion.viafabricplus.injection.access.v1_21_7.IGameTestBlockHighlightRenderer
import org.spongepowered.asm.mixin.Final

@Mixin(net.minecraft.client.renderer.debug.GameTestBlockHighlightRenderer::class) 
abstract class MixinGameTestBlockHighlightRenderer : IGameTestBlockHighlightRenderer {

    @org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField val markers: kotlin.collections.MutableMap<net.minecraft.core.BlockPos, net.minecraft.client.renderer.debug.GameTestBlockHighlightRenderer.Marker> = uninitialized()
    override fun `viaFabricPlus$addMarker`(pos: net.minecraft.core.BlockPos, color: kotlin.Int, message: kotlin.String, duration: kotlin.Int) {
        this.markers.put(pos, net.minecraft.client.renderer.debug.GameTestBlockHighlightRenderer.Marker(color, message, net.minecraft.util.Util.getMillis() + duration))
    }
    
}
