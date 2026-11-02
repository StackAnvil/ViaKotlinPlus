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

import com.viaversion.viafabricplus.protocoltranslator.impl.ViaFabricPlusMappingDataLoader
import com.viaversion.viafabricplus.features.v1_12_2.RenderableGlyphDiff
import com.viaversion.nbt.tag.MixedListTag

object RenderableGlyphDiff {
    private val RENDERABLE_GLYPH_DIFF_LEGACY: it.unimi.dsi.fastutil.ints.Int2ObjectMap<com.viaversion.viaversion.api.protocol.version.ProtocolVersion> = it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap<com.viaversion.viaversion.api.protocol.version.ProtocolVersion>()
    private val RENDERABLE_GLYPH_DIFF: it.unimi.dsi.fastutil.ints.Int2ObjectMap<com.viaversion.viaversion.api.protocol.version.ProtocolVersion> = it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap<com.viaversion.viaversion.api.protocol.version.ProtocolVersion>()
    
    fun init() {
        val glyphs: com.viaversion.nbt.tag.CompoundTag = ViaFabricPlusMappingDataLoader.INSTANCE.loadNBT("renderable-glyphs.nbt")!!
        RenderableGlyphDiff.fill(glyphs.getCompoundTag("legacy"), RenderableGlyphDiff.RENDERABLE_GLYPH_DIFF_LEGACY)
        RenderableGlyphDiff.fill(glyphs.getCompoundTag("unihex"), RenderableGlyphDiff.RENDERABLE_GLYPH_DIFF)
    }
    
    private fun fill(glyphs: com.viaversion.nbt.tag.CompoundTag, map: it.unimi.dsi.fastutil.ints.Int2ObjectMap<com.viaversion.viaversion.api.protocol.version.ProtocolVersion>) {
        for (entry in glyphs) {
            val version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion = com.viaversion.viaversion.api.protocol.version.ProtocolVersion.getClosest(entry.key)!!
            kotlin.checkNotNull(version) {"Unknown protocol version: " + entry.key} 
            
            val list: MixedListTag = entry.value as MixedListTag
            for (i in list) {
                if (i is com.viaversion.nbt.tag.IntTag) {
                    map.put(i.asInt(), version)
                } else if (i is com.viaversion.nbt.tag.CompoundTag) {
                    val from: kotlin.Int = i.getInt("from")
                    val to: kotlin.Int = i.getInt("to")
                    for (j in from .. to) {
                        map.put(j, version)
                    }
                }
            }
        }
    }
    
    fun isGlyphRenderable(codePoint: kotlin.Int): kotlin.Boolean {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20)) { // 1.20 switched to using Unihex as a main font
            return !RenderableGlyphDiff.RENDERABLE_GLYPH_DIFF.containsKey(codePoint) || com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThanOrEqualTo(RenderableGlyphDiff.RENDERABLE_GLYPH_DIFF.get(codePoint))
        } else {
            return RenderableGlyphDiff.RENDERABLE_GLYPH_DIFF_LEGACY.containsKey(codePoint) && com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThanOrEqualTo(RenderableGlyphDiff.RENDERABLE_GLYPH_DIFF_LEGACY.get(codePoint))
        }
    }
    
}
