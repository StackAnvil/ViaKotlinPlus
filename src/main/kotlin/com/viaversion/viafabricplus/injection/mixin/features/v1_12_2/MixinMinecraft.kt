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
import org.objectweb.asm.Opcodes
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.viaversion.viafabricplus.injection.access.v1_12_2.ISDLEventHandler

@Mixin(net.minecraft.client.Minecraft::class) 
abstract class MixinMinecraft {

    @org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField val sdlEventHandler: com.mojang.blaze3d.platform.SDLEventHandler = uninitialized()
    @Inject(method = ["tick"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;screen()Lnet/minecraft/client/gui/screens/Screen;", ordinal = 0)], slice = [org.spongepowered.asm.mixin.injection.Slice(from = At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;missTime:I", ordinal = 0, opcode = Opcodes.PUTFIELD), to = At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;fillReportDetails(Lnet/minecraft/CrashReport;)Lnet/minecraft/CrashReportCategory;"))]) private fun processInputQueues(ci: CallbackInfo) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)) {
            val inputEvents: java.util.Queue<java.lang.Runnable> = (this.sdlEventHandler as ISDLEventHandler).`viaFabricPlus$getPendingScreenEvents`()
            while (!inputEvents.isEmpty()) inputEvents.poll().run()
        }
    }
    
}
