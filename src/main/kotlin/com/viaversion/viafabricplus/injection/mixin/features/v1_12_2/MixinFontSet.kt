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
package com.viaversion.viafabricplus.injection.mixin.features.v1_12_2

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.viaversion.viafabricplus.features.v1_12_2.BuiltinEmptyGlyph1_12_2
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import com.viaversion.viafabricplus.ViaFabricPlusImpl
import com.viaversion.viafabricplus.features.v1_12_2.RenderableGlyphDiff

@Mixin(net.minecraft.client.gui.font.FontSet::class) 
abstract class MixinFontSet {

    @field:org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField val stitcher: net.minecraft.client.gui.font.GlyphStitcher = uninitialized()
    @field:org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField val missingSelectedGlyphs: net.minecraft.client.gui.font.FontSet.SelectedGlyphs = uninitialized()
    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$blankBakedGlyph1_12_2`: net.minecraft.client.gui.font.glyphs.BakedGlyph = uninitialized()
    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$blankBakedGlyphPair1_12_2`: net.minecraft.client.gui.font.FontSet.SelectedGlyphs = uninitialized()
    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$obfuscatedLookup`: kotlin.Boolean = false
    
    @Inject(method = ["resetTextures"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/gui/font/glyphs/SpecialGlyphs;bake(Lnet/minecraft/client/gui/font/GlyphStitcher;)Lnet/minecraft/client/gui/font/glyphs/BakedSheetGlyph;", ordinal = 0)]) private fun bakeBlankGlyph1_12_2(ci: CallbackInfo) {
        this.`viaFabricPlus$blankBakedGlyph1_12_2` = BuiltinEmptyGlyph1_12_2.INSTANCE.bake(this.stitcher)
        this.`viaFabricPlus$blankBakedGlyphPair1_12_2` = net.minecraft.client.gui.font.FontSet.SelectedGlyphs(java.util.function.Supplier {this.`viaFabricPlus$blankBakedGlyph1_12_2`}, java.util.function.Supplier {this.`viaFabricPlus$blankBakedGlyph1_12_2`})
    }
    
    @Inject(method = ["getGlyph"], at = [At("RETURN")], cancellable = true) private fun filterBakedGlyph(codepoint: kotlin.Int, cir: CallbackInfoReturnable<net.minecraft.client.gui.font.FontSet.SelectedGlyphs>) {
        if (this.`viaFabricPlus$shouldBeInvisible`(codepoint)) {
            if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)) {
                cir.setReturnValue(`viaFabricPlus$blankBakedGlyphPair1_12_2`)
            } else {
                cir.setReturnValue(missingSelectedGlyphs)
            }
        }
    }
    
    @Inject(method = ["computeGlyphInfo"], at = [At("RETURN")], cancellable = true) private fun fixBlankGlyph1_12_2(codepoint: kotlin.Int, cir: CallbackInfoReturnable<net.minecraft.client.gui.font.FontSet.SelectedGlyphs>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)) {
            val glyphPair: net.minecraft.client.gui.font.FontSet.SelectedGlyphs = cir.getReturnValue()
            cir.setReturnValue(if (glyphPair === this.missingSelectedGlyphs) this.`viaFabricPlus$blankBakedGlyphPair1_12_2` else glyphPair)
        }
    }
    
     // Ignore obfuscated texts in the character filter as obfuscated text uses *all* characters, even those not existing in the current target version.
    @Inject(method = ["getRandomGlyph"], at = [At("HEAD")]) private fun pauseCharacterFiltering(random: net.minecraft.util.RandomSource, width: kotlin.Int, cir: CallbackInfoReturnable<net.minecraft.client.gui.font.glyphs.BakedGlyph>) {
        `viaFabricPlus$obfuscatedLookup` = true
    }
    
    @Inject(method = ["getGlyph"], at = [At("RETURN")]) private fun resumeCharacterFiltering(codepoint: kotlin.Int, cir: CallbackInfoReturnable<net.minecraft.client.gui.font.glyphs.BakedGlyph>) {
        `viaFabricPlus$obfuscatedLookup` = false
    }
    
    @org.spongepowered.asm.mixin.Unique private fun `viaFabricPlus$shouldBeInvisible`(codePoint: kotlin.Int): kotlin.Boolean {
        if (!`viaFabricPlus$obfuscatedLookup` && ViaFabricPlusImpl.impl().advanced().filterNonExistingGlyphs().isActive()) {
            return (stitcher.texturePrefix == net.minecraft.client.Minecraft.DEFAULT_FONT) && !RenderableGlyphDiff.isGlyphRenderable(codePoint)
        } else {
            return false
        }
    }
    
}
