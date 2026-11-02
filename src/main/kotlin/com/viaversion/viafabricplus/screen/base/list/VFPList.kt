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
package com.viaversion.viafabricplus.screen.base.list

import com.viaversion.viafabricplus.screen.base.list.VFPListEntry
import com.viaversion.viafabricplus.screen.base.list.VFPList

open class VFPList (minecraftClient: net.minecraft.client.Minecraft, width: kotlin.Int, height: kotlin.Int, top: kotlin.Int, bottom: kotlin.Int, entryHeight: kotlin.Int) : net.minecraft.client.gui.components.ObjectSelectionList<VFPListEntry>(minecraftClient, width, height - top - bottom, top, entryHeight) {

    override fun setScrollAmount(scrollY: kotlin.Double) {
        super.setScrollAmount(scrollY)
        updateSlotAmount(scrollAmount()) // Ensure value is clamped
    }
    
    override fun scrollRate(): kotlin.Double {
        val steps: kotlin.Int = kotlin.math.min(this.getItemCount() / VFPList.Companion.ENTRIES_PER_SCROLL_STEP, VFPList.Companion.MAX_SCROLL_STEPS)
        return kotlin.math.max(super.scrollRate(), steps * this.defaultEntryHeight.toDouble())
    }
    
    override fun extractSelection(graphics: net.minecraft.client.gui.GuiGraphicsExtractor, entry: VFPListEntry, outlineColor: kotlin.Int) {
        // Remove selection box
    }
    
    protected open fun updateSlotAmount(amount: kotlin.Double) {
        // To be overridden
    }
    
    companion object {
        private const val ENTRIES_PER_SCROLL_STEP: kotlin.Int = 5
        private const val MAX_SCROLL_STEPS: kotlin.Int = 2
        
    }}
