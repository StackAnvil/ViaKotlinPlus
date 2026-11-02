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

import com.viaversion.viafabricplus.features.v1_11_1.AddBannerPatternRecipe
import com.viaversion.viafabricplus.features.v1_11_1.BannerPattern_1_13_2

class AddBannerPatternRecipe : net.minecraft.world.item.crafting.CustomRecipe() {

    override fun matches(input: net.minecraft.world.item.crafting.CraftingInput, world: net.minecraft.world.level.Level): kotlin.Boolean {
        var foundBanner: kotlin.Boolean = false
        for (i in 0 ..< input.size()) {
            val stack: net.minecraft.world.item.ItemStack = input.getItem(i)
            if (stack.getItem() is net.minecraft.world.item.BannerItem) {
                if (foundBanner) return false
                if (stack.getOrDefault<net.minecraft.world.level.block.entity.BannerPatternLayers>(net.minecraft.core.component.DataComponents.BANNER_PATTERNS, net.minecraft.world.level.block.entity.BannerPatternLayers.EMPTY).layers().size >= 6) return false
                foundBanner = true
            }
        }
        return foundBanner && AddBannerPatternRecipe.Companion.getBannerPattern(input) != null
    }
    
    override fun assemble(input: net.minecraft.world.item.crafting.CraftingInput): net.minecraft.world.item.ItemStack {
        var result: net.minecraft.world.item.ItemStack = net.minecraft.world.item.ItemStack.EMPTY
        
        for (i in 0 ..< input.size()) {
            val stack: net.minecraft.world.item.ItemStack = input.getItem(i)
            if (!stack.isEmpty() && stack.getItem() is net.minecraft.world.item.BannerItem) {
                result = stack.copy()
                result.setCount(1)
                break
            }
        }
        
        val pattern: BannerPattern_1_13_2 = AddBannerPatternRecipe.Companion.getBannerPattern(input)!!
        if (pattern != null) {
            val patternKey: net.minecraft.core.Holder.Reference<net.minecraft.world.level.block.entity.BannerPattern> = net.minecraft.client.Minecraft.getInstance().getConnection().registryAccess().lookupOrThrow<net.minecraft.world.level.block.entity.BannerPattern>(net.minecraft.core.registries.Registries.BANNER_PATTERN).getOrThrow(pattern.key)
            var color: net.minecraft.world.item.DyeColor = if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)) net.minecraft.world.item.DyeColor.BLACK else net.minecraft.world.item.DyeColor.WHITE
            for (i in 0 ..< input.size()) {
                val item: net.minecraft.world.item.ItemStack = input.getItem(i)
                val dye = item.get(net.minecraft.core.component.DataComponents.DYE)
                if (dye != null) {
                    color = dye
                }
            }
            
            val patternsBuilder: net.minecraft.world.level.block.entity.BannerPatternLayers.Builder = net.minecraft.world.level.block.entity.BannerPatternLayers.Builder()
            if (result.has(net.minecraft.core.component.DataComponents.BANNER_PATTERNS)) {
                patternsBuilder.addAll(result.get<net.minecraft.world.level.block.entity.BannerPatternLayers>(net.minecraft.core.component.DataComponents.BANNER_PATTERNS))
            }
            patternsBuilder.add(net.minecraft.world.level.block.entity.BannerPatternLayers.Layer(patternKey, color))
            result.set<net.minecraft.world.level.block.entity.BannerPatternLayers>(net.minecraft.core.component.DataComponents.BANNER_PATTERNS, patternsBuilder.build())
        }
        
        return result
    }
    
    override fun getSerializer(): net.minecraft.world.item.crafting.RecipeSerializer<AddBannerPatternRecipe> {
        return AddBannerPatternRecipe.Companion.SERIALIZER
    }
    
    companion object {
        val SERIALIZER: net.minecraft.world.item.crafting.RecipeSerializer<AddBannerPatternRecipe> = net.minecraft.world.item.crafting.RecipeSerializer<AddBannerPatternRecipe>(com.mojang.serialization.MapCodec.unit<AddBannerPatternRecipe>(AddBannerPatternRecipe()), net.minecraft.network.codec.StreamCodec.unit<net.minecraft.network.RegistryFriendlyByteBuf, AddBannerPatternRecipe>(AddBannerPatternRecipe()))
        
        private fun getBannerPattern(input: net.minecraft.world.item.crafting.CraftingInput): BannerPattern_1_13_2? {
            for (pattern in BannerPattern_1_13_2.entries) {
                if (!pattern.isCraftable) continue 
                
                var matches: kotlin.Boolean = true
                if (pattern.hasBaseStack()) {
                    var foundBaseItem: kotlin.Boolean = false
                    var foundDye: kotlin.Boolean = false
                    for (i in 0 ..< input.size()) {
                        val stack: net.minecraft.world.item.ItemStack = input.getItem(i)
                        if (!stack.isEmpty() && stack.getItem() !is net.minecraft.world.item.BannerItem) {
                            if (stack.getItem() is net.minecraft.world.item.DyeItem) {
                                if (foundDye) {
                                    matches = false
                                    break
                                }
                                foundDye = true
                            } else {
                                if (foundBaseItem || !net.minecraft.world.item.ItemStack.isSameItem(stack, pattern.baseStack)) {
                                    matches = false
                                    break
                                }
                                foundBaseItem = true
                            }
                        }
                    }
                    if (!foundBaseItem || (!foundDye && com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_10))) matches = false
                } else if (input.size() == pattern.recipePattern.size * pattern.recipePattern[0]!!.length) {
                    var patternColor: net.minecraft.world.item.DyeColor? = null
                    for (i in 0 ..< input.size()) {
                        val row: kotlin.Int = i / 3
                        val col: kotlin.Int = i % 3
                        val stack: net.minecraft.world.item.ItemStack = input.getItem(i)
                        val item: net.minecraft.world.item.Item = stack.getItem()
                        if (!stack.isEmpty() && item !is net.minecraft.world.item.BannerItem) {
                            val color: net.minecraft.world.item.DyeColor = stack.get<net.minecraft.world.item.DyeColor>(net.minecraft.core.component.DataComponents.DYE)
                            if (color == null) {
                                matches = false
                                break
                            }
                            
                            if (patternColor != null && color != patternColor) {
                                matches = false
                                break
                            }
                            
                            if (pattern.recipePattern[row]!![col] == ' ') {
                                matches = false
                                break
                            }
                            
                            patternColor = color
                        } else if (pattern.recipePattern[row]!![col] != ' ') {
                            matches = false
                            break
                        }
                    }
                } else {
                    matches = false
                }
                
                if (matches) return pattern
            }
            
            return null
        }
        
    }}
