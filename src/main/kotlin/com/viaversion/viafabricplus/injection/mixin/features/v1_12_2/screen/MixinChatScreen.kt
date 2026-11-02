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
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.ModifyArg

@Mixin(value = [net.minecraft.client.gui.screens.ChatScreen::class]) 
abstract class MixinChatScreen {

    @org.spongepowered.asm.mixin.Shadow 
    @JvmField var input: net.minecraft.client.gui.components.EditBox = uninitialized()
    @org.spongepowered.asm.mixin.Shadow 
    @JvmField var initial: kotlin.String = uninitialized()
    @org.spongepowered.asm.mixin.Shadow 
    @JvmField val commandSuggestions: net.minecraft.client.gui.components.CommandSuggestions = uninitialized()
    @com.llamalad7.mixinextras.injector.v2.WrapWithCondition(method = ["init"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/EditBox;setValue(Ljava/lang/String;)V")]) fun moveSetTextDown(instance: net.minecraft.client.gui.components.EditBox, value: kotlin.String): kotlin.Boolean {
        return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)
    }
    
    @Inject(method = ["init"], at = [At("RETURN")]) private fun moveSetTextDown(ci: CallbackInfo) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)) {
            this.input.setValue(this.initial)
            this.commandSuggestions.updateCommandInfo()
        }
    }
    
    @ModifyArg(method = ["onEdited"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/CommandSuggestions;setAllowSuggestions(Z)V")], index = 0) private fun fixCommandKey(windowActive: kotlin.Boolean): kotlin.Boolean {
        val instance: kotlin.String = this.input.getValue()
        if (this.`viaFabricPlus$keepTabComplete`()) {
            return true
        } else {
            return !instance.isEmpty()
        }
    }
    
    @com.llamalad7.mixinextras.injector.v2.WrapWithCondition(method = ["onEdited"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/CommandSuggestions;updateCommandInfo()V")]) private fun disableAutoTabComplete(instance: net.minecraft.client.gui.components.CommandSuggestions): kotlin.Boolean {
        return this.`viaFabricPlus$keepTabComplete`()
    }
    
    @org.spongepowered.asm.mixin.Unique private fun `viaFabricPlus$keepTabComplete`(): kotlin.Boolean {
        return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2) || !this.input.getValue().startsWith("/")
    }
    
}
