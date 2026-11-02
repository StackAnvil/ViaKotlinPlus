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
package com.viaversion.viafabricplus.injection.mixin.features.v1_8.entity

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(net.minecraft.world.entity.player.Player::class) 
abstract class MixinPlayer {

    @org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField val abilities: net.minecraft.world.entity.player.Abilities = uninitialized()
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["getAttackStrengthScale"], at = [At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F")]) private fun removeAttackCooldown(value: kotlin.Float, min: kotlin.Float, max: kotlin.Float): kotlin.Float {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8)) {
            return 1.0f
        } else {
            return net.minecraft.util.Mth.clamp(value, min, max)
        }
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["getItemSwapScale"], at = [At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F")]) private fun removeSwapCooldown(value: kotlin.Float, min: kotlin.Float, max: kotlin.Float): kotlin.Float {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8)) {
            return 1.0f
        } else {
            return net.minecraft.util.Mth.clamp(value, min, max)
        }
    }
    
    @Inject(method = ["isCreative"], at = [At("HEAD")], cancellable = true) private fun fixCreativeCheck(cir: CallbackInfoReturnable<kotlin.Boolean>) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8)) {
            cir.setReturnValue(this.abilities.instabuild)
        }
    }
    
}
