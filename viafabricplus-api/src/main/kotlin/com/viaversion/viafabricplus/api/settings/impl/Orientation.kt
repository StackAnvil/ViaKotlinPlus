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
package com.viaversion.viafabricplus.api.settings.impl

import java.util.Locale

enum class Orientation (positioner: com.viaversion.viafabricplus.api.settings.impl.Orientation.Positioner) : com.viaversion.viafabricplus.api.settings.base.EnumSetting.EnumValue {

    NONE(Positioner { _, _, _ -> }),
    LEFT_TOP(Positioner { button, _, _ -> button.setPosition(5, 5) }),
    RIGHT_TOP(Positioner { button, width, _ -> button.setPosition(width - 98 - 5, 5) }),
    LEFT_BOTTOM(Positioner { button, _, height -> button.setPosition(5, height - 20 - 5) }),
    RIGHT_BOTTOM(Positioner { button, width, height -> button.setPosition(width - 98 - 5, height - 20 - 5) });
    
    val component: net.minecraft.network.chat.Component
    private val positionerValue: com.viaversion.viafabricplus.api.settings.impl.Orientation.Positioner
    
    init {
        this.component = net.minecraft.network.chat.Component.translatable("base.viafabricplus." + this.name.lowercase(Locale.getDefault()))
        this.positionerValue = positioner
    }
    
    fun getPositioner(): com.viaversion.viafabricplus.api.settings.impl.Orientation.Positioner {
        return this.positionerValue
    }
    
    override fun component(): net.minecraft.network.chat.Component {
        return this.component
    }
    
    fun interface Positioner {
    
        fun setPosition(button: net.minecraft.client.gui.components.Button, width: kotlin.Int, height: kotlin.Int)
        
    }
    
}
