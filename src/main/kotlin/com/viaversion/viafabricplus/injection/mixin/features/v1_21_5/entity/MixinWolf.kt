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

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import com.viaversion.viafabricplus.features.v1_21_5.WolfHealthTracker1_14_4

@Mixin(net.minecraft.world.entity.animal.wolf.Wolf::class) 
abstract class MixinWolf protected constructor(entityType: net.minecraft.world.entity.EntityType<out net.minecraft.world.entity.TamableAnimal>, world: net.minecraft.world.level.Level) : net.minecraft.world.entity.TamableAnimal(entityType, world), net.minecraft.world.entity.NeutralMob {

    @org.spongepowered.asm.mixin.Shadow abstract fun getCollarColor(): net.minecraft.world.item.DyeColor
    
    @org.spongepowered.asm.mixin.Shadow protected abstract fun setCollarColor(color: net.minecraft.world.item.DyeColor)
    
    @Inject(method = ["mobInteract"], at = [At("HEAD")], cancellable = true) private fun fixWolfInteract(player: net.minecraft.world.entity.player.Player, hand: net.minecraft.world.InteractionHand, cir: CallbackInfoReturnable<net.minecraft.world.InteractionResult>) {
        val itemStack: net.minecraft.world.item.ItemStack = player.getItemInHand(hand)
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_14_4)) {
            val item: net.minecraft.world.item.Item = itemStack.getItem()
            if (this.isTame()) {
                val foodComponent: net.minecraft.world.food.FoodProperties = itemStack.get<net.minecraft.world.food.FoodProperties>(net.minecraft.core.component.DataComponents.FOOD)
                if (foodComponent != null) {
                    if (this.isFood(itemStack) && WolfHealthTracker1_14_4.getWolfHealth(this) < 20.0f) {
                        if (!player.getAbilities().instabuild) itemStack.shrink(1)
                        this.heal(foodComponent.nutrition().toFloat())
                        cir.setReturnValue(net.minecraft.world.InteractionResult.SUCCESS)
                        return 
                    }
                } else if (item is net.minecraft.world.item.DyeItem) {
                    val dyeColor: net.minecraft.world.item.DyeColor = itemStack.get<net.minecraft.world.item.DyeColor>(net.minecraft.core.component.DataComponents.DYE)
                    if (dyeColor != null && dyeColor != this.getCollarColor()) {
                        this.setCollarColor(dyeColor)
                        if (!player.getAbilities().instabuild) itemStack.shrink(1)
                        cir.setReturnValue(net.minecraft.world.InteractionResult.SUCCESS)
                        return 
                    }
                }
            } else if (item === net.minecraft.world.item.Items.BONE && !this.isAngry()) {
                if (!player.getAbilities().instabuild) itemStack.shrink(1)
                cir.setReturnValue(net.minecraft.world.InteractionResult.SUCCESS)
                return 
            }
            
            cir.setReturnValue(super.mobInteract(player, hand))
        } else if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_5) && this.isTame()) {
            // Call armor shearing manually as removed in MixinEntity
            if (itemStack.`is`(net.minecraft.world.item.Items.SHEARS)
            && this.isOwnedBy(player)
            && this.isWearingBodyArmor()
            && (!net.minecraft.world.item.enchantment.EnchantmentHelper.has(this.getBodyArmorItem(), net.minecraft.world.item.enchantment.EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE) || player.isCreative())) {
                this.playSound(net.minecraft.sounds.SoundEvents.ARMOR_UNEQUIP_WOLF)
                cir.setReturnValue(net.minecraft.world.InteractionResult.SUCCESS)
            }
        }
    }
    
}
