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
package com.viaversion.viafabricplus.injection.mixin.features.b1_8

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import com.viaversion.viafabricplus.ViaFabricPlusImpl

@Mixin(net.minecraft.world.entity.player.Player::class) 
abstract class MixinPlayer {

    @Inject(method = ["getHurtSound"], at = [At("HEAD")], cancellable = true) private fun replaceSound(source: net.minecraft.world.damagesource.DamageSource, cir: CallbackInfoReturnable<net.minecraft.sounds.SoundEvent>) {
        if (ViaFabricPlusImpl.impl().visuals().replaceHurtSoundWithOOFSound().isActive()) {
            cir.setReturnValue(com.viaversion.viafabricplus.injection.mixin.features.b1_8.MixinPlayer.Companion.`viaFabricPlus$oof_hurt`)
        }
    }
    
    private companion object {
        @field:org.spongepowered.asm.mixin.Unique 
        @JvmField val `viaFabricPlus$oof_hurt`: net.minecraft.sounds.SoundEvent = net.minecraft.sounds.SoundEvent.createVariableRangeEvent(net.minecraft.resources.Identifier.fromNamespaceAndPath("viafabricplus", "oof.hurt"))
        
    }}
