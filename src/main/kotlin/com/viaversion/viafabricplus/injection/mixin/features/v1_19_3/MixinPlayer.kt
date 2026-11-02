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
package com.viaversion.viafabricplus.injection.mixin.features.v1_19_3

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(net.minecraft.world.entity.player.Player::class) 
abstract class MixinPlayer protected constructor(entityType: net.minecraft.world.entity.EntityType<out net.minecraft.world.entity.LivingEntity>, world: net.minecraft.world.level.Level) : net.minecraft.world.entity.LivingEntity(entityType, world) {

    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$isSprinting`: kotlin.Boolean = false
    
    @Inject(method = ["aiStep"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setSpeed(F)V")]) private fun storeSprintingState(ci: CallbackInfo) {
        `viaFabricPlus$isSprinting` = this.isSprinting()
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["getFlyingSpeed"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSprinting()Z")]) private fun useLastSprintingState(instance: net.minecraft.world.entity.player.Player): kotlin.Boolean {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_19_3)) {
            return `viaFabricPlus$isSprinting`
        } else {
            return instance.isSprinting()
        }
    }
    
}
