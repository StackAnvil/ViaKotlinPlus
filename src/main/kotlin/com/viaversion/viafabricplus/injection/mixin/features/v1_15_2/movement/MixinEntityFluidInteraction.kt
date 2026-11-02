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
package com.viaversion.viafabricplus.injection.mixin.features.v1_15_2.movement

import org.spongepowered.asm.mixin.Mixin
import com.llamalad7.mixinextras.injector.ModifyExpressionValue
import org.spongepowered.asm.mixin.injection.At

@Mixin(net.minecraft.world.entity.EntityFluidInteraction::class) 
abstract class MixinEntityFluidInteraction {

    @com.llamalad7.mixinextras.expression.Definition(id = "eyeY", local = [com.llamalad7.mixinextras.sugar.Local(type = kotlin.Double::class, name = ["eyeY"])]) @com.llamalad7.mixinextras.expression.Definition(id = "fluidTopForCamera", local = [com.llamalad7.mixinextras.sugar.Local(type = kotlin.Double::class, name = ["fluidTopForCamera"])]) @com.llamalad7.mixinextras.expression.Expression("eyeY <= fluidTopForCamera") @ModifyExpressionValue(method = ["update"], at = [At("MIXINEXTRAS:EXPRESSION")]) private fun addMagicOffset(original: kotlin.Boolean, @com.llamalad7.mixinextras.sugar.Local(name = ["eyeY"]) eyeY: kotlin.Double, @com.llamalad7.mixinextras.sugar.Local(name = ["fluidBottom"]) fluidBottom: kotlin.Double, @com.llamalad7.mixinextras.sugar.Local(name = ["level"]) level: net.minecraft.world.level.BlockGetter, @com.llamalad7.mixinextras.sugar.Local(name = ["fluidState"]) fluidState: net.minecraft.world.level.material.FluidState, @com.llamalad7.mixinextras.sugar.Local(name = ["mutablePos"]) mutablePos: net.minecraft.core.BlockPos.MutableBlockPos): kotlin.Boolean {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_15_2)) {
            return eyeY <= (fluidBottom + (fluidState.getHeight(level, mutablePos) + (0.11111111f * 2f)))
        } else {
            return original
        }
    }
    
}
