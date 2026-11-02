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
package com.viaversion.viafabricplus.injection.mixin.features.v1_21_5.entity

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(net.minecraft.world.entity.Entity::class) 
abstract class MixinEntity {

    @org.spongepowered.asm.mixin.Shadow 
    @JvmField val level: net.minecraft.world.level.Level = uninitialized()
    @org.spongepowered.asm.mixin.Shadow abstract fun isAlive(): kotlin.Boolean
    
    @org.spongepowered.asm.mixin.Shadow abstract fun level(): net.minecraft.world.level.Level
    
    @Inject(method = ["interact"], at = [At("HEAD")], cancellable = true) private fun removeLeashActions(player: net.minecraft.world.entity.player.Player, hand: net.minecraft.world.InteractionHand, location: net.minecraft.world.phys.Vec3, cir: CallbackInfoReturnable<net.minecraft.world.InteractionResult>) {
        // Removes shearing of equipment & snipping all leashes + condition changes
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_5)) {
            val itemStack: net.minecraft.world.item.ItemStack = player.getItemInHand(hand)
            if (this.isAlive() && this is net.minecraft.world.entity.Leashable) {
                val leashable = this as net.minecraft.world.entity.Leashable
                if (leashable.leashHolder !== player) {
                    if (itemStack.`is`(net.minecraft.world.item.Items.LEAD) && leashable.canHaveALeashAttachedTo(player)) {
                        itemStack.shrink(1)
                        cir.setReturnValue(net.minecraft.world.InteractionResult.SUCCESS)
                        return 
                    }
                } else {
                    cir.setReturnValue(net.minecraft.world.InteractionResult.SUCCESS.withoutItem())
                    return 
                }
            }
            
            cir.setReturnValue(net.minecraft.world.InteractionResult.PASS)
        }
    }
    
    @com.llamalad7.mixinextras.injector.v2.WrapWithCondition(method = ["move"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addMovementThisTick(Lnet/minecraft/world/entity/Entity\$Movement;)V")]) private fun removeExtraCollisionChecks(instance: net.minecraft.world.entity.Entity, movement: net.minecraft.world.entity.Entity.Movement): kotlin.Boolean {
        return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_5)
    }
    
}
