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
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(net.minecraft.world.scores.PlayerTeam::class) 
abstract class MixinPlayerTeam {

    @org.spongepowered.asm.mixin.Shadow 
    @JvmField val playerPrefix: net.minecraft.network.chat.Component = uninitialized()
    @org.spongepowered.asm.mixin.Shadow 
    @JvmField val playerSuffix: net.minecraft.network.chat.Component = uninitialized()
    @Inject(method = ["getFormattedName(Lnet/minecraft/network/chat/Component;)Lnet/minecraft/network/chat/MutableComponent;"], at = [At("HEAD")], cancellable = true) private fun decorateName1_12_2(teamMemberName: net.minecraft.network.chat.Component, cir: CallbackInfoReturnable<net.minecraft.network.chat.MutableComponent>) {
        // All components were legacy strings prior to 1.13, meaning their styles are not separated but used across the whole component.
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)) {
            val prefixStyle: net.minecraft.network.chat.Style = `viaFabricPlus$getLastStyle`(this.playerPrefix)
            val nameWithStyle: net.minecraft.network.chat.Component = `viaFabricPlus$fillStyle`(teamMemberName, prefixStyle)
            val nameStyle: net.minecraft.network.chat.Style = `viaFabricPlus$getLastStyle`(nameWithStyle)
            
            cir.setReturnValue(net.minecraft.network.chat.Component.empty()
            .append(this.playerPrefix)
            .append(nameWithStyle)
            .append(`viaFabricPlus$fillStyle`(this.playerSuffix, nameStyle))
            )
        }
    }
    
    @org.spongepowered.asm.mixin.Unique private fun `viaFabricPlus$getLastStyle`(text: net.minecraft.network.chat.Component): net.minecraft.network.chat.Style {
        for (i in text.getSiblings().indices.reversed()) {
            val sibling: net.minecraft.network.chat.Component = text.getSiblings().get(i)
            if (sibling.getStyle() !== net.minecraft.network.chat.Style.EMPTY) {
                return sibling.getStyle()
            }
        }
        
        return text.getStyle()
    }
    
    @org.spongepowered.asm.mixin.Unique private fun `viaFabricPlus$fillStyle`(text: net.minecraft.network.chat.Component, style: net.minecraft.network.chat.Style): net.minecraft.network.chat.Component {
        if (text.getStyle() !== net.minecraft.network.chat.Style.EMPTY) {
            return text
        } else {
            return text.copy().withStyle(style)
        }
    }
    
}
