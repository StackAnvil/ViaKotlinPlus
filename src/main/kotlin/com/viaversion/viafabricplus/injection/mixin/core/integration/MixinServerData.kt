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
package com.viaversion.viafabricplus.injection.mixin.core.integration

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import com.viaversion.viafabricplus.injection.access.core.IServerData
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(net.minecraft.client.multiplayer.ServerData::class) 
abstract class MixinServerData : IServerData {

    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$forcedVersion`: com.viaversion.viaversion.api.protocol.version.ProtocolVersion = uninitialized()
    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$passedDirectConnectScreen`: kotlin.Boolean = false
    
    @org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$translatingVersion`: com.viaversion.viaversion.api.protocol.version.ProtocolVersion = uninitialized()
    @Inject(method = ["write"], at = [At("TAIL")]) private fun saveForcedVersion(cir: CallbackInfoReturnable<net.minecraft.nbt.CompoundTag>, @com.llamalad7.mixinextras.sugar.Local(name = ["tag"]) tag: net.minecraft.nbt.CompoundTag) {
        if (`viaFabricPlus$forcedVersion` != null) {
            tag.putString("viafabricplus_forcedversion", `viaFabricPlus$forcedVersion`.getName())
        }
    }
    
    @Inject(method = ["copyNameIconFrom"], at = [At("RETURN")]) private fun syncForcedVersion(other: net.minecraft.client.multiplayer.ServerData, ci: CallbackInfo) {
        `viaFabricPlus$forceVersion`((other as IServerData).`viaFabricPlus$forcedVersion`())
    }
    
    override fun `viaFabricPlus$forcedVersion`(): com.viaversion.viaversion.api.protocol.version.ProtocolVersion {
        return `viaFabricPlus$forcedVersion`
    }
    
    override fun `viaFabricPlus$forceVersion`(version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion) {
        `viaFabricPlus$forcedVersion` = version
    }
    
    override fun `viaFabricPlus$passedDirectConnectScreen`(): kotlin.Boolean {
        return `viaFabricPlus$passedDirectConnectScreen`
    }
    
    override fun `viaFabricPlus$passDirectConnectScreen`(state: kotlin.Boolean) {
        `viaFabricPlus$passedDirectConnectScreen` = state
    }
    
    override fun `viaFabricPlus$translatingVersion`(): com.viaversion.viaversion.api.protocol.version.ProtocolVersion {
        return `viaFabricPlus$translatingVersion`
    }
    
    override fun `viaFabricPlus$setTranslatingVersion`(version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion) {
        `viaFabricPlus$translatingVersion` = version
    }
    
}
