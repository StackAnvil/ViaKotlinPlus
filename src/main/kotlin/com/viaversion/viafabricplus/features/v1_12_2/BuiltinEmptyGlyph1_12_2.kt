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
package com.viaversion.viafabricplus.features.v1_12_2

import com.viaversion.viafabricplus.features.v1_12_2.BuiltinEmptyGlyph1_12_2

enum class BuiltinEmptyGlyph1_12_2 : com.mojang.blaze3d.font.GlyphInfo {
    INSTANCE;
    
    override fun getAdvance(): kotlin.Float {
        return BuiltinEmptyGlyph1_12_2.Companion.WIDTH.toFloat()
    }
    
    fun bake(baker: net.minecraft.client.gui.font.GlyphStitcher): net.minecraft.client.gui.font.glyphs.BakedSheetGlyph {
        return baker.stitch(this, object : com.mojang.blaze3d.font.GlyphBitmap {
        
            override fun getPixelWidth(): kotlin.Int {
                return BuiltinEmptyGlyph1_12_2.Companion.WIDTH
            }
            
            override fun getPixelHeight(): kotlin.Int {
                return BuiltinEmptyGlyph1_12_2.Companion.HEIGHT
            }
            
            override fun upload(x: kotlin.Int, y: kotlin.Int, texture: com.mojang.renderpearl.api.textures.GpuTexture) {
            }
            
            override fun getOversample(): kotlin.Float {
                return 1f
            }
            
            override fun isColored(): kotlin.Boolean {
                return true
            }
        })
    }
    
    companion object {
        private const val WIDTH: kotlin.Int = 0
        private const val HEIGHT: kotlin.Int = 8
        
    }}
