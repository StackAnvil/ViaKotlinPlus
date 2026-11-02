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

class RecipeManager1_11_2 (recipes: Iterable<net.minecraft.world.item.crafting.RecipeHolder<*>>) {

    private val recipesByType: com.google.common.collect.Multimap<net.minecraft.world.item.crafting.RecipeType<*>, net.minecraft.world.item.crafting.RecipeHolder<*>>
    private val recipesById: kotlin.collections.MutableMap<net.minecraft.resources.ResourceKey<net.minecraft.world.item.crafting.Recipe<*>>, net.minecraft.world.item.crafting.RecipeHolder<*>>
    
    init {
        val recipesByTypeBuilder: com.google.common.collect.ImmutableMultimap.Builder<net.minecraft.world.item.crafting.RecipeType<*>, net.minecraft.world.item.crafting.RecipeHolder<*>> = com.google.common.collect.ImmutableMultimap.builder<net.minecraft.world.item.crafting.RecipeType<*>, net.minecraft.world.item.crafting.RecipeHolder<*>>()
        val recipesByIdBuilder: com.google.common.collect.ImmutableMap.Builder<net.minecraft.resources.ResourceKey<net.minecraft.world.item.crafting.Recipe<*>>, net.minecraft.world.item.crafting.RecipeHolder<*>> = com.google.common.collect.ImmutableMap.builder<net.minecraft.resources.ResourceKey<net.minecraft.world.item.crafting.Recipe<*>>, net.minecraft.world.item.crafting.RecipeHolder<*>>()
        
        for (recipeEntry in recipes) {
            val recipeType: net.minecraft.world.item.crafting.RecipeType<*> = recipeEntry.value().getType()
            recipesByTypeBuilder.put(recipeType, recipeEntry)
            recipesByIdBuilder.put(recipeEntry.id(), recipeEntry)
        }
        
        this.recipesByType = recipesByTypeBuilder.build()
        this.recipesById = recipesByIdBuilder.build()
    }
    
    fun <I : net.minecraft.world.item.crafting.RecipeInput, T : net.minecraft.world.item.crafting.Recipe<I>>getFirstMatch(type: net.minecraft.world.item.crafting.RecipeType<T>, input: I, world: net.minecraft.world.level.Level): java.util.Optional<net.minecraft.world.item.crafting.RecipeHolder<T>> {
        if (input.isEmpty()) {
            return java.util.Optional.empty<net.minecraft.world.item.crafting.RecipeHolder<T>>()
        } else {
            return this.recipesByType.get(type).stream().map<net.minecraft.world.item.crafting.RecipeHolder<T>> {e: net.minecraft.world.item.crafting.RecipeHolder<*> -> e as net.minecraft.world.item.crafting.RecipeHolder<T>} .filter {recipe: net.minecraft.world.item.crafting.RecipeHolder<T> -> recipe.value().matches(input, world)} .findFirst()
        }
    }
    
    fun get(id: net.minecraft.resources.ResourceKey<net.minecraft.world.item.crafting.Recipe<*>>): java.util.Optional<net.minecraft.world.item.crafting.RecipeHolder<*>> {
        return java.util.Optional.ofNullable<net.minecraft.world.item.crafting.RecipeHolder<*>>(this.recipesById.get(id))
    }
    
    fun values(): kotlin.collections.MutableCollection<net.minecraft.world.item.crafting.RecipeHolder<*>> {
        return this.recipesById.values
    }
    
    fun keys(): java.util.stream.Stream<net.minecraft.resources.ResourceKey<net.minecraft.world.item.crafting.Recipe<*>>> {
        return this.recipesById.keys.stream()
    }
    
}
