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
package com.viaversion.viafabricplus.injection.mixin.features.v1_15_2.movement

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation

@Mixin(net.minecraft.world.entity.LivingEntity::class) 
abstract class MixinLivingEntity {

    @org.spongepowered.asm.mixin.injection.Redirect(method = ["canGlide"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/core/Holder;)Z")]) private fun allowElytraWhenLevitating(instance: net.minecraft.world.entity.LivingEntity, effect: net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect>): kotlin.Boolean {
        return com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_15_2) && instance.hasEffect(effect)
    }
    
    @WrapOperation(method = ["drop"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;swing(Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/component/SwingAnimation;Z)Z")]) private fun dontSwingHand(instance: net.minecraft.world.entity.LivingEntity, hand: net.minecraft.world.InteractionHand, animation: net.minecraft.world.item.component.SwingAnimation, sendToSwingingEntity: kotlin.Boolean, original: com.llamalad7.mixinextras.injector.wrapoperation.Operation<kotlin.Boolean>): kotlin.Boolean {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_15_2)) {
            return original.call(instance, hand, animation, sendToSwingingEntity)
        } else {
            return false
        }
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["isInShallowFluid"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getFluidHeight(Lnet/minecraft/tags/TagKey;)D")]) private fun dontApplyLavaMovement(instance: net.minecraft.world.entity.LivingEntity, tagKey: net.minecraft.tags.TagKey<net.minecraft.world.level.material.Fluid>): kotlin.Double {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_15_2) && tagKey === net.minecraft.tags.FluidTags.LAVA) {
            return kotlin.Double.Companion.MAX_VALUE
        } else {
            return instance.getFluidHeight(tagKey)
        }
    }
    
}
