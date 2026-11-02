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
package com.viaversion.viafabricplus.injection.mixin.features.v1_20_3.network

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import com.viaversion.viafabricplus.ViaFabricPlusImpl

@Mixin(net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl::class) 
abstract class MixinClientCommonPacketListenerImpl {

    @com.llamalad7.mixinextras.injector.v2.WrapWithCondition(method = ["onPacketError"], at = [At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;disconnect(Lnet/minecraft/network/DisconnectionDetails;)V")]) private fun dontDisconnectOnPacketException(instance: net.minecraft.network.Connection, details: net.minecraft.network.DisconnectionDetails): kotlin.Boolean {
        return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20_3)
    }
    
    @Inject(method = ["storeDisconnectionReport"], at = [At("HEAD")], cancellable = true) private fun dontCreatePacketErrorCrashReports(cir: CallbackInfoReturnable<java.util.Optional<java.nio.file.Path>>) {
        if (ViaFabricPlusImpl.impl().advanced().dontCreatePacketErrorCrashReports().isActive()) {
            cir.setReturnValue(java.util.Optional.empty<java.nio.file.Path>())
        }
    }
    
}
