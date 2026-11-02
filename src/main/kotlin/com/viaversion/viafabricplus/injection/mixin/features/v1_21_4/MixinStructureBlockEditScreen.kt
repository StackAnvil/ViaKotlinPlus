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
package com.viaversion.viafabricplus.injection.mixin.features.v1_21_4

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.Final

@Mixin(net.minecraft.client.gui.screens.inventory.StructureBlockEditScreen::class) 
abstract class MixinStructureBlockEditScreen {

    @org.spongepowered.asm.mixin.Shadow 
    @JvmField val strictButton: net.minecraft.client.gui.components.CycleButton<kotlin.Boolean> = uninitialized()
    @Inject(method = ["updateMode"], at = [At("RETURN")]) private fun hideStrictButton(mode: net.minecraft.world.level.block.state.properties.StructureMode, ci: CallbackInfo) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_4)) {
            strictButton.visible = false
        }
    }
    
    @com.llamalad7.mixinextras.injector.v2.WrapWithCondition(method = ["extractRenderState"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V")]) private fun hideStrictText(instance: net.minecraft.client.gui.GuiGraphicsExtractor, font: net.minecraft.client.gui.Font, str: net.minecraft.network.chat.Component, x: kotlin.Int, y: kotlin.Int, color: kotlin.Int): kotlin.Boolean {
        if (str === com.viaversion.viafabricplus.injection.mixin.features.v1_21_4.MixinStructureBlockEditScreen.Companion.STRICT_LABEL) {
            return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_4)
        } else {
            return true
        }
    }
    
    private companion object {
        @field:org.spongepowered.asm.mixin.Shadow @Final 
        @JvmField val STRICT_LABEL: net.minecraft.network.chat.Component = uninitialized()
    }}
