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

import com.viaversion.viafabricplus.features.v1_11_1.Recipes1_11_2.LegacyRecipe
import com.viaversion.viaversion.api.protocol.version.ProtocolVersionRange
import com.viaversion.viafabricplus.features.v1_11_1.RecipeManager1_11_2
import com.viaversion.viafabricplus.features.v1_11_1.Recipes1_11_2
import com.viaversion.viafabricplus.protocoltranslator.impl.ViaFabricPlusMappingDataLoader
import com.viaversion.viafabricplus.features.v1_11_1.Recipes1_11_2.LegacyShapedRecipe
import com.viaversion.viafabricplus.features.v1_11_1.Recipes1_11_2.LegacyShapelessRecipe
import com.viaversion.viafabricplus.features.v1_11_1.Recipes1_11_2.LegacySmeltingRecipe
import net.raphimc.vialegacy.api.LegacyProtocolVersion
import com.viaversion.viafabricplus.features.v1_11_1.ShulkerBoxColoringRecipe
import com.viaversion.viafabricplus.features.v1_11_1.AddBannerPatternRecipe
import java.util.Locale
import com.viaversion.viafabricplus.features.v1_11_1.Recipes1_11_2.RecipeItemStack
import java.util.HashMap

object Recipes1_11_2 {
    private val LEGACY_RECIPES: kotlin.collections.MutableList<com.mojang.datafixers.util.Pair<LegacyRecipe, ProtocolVersionRange>> = java.util.ArrayList<com.mojang.datafixers.util.Pair<LegacyRecipe, ProtocolVersionRange>>()
    private var RECIPE_MANAGER: RecipeManager1_11_2? = null
    
    fun init() {
        kotlin.check(Recipes1_11_2.LEGACY_RECIPES.isEmpty()) {"Recipes1_11_2 is already initialized"} 
        
        val recipes: com.viaversion.viaversion.libs.gson.JsonArray = ViaFabricPlusMappingDataLoader.INSTANCE.loadData("recipes-1.11.2.json")!!.getAsJsonArray("")
        for (recipeElement in recipes) {
            val type: kotlin.String = recipeElement.getAsJsonObject().get("type").getAsString()
            val versionRange: ProtocolVersionRange = ProtocolVersionRange.fromString(recipeElement.getAsJsonObject().get("version").getAsString())
            when (type){"shaped" -> Recipes1_11_2.LEGACY_RECIPES.add(com.mojang.datafixers.util.Pair<LegacyRecipe, ProtocolVersionRange>(LegacyShapedRecipe.Companion.fromJson(recipeElement.getAsJsonObject()), versionRange))
                "shapeless" -> Recipes1_11_2.LEGACY_RECIPES.add(com.mojang.datafixers.util.Pair<LegacyRecipe, ProtocolVersionRange>(LegacyShapelessRecipe.Companion.fromJson(recipeElement.getAsJsonObject()), versionRange))
                "smelting" -> Recipes1_11_2.LEGACY_RECIPES.add(com.mojang.datafixers.util.Pair<LegacyRecipe, ProtocolVersionRange>(LegacySmeltingRecipe.Companion.fromJson(recipeElement.getAsJsonObject()), versionRange))
                else -> throw java.lang.IllegalArgumentException("Unknown recipe type: " + type)}
        }
    }
    
