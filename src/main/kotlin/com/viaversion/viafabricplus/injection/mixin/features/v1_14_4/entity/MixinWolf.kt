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
package com.viaversion.viafabricplus.injection.mixin.features.v1_14_4.entity

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import com.viaversion.viafabricplus.features.v1_21_5.WolfHealthTracker1_14_4

@Mixin(net.minecraft.world.entity.animal.wolf.Wolf::class) 
abstract class MixinWolf protected constructor(entityType: net.minecraft.world.entity.EntityType<out net.minecraft.world.entity.TamableAnimal>, world: net.minecraft.world.level.Level) : net.minecraft.world.entity.TamableAnimal(entityType, world), net.minecraft.world.entity.NeutralMob {

    @org.spongepowered.asm.mixin.injection.Redirect(method = ["*"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/wolf/Wolf;getHealth()F")]) private fun fixWolfHealth(instance: net.minecraft.world.entity.animal.wolf.Wolf): kotlin.Float {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_14_4)) {
            return WolfHealthTracker1_14_4.getWolfHealth(this)
        } else {
            return instance.getHealth()
        }
    }
    
}
