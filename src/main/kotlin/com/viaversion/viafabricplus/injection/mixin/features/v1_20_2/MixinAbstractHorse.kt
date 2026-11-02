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
package com.viaversion.viafabricplus.injection.mixin.features.v1_20_2

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At

@Mixin(net.minecraft.world.entity.animal.equine.AbstractHorse::class) 
abstract class MixinAbstractHorse {

    @org.spongepowered.asm.mixin.Shadow protected abstract fun handleEating(player: net.minecraft.world.entity.player.Player, itemStack: net.minecraft.world.item.ItemStack): kotlin.Boolean
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["fedFood"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/equine/AbstractHorse;handleEating(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)Z")]) private fun decrementFoodItemClientside(instance: net.minecraft.world.entity.animal.equine.AbstractHorse, player: net.minecraft.world.entity.player.Player, itemStack: net.minecraft.world.item.ItemStack): kotlin.Boolean {
        return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20_2) || this.handleEating(player, itemStack)
    }
    
}
