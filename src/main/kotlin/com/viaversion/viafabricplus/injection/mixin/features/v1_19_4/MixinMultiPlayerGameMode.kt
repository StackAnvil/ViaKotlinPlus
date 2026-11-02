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
package com.viaversion.viafabricplus.injection.mixin.features.v1_19_4

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(net.minecraft.client.multiplayer.MultiPlayerGameMode::class) 
abstract class MixinMultiPlayerGameMode {

    @field:org.spongepowered.asm.mixin.Shadow 
    @JvmField val destroyProgress: kotlin.Float = 0f
    
    @Inject(method = ["getDestroyStage"], at = [At("HEAD")], cancellable = true) private fun changeCalculation(cir: CallbackInfoReturnable<kotlin.Int>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_19_4)) {
            cir.setReturnValue((this.destroyProgress * 10.0f).toInt() - 1)
        }
    }
    
}
