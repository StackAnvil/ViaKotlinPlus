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
package com.viaversion.viafabricplus.injection.mixin.features.v1_21_4.movement

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At

@Mixin(net.minecraft.world.entity.PositionMoveRotation::class) 
class MixinPositionMoveRotation {
private companion object {
        @JvmStatic @org.spongepowered.asm.mixin.injection.Redirect(method = ["calculateAbsolute"], at = [At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F")]) fun uncapPlayerPitchMovement(value: kotlin.Float, min: kotlin.Float, max: kotlin.Float): kotlin.Float {
            if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_4)) {
                return value
            } else {
                return net.minecraft.util.Mth.clamp(value, min, max)
            }
        }
    
    }
}
