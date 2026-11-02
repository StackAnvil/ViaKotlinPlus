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
package com.viaversion.viafabricplus.features.v1_12_2

import com.viaversion.viafabricplus.features.v1_12_2.LanguageUtil

object LanguageUtil {
    private const val NON_ASCII_THRESHOLD: kotlin.Int = 256
    
    fun isUnicodeFont1_12_2(translations: kotlin.collections.MutableMap<kotlin.String, kotlin.String>): kotlin.Boolean {
        var nonAsciiCharacters: kotlin.Int = 0
        var totalCharacters: kotlin.Int = 0
        
        for (value in translations.values) {
            totalCharacters += value.length
            for (i in 0 ..< value.length) {
                if (value.get(i).code >= LanguageUtil.NON_ASCII_THRESHOLD) {
                    nonAsciiCharacters++
                }
            }
        }
        
        return nonAsciiCharacters.toFloat() / totalCharacters > 0.1
    }
    
}
