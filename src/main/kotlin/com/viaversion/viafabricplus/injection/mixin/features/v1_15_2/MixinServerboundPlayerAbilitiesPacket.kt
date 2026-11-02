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
package com.viaversion.viafabricplus.injection.mixin.features.v1_15_2

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacket::class) 
abstract class MixinServerboundPlayerAbilitiesPacket {

    @org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$abilities`: net.minecraft.world.entity.player.Abilities = uninitialized()
    @Inject(method = ["<init>(Lnet/minecraft/world/entity/player/Abilities;)V"], at = [At("RETURN")]) private fun capturePlayerAbilities(abilities: net.minecraft.world.entity.player.Abilities, ci: CallbackInfo) {
        this.`viaFabricPlus$abilities` = abilities
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["write"], at = [At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;writeByte(I)Lnet/minecraft/network/FriendlyByteBuf;")]) private fun implementFlags(instance: net.minecraft.network.FriendlyByteBuf, value: kotlin.Int): net.minecraft.network.FriendlyByteBuf {
        var value: kotlin.Int = value
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_15_2)) {
            if (`viaFabricPlus$abilities`.invulnerable) value = value or 1
            if (`viaFabricPlus$abilities`.mayfly) value = value or 4
            if (`viaFabricPlus$abilities`.instabuild) value = value or 8
        }
        
        return instance.writeByte(value)
    }
    
}
