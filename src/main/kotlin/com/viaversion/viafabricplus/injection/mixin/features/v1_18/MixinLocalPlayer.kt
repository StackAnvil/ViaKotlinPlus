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
package com.viaversion.viafabricplus.injection.mixin.features.v1_18

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(net.minecraft.client.player.LocalPlayer::class) 
abstract class MixinLocalPlayer (world: net.minecraft.client.multiplayer.ClientLevel, profile: com.mojang.authlib.GameProfile) : net.minecraft.client.player.AbstractClientPlayer(world, profile) {

    @org.spongepowered.asm.mixin.injection.Redirect(method = ["sendPosition"], at = [At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;square(D)D")]) private fun changeMagnitude(x: kotlin.Double): kotlin.Double {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_18)) {
            return 9.0E-4
        } else {
            return net.minecraft.util.Mth.square(x)
        }
    }
    
    @Inject(method = ["startRiding"], at = [At("RETURN")]) private fun setRotationsWhenInBoat(entity: net.minecraft.world.entity.Entity, force: kotlin.Boolean, sendEventAndTriggers: kotlin.Boolean, cir: CallbackInfoReturnable<kotlin.Boolean>) {
        if (cir.getReturnValueZ() && entity is net.minecraft.world.entity.vehicle.boat.Boat && com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_18)) {
            this.yRotO = entity.getYRot()
            this.setYRot(entity.getYRot())
            this.setYHeadRot(entity.getYRot())
        }
    }
    
}
