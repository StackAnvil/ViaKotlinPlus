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

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.injection.At
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import net.raphimc.vialegacy.api.LegacyProtocolVersion

@Mixin(net.minecraft.world.entity.player.Player::class) 
abstract class MixinPlayer protected constructor(entityType: net.minecraft.world.entity.EntityType<out net.minecraft.world.entity.LivingEntity>, world: net.minecraft.world.level.Level) : net.minecraft.world.entity.LivingEntity(entityType, world) {

    @org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField val inventory: net.minecraft.world.entity.player.Inventory = uninitialized()
    @org.spongepowered.asm.mixin.Shadow abstract fun hasCorrectToolForDrops(state: net.minecraft.world.level.block.state.BlockState): kotlin.Boolean
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["getDestroySpeed"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;hasEffect(Lnet/minecraft/core/Holder;)Z")]) private fun changeSpeedCalculation(instance: net.minecraft.world.entity.player.Player, statusEffect: net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect>, @com.llamalad7.mixinextras.sugar.Local(name = ["speed"]) f: LocalFloatRef): kotlin.Boolean {
        val hasMiningFatigue: kotlin.Boolean = instance.hasEffect(statusEffect)
        if (hasMiningFatigue && com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_7_6)) {
            f.set(f.get() * (1.0f - (this.getEffect(net.minecraft.world.effect.MobEffects.MINING_FATIGUE).getAmplifier() + 1) * 0.2f))
            if (f.get() < 0) {
                f.set(0f)
            }
            return false // disable original code
        }
        return hasMiningFatigue
    }
    
    @Inject(method = ["getDestroySpeed"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectUtil;hasDigSpeed(Lnet/minecraft/world/entity/LivingEntity;)Z")]) private fun changeSpeedCalculation(block: net.minecraft.world.level.block.state.BlockState, cir: CallbackInfoReturnable<kotlin.Float>, @com.llamalad7.mixinextras.sugar.Local(name = ["speed"]) f: LocalFloatRef) {
        val efficiency: kotlin.Float = this.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.MINING_EFFICIENCY).toFloat()
        if (efficiency <= 0) {
            return 
        }
        
        val speed: kotlin.Float = this.inventory.getSelectedItem().getDestroySpeed(block)
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_4_4tor1_4_5) && this.hasCorrectToolForDrops(block)) {
            f.set(speed + efficiency)
        } else if (speed > 1f || com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_4_6tor1_4_7)) {
            if (!this.getMainHandItem().isEmpty()) {
                if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_7_6)) {
                    if (speed <= 1.0 && !this.hasCorrectToolForDrops(block)) {
                        f.set(speed + efficiency * 0.08f)
                    } else {
                        f.set(speed + efficiency)
                    }
                }
            }
        }
    }
    
}
