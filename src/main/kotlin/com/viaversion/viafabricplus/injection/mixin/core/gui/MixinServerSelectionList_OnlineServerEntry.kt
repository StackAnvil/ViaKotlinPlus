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
package com.viaversion.viafabricplus.injection.mixin.core.gui

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Final
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
import org.spongepowered.asm.mixin.injection.At
import com.viaversion.viafabricplus.ViaFabricPlusImpl
import com.viaversion.viafabricplus.injection.access.core.IServerData

@Mixin(net.minecraft.client.gui.screens.multiplayer.ServerSelectionList.OnlineServerEntry::class) 
abstract class MixinServerSelectionList_OnlineServerEntry {

    @org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField val serverData: net.minecraft.client.multiplayer.ServerData = uninitialized()
    @WrapOperation(method = ["extractContent"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;setTooltipForNextFrame(Lnet/minecraft/network/chat/Component;II)V")]) private fun drawTranslatingState(instance: net.minecraft.client.gui.GuiGraphicsExtractor, component: net.minecraft.network.chat.Component, x: kotlin.Int, y: kotlin.Int, original: com.llamalad7.mixinextras.injector.wrapoperation.Operation<java.lang.Void>) {
        val tooltips: kotlin.collections.MutableList<net.minecraft.network.chat.Component> = java.util.ArrayList<net.minecraft.network.chat.Component>()
        tooltips.add(component)
        if (ViaFabricPlusImpl.impl().options().showAdvertisedServerVersion().isActive()) {
            tooltips.add(net.minecraft.network.chat.Component.translatable("base.viafabricplus.target_version", (serverData as IServerData).`viaFabricPlus$translatingVersion`()))
            tooltips.add(net.minecraft.network.chat.Component.translatable("base.viafabricplus.server_version", serverData.version.getString() + " (" + serverData.protocol + ")"))
        }
        instance.setTooltipForNextFrame(com.google.common.collect.Lists.transform<net.minecraft.network.chat.Component, net.minecraft.util.FormattedCharSequence>(tooltips, com.google.common.base.Function {obj: net.minecraft.network.chat.Component -> obj.getVisualOrderText()}), x, y)
    }
    
}
