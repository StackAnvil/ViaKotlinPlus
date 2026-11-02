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
package com.viaversion.viafabricplus.injection.mixin.core.connection

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.viaversion.viafabricplus.ViaFabricPlusImpl
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslationImpl

@Mixin(net.minecraft.client.Minecraft::class) 
abstract class MixinMinecraft {

    @Inject(method = ["doWorldLoad"], at = [At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;initiateServerboundPlayConnection(Ljava/lang/String;ILnet/minecraft/network/protocol/login/ClientLoginPacketListener;)V")]) private fun disableProtocolTranslator(levelSourceAccess: net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess, packRepository: net.minecraft.server.packs.repository.PackRepository, worldStem: net.minecraft.server.WorldStem, gameRules: java.util.Optional<net.minecraft.world.level.gamerules.GameRules>, newWorld: kotlin.Boolean, ci: CallbackInfo, @com.llamalad7.mixinextras.sugar.Local(name = ["connection"]) connection: net.minecraft.network.Connection) {
        ViaFabricPlusImpl.impl().protocolTranslation().setTargetVersion(ProtocolTranslationImpl.NATIVE_VERSION, true)
        if (connection.isConnected()) {
            ViaFabricPlusImpl.impl().protocolTranslation().injectionPreviousVersionHandler(connection.channel)
        } else {
            connection.pendingActions.add(java.util.function.Consumer {c: net.minecraft.network.Connection -> ViaFabricPlusImpl.impl().protocolTranslation().injectionPreviousVersionHandler(c.channel)})
        }
    }
    
}
