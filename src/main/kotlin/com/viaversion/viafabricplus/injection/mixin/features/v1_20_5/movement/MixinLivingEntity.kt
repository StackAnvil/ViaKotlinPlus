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
package com.viaversion.viafabricplus.injection.mixin.features.v1_20_5.movement

import org.spongepowered.asm.mixin.Mixin
import com.llamalad7.mixinextras.injector.ModifyExpressionValue
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import com.viaversion.viafabricplus.features.v1_20_5.EnchantmentAttributesEmulation1_20_6

@Mixin(value = [net.minecraft.world.entity.LivingEntity::class], priority = 999) 
abstract class MixinLivingEntity {

    @ModifyExpressionValue(method = ["tickEffects"], at = [At(value = "CONSTANT", args = ["intValue=4"])]) private fun changeParticleDensity(original: kotlin.Int): kotlin.Int {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20_5)) {
            return 2
        } else {
            return original
        }
    }
    
    @Inject(method = ["getBlockSpeedFactor"], at = [At("HEAD")]) private fun setGenericMovementEfficiencyAttribute(cir: CallbackInfoReturnable<kotlin.Float>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20_5)) {
            EnchantmentAttributesEmulation1_20_6.setGenericMovementEfficiencyAttribute(this as kotlin.Any as net.minecraft.world.entity.LivingEntity)
        }
    }
    
}
