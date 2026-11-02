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
package com.viaversion.viafabricplus.injection.mixin.features.v1_12_2.screen

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(net.minecraft.client.gui.components.CommandSuggestions::class) 
abstract class MixinCommandSuggestions {

    @org.spongepowered.asm.mixin.Shadow 
    @JvmField var suggestions: net.minecraft.client.gui.components.CommandSuggestions.SuggestionsList = uninitialized()
    @org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField val input: net.minecraft.client.gui.components.EditBox = uninitialized()
    @org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField val commandUsage: kotlin.collections.MutableList<net.minecraft.util.FormattedCharSequence> = uninitialized()
    @org.spongepowered.asm.mixin.Shadow abstract fun updateCommandInfo()
    
    @Inject(method = ["formatChat"], at = [At(value = "HEAD")], cancellable = true) private fun disableTextFieldColors(text: kotlin.String, offset: kotlin.Int, cir: CallbackInfoReturnable<net.minecraft.util.FormattedCharSequence>) {
        if (this.`viaFabricPlus$cancelTabComplete`()) {
            cir.setReturnValue(net.minecraft.util.FormattedCharSequence.forward(text, net.minecraft.network.chat.Style.EMPTY))
        }
    }
    
    @Inject(method = ["keyPressed"], at = [At("HEAD")], cancellable = true) private fun handle1_12_2KeyPressed(event: net.minecraft.client.input.KeyEvent, cir: CallbackInfoReturnable<kotlin.Boolean>) {
        if (this.`viaFabricPlus$cancelTabComplete`()) {
            if (event.key() == com.mojang.blaze3d.platform.InputConstants.KEY_TAB && this.suggestions == null) {
                this.updateCommandInfo()
            } else if (this.suggestions != null) {
                if (this.suggestions.keyPressed(event)) {
                    cir.setReturnValue(true)
                    return 
                }
                this.input.setSuggestion(null)
                this.suggestions = uninitialized()
            }
        }
    }
    
    @Inject(method = ["extractRenderState"], at = [At("HEAD")]) private fun clearMessages(graphics: net.minecraft.client.gui.GuiGraphicsExtractor, mouseX: kotlin.Int, mouseY: kotlin.Int, ci: CallbackInfo) {
        if (this.`viaFabricPlus$cancelTabComplete`()) {
            this.commandUsage.clear()
        }
    }
    
    @org.spongepowered.asm.mixin.Unique private fun `viaFabricPlus$cancelTabComplete`(): kotlin.Boolean {
        return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2) && this.input.getValue().startsWith("/")
    }
    
}
