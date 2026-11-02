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
package com.viaversion.viafabricplus.injection.mixin.features.b1_7

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Final
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
import org.spongepowered.asm.mixin.injection.At
import java.util.concurrent.ThreadPoolExecutor
import com.viaversion.viafabricplus.injection.access.core.IServerData
import com.viaversion.viafabricplus.ViaFabricPlusImpl
import org.objectweb.asm.Opcodes
import org.spongepowered.asm.mixin.injection.ModifyArg

@Mixin(net.minecraft.client.gui.screens.multiplayer.ServerSelectionList.OnlineServerEntry::class) 
abstract class MixinServerSelectionList_OnlineServerEntry {

    @field:org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField val serverData: net.minecraft.client.multiplayer.ServerData = uninitialized()
    @field:org.spongepowered.asm.mixin.Mutable @field:org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField val icon: net.minecraft.client.gui.screens.FaviconTexture = uninitialized()
    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$disableServerPinging`: kotlin.Boolean = false
    
    @WrapOperation(method = ["extractContent"], at = [At(value = "INVOKE", target = "Ljava/util/concurrent/ThreadPoolExecutor;submit(Ljava/lang/Runnable;)Ljava/util/concurrent/Future;")]) private fun disableServerPinging(instance: ThreadPoolExecutor, runnable: java.lang.Runnable, original: com.llamalad7.mixinextras.injector.wrapoperation.Operation<java.util.concurrent.Future<*>>): java.util.concurrent.Future<*> {
        var version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion = (serverData as IServerData).`viaFabricPlus$forcedVersion`()
        if (version == null) {
            version = com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion()
        }
        
        `viaFabricPlus$disableServerPinging` = ViaFabricPlusImpl.impl().visuals().disableServerPinging().isActive(version)
        if (`viaFabricPlus$disableServerPinging`) {
            this.serverData.version = net.minecraft.network.chat.Component.nullToEmpty(version.getName()) // Show target version
            return uninitialized()
        }
        return original.call(instance, runnable)
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["extractContent"], at = [At(value = "FIELD", target = "Lnet/minecraft/client/multiplayer/ServerData\$State;INCOMPATIBLE:Lnet/minecraft/client/multiplayer/ServerData\$State;", opcode = Opcodes.GETSTATIC)]) private fun disableServerPinging(): net.minecraft.client.multiplayer.ServerData.State {
        if (`viaFabricPlus$disableServerPinging`) {
            return this.serverData.state() // server version will always be shown (as we don't have a player count anyway)
        } else {
            return net.minecraft.client.multiplayer.ServerData.State.INCOMPATIBLE
        }
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["extractContent"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;split(Lnet/minecraft/network/chat/FormattedText;I)Ljava/util/List;")]) private fun disableServerPinging(instance: net.minecraft.client.gui.Font, input: net.minecraft.network.chat.FormattedText, maxWidth: kotlin.Int): kotlin.collections.MutableList<net.minecraft.util.FormattedCharSequence> {
        if (`viaFabricPlus$disableServerPinging`) { // server label will just show the server address
            return instance.split(net.minecraft.network.chat.Component.nullToEmpty(serverData.ip), maxWidth)
        } else {
            return instance.split(input, maxWidth)
        }
    }
    
    @ModifyArg(method = ["extractContent"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V")], index = 2) private fun disableServerPinging(x: kotlin.Int): kotlin.Int {
        var x: kotlin.Int = x
        if (`viaFabricPlus$disableServerPinging`) { // Move server label to the right (as we remove the ping bar)
            x += com.viaversion.viafabricplus.injection.mixin.features.b1_7.MixinServerSelectionList_OnlineServerEntry.Companion.STATUS_ICON_WIDTH + com.viaversion.viafabricplus.injection.mixin.features.b1_7.MixinServerSelectionList_OnlineServerEntry.Companion.SPACING - 3 /* magical offset */
        }
        return x
    }
    
    @com.llamalad7.mixinextras.injector.v2.WrapWithCondition(method = ["extractContent"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V", ordinal = 0)]) private fun disableServerPinging(instance: net.minecraft.client.gui.GuiGraphicsExtractor, renderPipeline: com.mojang.renderpearl.api.pipeline.RenderPipeline, location: net.minecraft.resources.Identifier, x: kotlin.Int, y: kotlin.Int, width: kotlin.Int, height: kotlin.Int): kotlin.Boolean {
        return !`viaFabricPlus$disableServerPinging` // Remove ping bar
    }
    
    @com.llamalad7.mixinextras.injector.v2.WrapWithCondition(method = ["extractContent"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;setTooltipForNextFrame(Ljava/util/List;II)V")]) private fun disableServerPinging(instance: net.minecraft.client.gui.GuiGraphicsExtractor, formattedCharSequences: kotlin.collections.MutableList<net.minecraft.util.FormattedCharSequence>, x: kotlin.Int, y: kotlin.Int): kotlin.Boolean {
        return !`viaFabricPlus$disableServerPinging` // Remove player list tooltip
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["extractContent"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/FaviconTexture;textureLocation()Lnet/minecraft/resources/Identifier;")]) private fun disableServerPinging(instance: net.minecraft.client.gui.screens.FaviconTexture): net.minecraft.resources.Identifier {
        if (`viaFabricPlus$disableServerPinging`) { // Remove server icon
            return net.minecraft.client.gui.screens.FaviconTexture.MISSING_LOCATION
        } else {
            return this.icon.textureLocation()
        }
    }
    
    @com.llamalad7.mixinextras.injector.v2.WrapWithCondition(method = ["extractContent"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;setTooltipForNextFrame(Lnet/minecraft/network/chat/Component;II)V")]) private fun disableServerPinging(instance: net.minecraft.client.gui.GuiGraphicsExtractor, component: net.minecraft.network.chat.Component, x: kotlin.Int, y: kotlin.Int): kotlin.Boolean {
        return !`viaFabricPlus$disableServerPinging` // Remove ping bar tooltip
    }
    
    private companion object {
        @field:org.spongepowered.asm.mixin.Shadow @Final 
        @JvmField val STATUS_ICON_WIDTH: kotlin.Int = 0
        
        @field:org.spongepowered.asm.mixin.Shadow @Final 
        @JvmField val SPACING: kotlin.Int = 0
        
    }}
