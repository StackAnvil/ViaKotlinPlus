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
package com.viaversion.viafabricplus.injection.mixin.features.v1_7_6.screen

import org.spongepowered.asm.mixin.Mixin
import com.viaversion.viafabricplus.injection.access.v1_7_6.IPlayerInfo
import com.viaversion.viafabricplus.features.v1_7_6.LegacyTabList
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(net.minecraft.client.multiplayer.PlayerInfo::class) 
abstract class MixinPlayerInfo : IPlayerInfo {

    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$index`: kotlin.Int = 0

    @Inject(method = ["<init>"], at = [At("RETURN")])
    private fun initIndex(profile: com.mojang.authlib.GameProfile, secureChat: kotlin.Boolean, ci: CallbackInfo) {
        `viaFabricPlus$index` = LegacyTabList.globalTablistIndex++
    }
    
    override fun `viaFabricPlus$getIndex`(): kotlin.Int {
        return `viaFabricPlus$index`
    }
    
}