    fun getRecipeManager(registryAccess: net.minecraft.core.RegistryAccess.Frozen): RecipeManager1_11_2 {
        if (Recipes1_11_2.RECIPE_MANAGER == null) {
            val recipes: kotlin.collections.MutableList<net.minecraft.world.item.crafting.RecipeHolder<*>> = java.util.ArrayList<net.minecraft.world.item.crafting.RecipeHolder<*>>()
            
                        // Regular recipes
            /* !!! Hit visitElement for element type: class org.jetbrains.kotlin.nj2k.tree.JKJavaForLoopStatement !!! */
            
                        // Special recipes
            val specialRecipes: kotlin.collections.MutableList<net.minecraft.world.item.crafting.CraftingRecipe> = java.util.ArrayList<net.minecraft.world.item.crafting.CraftingRecipe>()
            if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThanOrEqualTo(LegacyProtocolVersion.r1_4_2)) {
                specialRecipes.add(Recipes1_11_2.createDyeRecipe(registryAccess, net.minecraft.world.item.Items.LEATHER_HELMET))
                specialRecipes.add(Recipes1_11_2.createDyeRecipe(registryAccess, net.minecraft.world.item.Items.LEATHER_CHESTPLATE))
                specialRecipes.add(Recipes1_11_2.createDyeRecipe(registryAccess, net.minecraft.world.item.Items.LEATHER_LEGGINGS))
                specialRecipes.add(Recipes1_11_2.createDyeRecipe(registryAccess, net.minecraft.world.item.Items.LEATHER_BOOTS))
                specialRecipes.add(Recipes1_11_2.createDyeRecipe(registryAccess, net.minecraft.world.item.Items.LEATHER_HORSE_ARMOR))
                specialRecipes.add(net.minecraft.world.item.crafting.TransmuteRecipe(net.minecraft.data.recipes.RecipeBuilder.createCraftingCommonInfo(true), net.minecraft.data.recipes.RecipeBuilder.createCraftingBookInfo(net.minecraft.data.recipes.RecipeCategory.MISC, "map_cloning"), net.minecraft.world.item.crafting.Ingredient.of(net.minecraft.world.item.Items.FILLED_MAP), net.minecraft.world.item.crafting.Ingredient.of(net.minecraft.world.item.Items.MAP), net.minecraft.world.item.crafting.TransmuteRecipe.FULL_RANGE_MATERIAL_COUNT, net.minecraft.world.item.crafting.TransmuteResult(net.minecraft.world.item.Items.FILLED_MAP), true))
                specialRecipes.add(net.minecraft.world.item.crafting.MapExtendingRecipe(net.minecraft.world.item.crafting.Ingredient.of(net.minecraft.world.item.Items.FILLED_MAP), net.minecraft.world.item.crafting.Ingredient.of(net.minecraft.world.item.Items.PAPER), net.minecraft.world.item.crafting.TransmuteResult(net.minecraft.world.item.Items.FILLED_MAP)))
            }
            if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThanOrEqualTo(LegacyProtocolVersion.r1_4_6tor1_4_7)) {
                specialRecipes.add(net.minecraft.world.item.crafting.FireworkRocketRecipe(net.minecraft.world.item.crafting.Ingredient.of(net.minecraft.world.item.Items.PAPER), net.minecraft.world.item.crafting.Ingredient.of(net.minecraft.world.item.Items.GUNPOWDER), net.minecraft.world.item.crafting.Ingredient.of(net.minecraft.world.item.Items.FIREWORK_STAR), net.minecraft.world.item.ItemStackTemplate(net.minecraft.world.item.Items.FIREWORK_ROCKET, 3)))
            }
            if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_11)) {
                specialRecipes.add(ShulkerBoxColoringRecipe())
            }
            if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_9)) {
                specialRecipes.add(net.minecraft.world.item.crafting.ImbueRecipe(net.minecraft.data.recipes.RecipeBuilder.createCraftingCommonInfo(true), net.minecraft.data.recipes.RecipeBuilder.createCraftingBookInfo(net.minecraft.data.recipes.RecipeCategory.MISC, "tipped_arrow"), net.minecraft.world.item.crafting.Ingredient.of(net.minecraft.world.item.Items.LINGERING_POTION), net.minecraft.world.item.crafting.Ingredient.of(net.minecraft.world.item.Items.ARROW), net.minecraft.world.item.ItemStackTemplate(net.minecraft.world.item.Items.TIPPED_ARROW, 8)))
                specialRecipes.add(net.minecraft.world.item.crafting.ShieldDecorationRecipe(net.minecraft.world.item.crafting.Ingredient.of(registryAccess.getOrThrow<net.minecraft.world.item.Item>(net.minecraft.tags.ItemTags.BANNERS)), net.minecraft.world.item.crafting.Ingredient.of(net.minecraft.world.item.Items.SHIELD), net.minecraft.world.item.ItemStackTemplate(net.minecraft.world.item.Items.SHIELD)))
            }
            if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8)) {
                specialRecipes.add(net.minecraft.world.item.crafting.RepairItemRecipe())
                net.minecraft.world.item.Items.BANNER.forEach(java.util.function.Consumer {item: net.minecraft.world.item.Item -> specialRecipes.add(net.minecraft.world.item.crafting.BannerDuplicateRecipe(net.minecraft.world.item.crafting.Ingredient.of(item), net.minecraft.world.item.ItemStackTemplate(item)))})
                specialRecipes.add(AddBannerPatternRecipe())
            }
            if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_7_2)) {
                specialRecipes.add(net.minecraft.world.item.crafting.BookCloningRecipe(net.minecraft.world.item.crafting.Ingredient.of(net.minecraft.world.item.Items.WRITTEN_BOOK), net.minecraft.world.item.crafting.Ingredient.of(net.minecraft.world.item.Items.WRITABLE_BOOK), net.minecraft.world.item.crafting.BookCloningRecipe.DEFAULT_BOOK_GENERATION_RANGES, net.minecraft.world.item.ItemStackTemplate(net.minecraft.world.item.Items.WRITTEN_BOOK)))
            }
            var index: kotlin.Int = 0
            for (specialRecipe in specialRecipes) {
                val key: net.minecraft.resources.ResourceKey<net.minecraft.world.item.crafting.Recipe<*>> = net.minecraft.resources.ResourceKey.create<net.minecraft.world.item.crafting.Recipe<*>>(net.minecraft.core.registries.Registries.RECIPE, net.minecraft.resources.Identifier.fromNamespaceAndPath("viafabricplus", "recipe/special_" + specialRecipe.javaClass.simpleName.replace("Recipe", "").lowercase(Locale.ROOT) + (if (index == 0) "" else index)))
                recipes.add(net.minecraft.world.item.crafting.RecipeHolder<net.minecraft.world.item.crafting.CraftingRecipe>(key, specialRecipe))
                index++
            }
            
            Recipes1_11_2.RECIPE_MANAGER = RecipeManager1_11_2(recipes)
        }
        
        return Recipes1_11_2.RECIPE_MANAGER!!
    }
    
    private fun createDyeRecipe(registryAccess: net.minecraft.core.RegistryAccess.Frozen, result: net.minecraft.world.item.Item): net.minecraft.world.item.crafting.DyeRecipe {
        val commonInfo: net.minecraft.world.item.crafting.Recipe.CommonInfo = net.minecraft.data.recipes.RecipeBuilder.createCraftingCommonInfo(true)
        val craftingBookInfo: net.minecraft.world.item.crafting.CraftingRecipe.CraftingBookInfo = net.minecraft.data.recipes.RecipeBuilder.createCraftingBookInfo(net.minecraft.data.recipes.RecipeCategory.MISC, "dyed_armor")
        val ingredient: net.minecraft.world.item.crafting.Ingredient = net.minecraft.world.item.crafting.Ingredient.of(registryAccess.getOrThrow<net.minecraft.world.item.Item>(net.minecraft.tags.ItemTags.DYES))
        return net.minecraft.world.item.crafting.DyeRecipe(commonInfo, craftingBookInfo, net.minecraft.world.item.crafting.Ingredient.of(result), ingredient, net.minecraft.world.item.ItemStackTemplate(result))
    }
    
    fun reset() {
        Recipes1_11_2.RECIPE_MANAGER = null
    }
    
    fun setCraftingResultSlot(syncId: kotlin.Int, screenHandler: net.minecraft.world.inventory.AbstractContainerMenu, inventory: net.minecraft.world.inventory.CraftingContainer) {
        val network: net.minecraft.client.multiplayer.ClientPacketListener = net.minecraft.client.Minecraft.getInstance().getConnection()
        val world: net.minecraft.client.multiplayer.ClientLevel = net.minecraft.client.Minecraft.getInstance().level
        val input: net.minecraft.world.item.crafting.CraftingInput = inventory.asCraftInput()
        
        val result: net.minecraft.world.item.ItemStack = Recipes1_11_2.getRecipeManager(network.registryAccess())
        .getFirstMatch<net.minecraft.world.item.crafting.CraftingInput, net.minecraft.world.item.crafting.CraftingRecipe>(net.minecraft.world.item.crafting.RecipeType.CRAFTING, input, world) // Get the first matching recipe
        .map<net.minecraft.world.item.ItemStack>(java.util.function.Function {recipe: net.minecraft.world.item.crafting.RecipeHolder<net.minecraft.world.item.crafting.CraftingRecipe> -> recipe.value().assemble(input)}) // Craft the recipe to get the result
        .orElse(net.minecraft.world.item.ItemStack.EMPTY) // If there is no recipe, set the result to air
        
                // Update the result slot
        network.handleContainerSetSlot(net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket(syncId, screenHandler.getStateId(), 0, result))
    }
    
    private fun getItemById(id: net.minecraft.resources.Identifier): net.minecraft.world.item.Item {
        val item = net.minecraft.core.registries.BuiltInRegistries.ITEM.getOptional(id).orElse(null)
        return kotlin.checkNotNull(item) { "Unknown item: " + id.toString() }
    }
    
    private interface LegacyRecipe

    private data class RecipeItemStack(val item: net.minecraft.world.item.Item, val count: kotlin.Int) {

        fun toItemStack(): net.minecraft.world.item.ItemStackTemplate {
            return net.minecraft.world.item.ItemStackTemplate(item, count)
        }

        companion object {
            fun fromJson(obj: com.viaversion.viaversion.libs.gson.JsonObject): RecipeItemStack {
                val id = net.minecraft.resources.Identifier.parse(obj.get("id").getAsString())
                val count = if (obj.has("count")) obj.get("count").getAsInt() else 1
                return RecipeItemStack(getItemById(id), count)
            }
        }
    }

    private data class LegacyShapedRecipe(
        val group: kotlin.String,
        val result: RecipeItemStack,
        val pattern: kotlin.collections.MutableList<kotlin.String>,
        val legend: kotlin.collections.MutableMap<Char, kotlin.collections.MutableList<net.minecraft.world.item.Item>>
    ) : LegacyRecipe {
        companion object {
            fun fromJson(obj: com.viaversion.viaversion.libs.gson.JsonObject): LegacyShapedRecipe {
                val group = if (obj.has("group")) obj.get("group").getAsString() else ""
                val result = RecipeItemStack.fromJson(obj.getAsJsonObject("result"))
                val pattern = mutableListOf<kotlin.String>()
                for (element in obj.getAsJsonArray("pattern")) {
                    pattern.add(element.getAsString())
                }
                val legend = mutableMapOf<Char, kotlin.collections.MutableList<net.minecraft.world.item.Item>>()
                for (entry in obj.getAsJsonObject("legend").entrySet()) {
                    val key = entry.key[0]
                    val items = mutableListOf<net.minecraft.world.item.Item>()
                    for (itemId in entry.value.asJsonArray) {
                        items.add(getItemById(net.minecraft.resources.Identifier.parse(itemId.getAsString())))
                    }
                    legend.put(key, items)
                }
                return LegacyShapedRecipe(group, result, pattern, legend)
            }
        }
    }

    private data class LegacyShapelessRecipe(
        val group: kotlin.String,
        val result: RecipeItemStack,
        val ingredients: kotlin.collections.MutableList<kotlin.collections.MutableList<net.minecraft.world.item.Item>>
    ) : LegacyRecipe {
        companion object {
            fun fromJson(obj: com.viaversion.viaversion.libs.gson.JsonObject): LegacyShapelessRecipe {
                val group = if (obj.has("group")) obj.get("group").getAsString() else ""
                val result = RecipeItemStack.fromJson(obj.getAsJsonObject("result"))
                val ingredients = mutableListOf<kotlin.collections.MutableList<net.minecraft.world.item.Item>>()
                for (element in obj.getAsJsonArray("ingredients")) {
                    val items = mutableListOf<net.minecraft.world.item.Item>()
                    for (itemId in element.getAsJsonArray()) {
                        items.add(getItemById(net.minecraft.resources.Identifier.parse(itemId.getAsString())))
                    }
                    ingredients.add(items)
                }
                return LegacyShapelessRecipe(group, result, ingredients)
            }
        }
    }

    private data class LegacySmeltingRecipe(
        val group: kotlin.String,
        val result: RecipeItemStack,
        val input: kotlin.collections.MutableList<net.minecraft.world.item.Item>,
        val experience: kotlin.Float
    ) : LegacyRecipe {
        companion object {
            fun fromJson(obj: com.viaversion.viaversion.libs.gson.JsonObject): LegacySmeltingRecipe {
                val group = if (obj.has("group")) obj.get("group").getAsString() else ""
                val result = RecipeItemStack.fromJson(obj.getAsJsonObject("result"))
                val input = mutableListOf<net.minecraft.world.item.Item>()
                for (element in obj.getAsJsonArray("input")) {
                    input.add(getItemById(net.minecraft.resources.Identifier.parse(element.getAsString())))
                }
                val experience = obj.get("experience").getAsFloat()
                return LegacySmeltingRecipe(group, result, input, experience)
            }
        }
    }
}
