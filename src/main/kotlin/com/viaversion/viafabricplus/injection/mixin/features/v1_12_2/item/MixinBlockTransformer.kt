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
package com.viaversion.viafabricplus.injection.mixin.features.v1_12_2.item

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(net.minecraft.core.component.BlockTransformer::class) 
abstract class MixinBlockTransformer {

    @Inject(method = ["transformBlock"], at = [At("HEAD")], cancellable = true) private fun preventAxeStripping(context: net.minecraft.world.item.context.UseOnContext, cir: CallbackInfoReturnable<net.minecraft.world.InteractionResult>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)) {
            val transformer: net.minecraft.core.Holder<net.minecraft.core.component.BlockTransformer> = context.getItemInHand().get<net.minecraft.core.Holder<net.minecraft.core.component.BlockTransformer>>(net.minecraft.core.component.DataComponents.BLOCK_TRANSFORMER)
            if (transformer != null && transformer.`is`(net.minecraft.world.item.component.BlockTransformers.AXE)) {
                cir.setReturnValue(net.minecraft.world.InteractionResult.PASS)
            }
        }
    }
    
}
