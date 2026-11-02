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
package com.viaversion.viafabricplus.injection.mixin.features.v1_5

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import net.raphimc.vialegacy.api.LegacyProtocolVersion

@Mixin(net.minecraft.client.gui.screens.inventory.AbstractContainerScreen::class) 
abstract class MixinAbstractContainerScreen protected constructor(component: net.minecraft.network.chat.Component) : net.minecraft.client.gui.screens.Screen(component) {

     // TODO: Item is supposed to go in slot on click not where mouse is released
    @Inject(method = ["mouseDragged"], at = [At("HEAD")], cancellable = true) private fun disableItemDragging(event: net.minecraft.client.input.MouseButtonEvent, dx: kotlin.Double, dy: kotlin.Double, cir: CallbackInfoReturnable<kotlin.Boolean>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThan(LegacyProtocolVersion.r1_5tor1_5_1)) {
            cir.setReturnValue(super.mouseDragged(event, dx, dy))
        }
    }
    
}
