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
package com.viaversion.viafabricplus.features.v1_11_1

import com.viaversion.viafabricplus.features.v1_11_1.ShulkerBoxColoringRecipe

class ShulkerBoxColoringRecipe : net.minecraft.world.item.crafting.CustomRecipe() {

    override fun matches(input: net.minecraft.world.item.crafting.CraftingInput, world: net.minecraft.world.level.Level): kotlin.Boolean {
        var i: kotlin.Int = 0
        var j: kotlin.Int = 0
        
        for (k in 0 ..< input.size()) {
            val stack: net.minecraft.world.item.ItemStack = input.getItem(k)
            if (!stack.isEmpty()) {
                if (net.minecraft.world.level.block.Block.byItem(stack.getItem()) is net.minecraft.world.level.block.ShulkerBoxBlock) {
                    i++
                } else {
                    if (stack.getItem() !is net.minecraft.world.item.DyeItem) {
                        return false
                    }
                    
                    j++
                }
                
                if (j > 1 || i > 1) {
                    return false
                }
            }
        }
        
        return i == 1 && j == 1
    }
    
    override fun assemble(input: net.minecraft.world.item.crafting.CraftingInput): net.minecraft.world.item.ItemStack {
        var result: net.minecraft.world.item.ItemStack = net.minecraft.world.item.ItemStack.EMPTY
        var dyeStack: net.minecraft.world.item.ItemStack = net.minecraft.world.item.Items.DYE.white().getDefaultInstance()
        
        for (i in 0 ..< input.size()) {
            val stack: net.minecraft.world.item.ItemStack = input.getItem(i)
            if (!stack.isEmpty()) {
                val item: net.minecraft.world.item.Item = stack.getItem()
                if (net.minecraft.world.level.block.Block.byItem(item) is net.minecraft.world.level.block.ShulkerBoxBlock) {
                    result = stack
                } else if (item is net.minecraft.world.item.DyeItem) {
                    dyeStack = stack
                }
            }
        }
        
        val dye: net.minecraft.world.item.DyeColor = dyeStack.get<net.minecraft.world.item.DyeColor>(net.minecraft.core.component.DataComponents.DYE)
        return result.transmuteCopy(if (dye == null) net.minecraft.world.level.block.Blocks.SHULKER_BOX else net.minecraft.world.level.block.Blocks.DYED_SHULKER_BOX.pick(dye), 1)
    }
    
    override fun getSerializer(): net.minecraft.world.item.crafting.RecipeSerializer<ShulkerBoxColoringRecipe> {
        return ShulkerBoxColoringRecipe.Companion.SERIALIZER
    }
    
    companion object {
        val SERIALIZER: net.minecraft.world.item.crafting.RecipeSerializer<ShulkerBoxColoringRecipe> = net.minecraft.world.item.crafting.RecipeSerializer<ShulkerBoxColoringRecipe>(com.mojang.serialization.MapCodec.unit<ShulkerBoxColoringRecipe>(ShulkerBoxColoringRecipe()), net.minecraft.network.codec.StreamCodec.unit<net.minecraft.network.RegistryFriendlyByteBuf, ShulkerBoxColoringRecipe>(ShulkerBoxColoringRecipe()))
        
    }}
