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
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.viaversion.viafabricplus.injection.mixin.core.integration.MixinDebugScreenEntries
import com.viaversion.viafabricplus.features.global.VFPDebugHudEntry
import java.util.HashMap

@Mixin(net.minecraft.client.gui.components.debug.DebugScreenEntries::class) 
class MixinDebugScreenEntries {
    private companion object {
        @JvmField
        @org.spongepowered.asm.mixin.Mutable
        @org.spongepowered.asm.mixin.Shadow
        @Final
        var PROFILES: kotlin.collections.MutableMap<net.minecraft.client.gui.components.debug.DebugScreenProfile, kotlin.collections.MutableMap<net.minecraft.resources.Identifier, net.minecraft.client.gui.components.debug.DebugScreenEntryStatus>> = uninitialized()

        @JvmStatic
        @org.spongepowered.asm.mixin.Shadow
        fun register(id: net.minecraft.resources.Identifier, entry: net.minecraft.client.gui.components.debug.DebugScreenEntry): net.minecraft.resources.Identifier {
            return uninitialized()
        }

        @JvmStatic
        @Inject(method = ["<clinit>"], at = [At("RETURN")])
        fun addViaFabricPlusEntry(ci: CallbackInfo) {
            val id: net.minecraft.resources.Identifier = register(VFPDebugHudEntry.ID, VFPDebugHudEntry())
        val profiles: kotlin.collections.MutableMap<net.minecraft.client.gui.components.debug.DebugScreenProfile, kotlin.collections.MutableMap<net.minecraft.resources.Identifier, net.minecraft.client.gui.components.debug.DebugScreenEntryStatus>> = HashMap<net.minecraft.client.gui.components.debug.DebugScreenProfile, kotlin.collections.MutableMap<net.minecraft.resources.Identifier, net.minecraft.client.gui.components.debug.DebugScreenEntryStatus>>()
        for (entry in PROFILES.entries) {
            val entries: kotlin.collections.MutableMap<net.minecraft.resources.Identifier, net.minecraft.client.gui.components.debug.DebugScreenEntryStatus> = HashMap<net.minecraft.resources.Identifier, net.minecraft.client.gui.components.debug.DebugScreenEntryStatus>(entry.value)
            if (entry.key == net.minecraft.client.gui.components.debug.DebugScreenProfile.DEFAULT) {
                entries.put(id, net.minecraft.client.gui.components.debug.DebugScreenEntryStatus.IN_OVERLAY)
            }
            profiles.put(entry.key, entries)
        }
        PROFILES = profiles
    }
    
    }
}
