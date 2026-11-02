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
import com.viaversion.viafabricplus.injection.access.v1_12_2.ISDLEventHandler
import org.spongepowered.asm.mixin.Final
import java.util.concurrent.ConcurrentLinkedQueue
import org.spongepowered.asm.mixin.injection.At
import org.lwjgl.sdl.SDL_Event
import com.viaversion.viafabricplus.injection.mixin.features.v1_12_2.MixinSDLEventHandler

@Mixin(com.mojang.blaze3d.platform.SDLEventHandler::class) 
abstract class MixinSDLEventHandler : ISDLEventHandler {

    @field:org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField val minecraft: net.minecraft.client.Minecraft = uninitialized()
    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField val `viaFabricPlus$pendingScreenEvents`: java.util.Queue<java.lang.Runnable> = ConcurrentLinkedQueue<java.lang.Runnable>()
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["handleTextInputEvent", "handleMouseButtonEvent", "handleMouseWheelEvent"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;execute(Ljava/lang/Runnable;)V")]) private fun storeEvent(instance: net.minecraft.client.Minecraft, runnable: java.lang.Runnable) {
        if (this.`viaFabricPlus$shouldStoreEvent`()) {
            this.`viaFabricPlus$pendingScreenEvents`.offer(runnable)
        } else {
            instance.execute(runnable)
        }
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["handleKeyEvent"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;execute(Ljava/lang/Runnable;)V")]) private fun storeKeyEvent(instance: net.minecraft.client.Minecraft, runnable: java.lang.Runnable, @com.llamalad7.mixinextras.sugar.Local(argsOnly = true) event: SDL_Event, @com.llamalad7.mixinextras.sugar.Local action: kotlin.Int, @com.llamalad7.mixinextras.sugar.Local key: net.minecraft.client.input.KeyEvent) {
        if (this.`viaFabricPlus$shouldStoreEvent`()) {
            val handle: kotlin.Long = MixinSDLEventHandler.Companion.getWindowHandle(event)
            this.`viaFabricPlus$pendingScreenEvents`.offer(java.lang.Runnable {this.minecraft.keyboardHandler.keyPress(handle, action, key)})
        } else {
            instance.execute(runnable)
        }
    }
    
    @org.spongepowered.asm.mixin.Unique private fun `viaFabricPlus$shouldStoreEvent`(): kotlin.Boolean {
        return this.minecraft.getConnection() != null && this.minecraft.gui.screen() != null && com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)
    }
    
    override fun `viaFabricPlus$getPendingScreenEvents`(): java.util.Queue<java.lang.Runnable> {
        return this.`viaFabricPlus$pendingScreenEvents`
    }
    
    private companion object {
        @JvmStatic @org.spongepowered.asm.mixin.Shadow fun getWindowHandle(event: SDL_Event): kotlin.Long {
            throw java.lang.UnsupportedOperationException("Implemented via mixin")
        }
        
    }}
