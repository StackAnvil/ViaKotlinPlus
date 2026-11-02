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
package com.viaversion.viafabricplus.injection.mixin.features.v1_7_6

import org.spongepowered.asm.mixin.Mixin
import com.llamalad7.mixinextras.injector.ModifyExpressionValue
import org.spongepowered.asm.mixin.injection.At
import com.viaversion.viafabricplus.ViaFabricPlusImpl
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation

@Mixin(net.minecraft.client.Minecraft::class) 
abstract class MixinMinecraft {

    @ModifyExpressionValue(method = ["continueAttack"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z")]) private fun allowBlockBreakAndItemUsageAtTheSameTime(original: kotlin.Boolean): kotlin.Boolean {
        return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_7_6) && original
    }
    
    @ModifyExpressionValue(method = ["startUseItem"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;isDestroying()Z")]) private fun allowItemUsageAndBlockBreakAtTheSameTime(original: kotlin.Boolean): kotlin.Boolean {
        return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_7_6) && !ViaFabricPlusImpl.impl().visuals().allowItemUsageWhileMining().isActive() && original
    }
    
    @WrapOperation(method = ["startAttack"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;hasMissTime()Z", ordinal = 1)]) private fun removeHitPenalty(instance: net.minecraft.client.multiplayer.MultiPlayerGameMode, original: com.llamalad7.mixinextras.injector.wrapoperation.Operation<kotlin.Boolean>): kotlin.Boolean {
        // In <=1.7 this code is not inside the MISS case but in the BLOCK case, causing it to never be called
        return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_7_6) && original.call(instance)
    }
    
}
