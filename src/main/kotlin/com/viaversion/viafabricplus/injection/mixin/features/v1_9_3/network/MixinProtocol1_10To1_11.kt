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
package com.viaversion.viafabricplus.injection.mixin.features.v1_9_3.network

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.ModifyConstant

@Mixin(targets = ["com.viaversion.viaversion.protocols.v1_10to1_11.Protocol1_10To1_11$6"], remap = false) 
class MixinProtocol1_10To1_11 {
private companion object {
        @JvmStatic @ModifyConstant(method = ["lambda\$register$0"], constant = [org.spongepowered.asm.mixin.injection.Constant(intValue = 100)]) fun changeMaxChatLength(constant: kotlin.Int): kotlin.Int {
            return com.viaversion.viafabricplus.ViaFabricPlus.api().limitations().maxChatLength()
        }
    
    }
}
