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

enum class BannerPattern_1_13_2 (pattern: net.minecraft.resources.ResourceKey<net.minecraft.world.level.block.entity.BannerPattern>) {

    BASE(net.minecraft.world.level.block.entity.BannerPatterns.BASE), 
    SQUARE_BOTTOM_LEFT(net.minecraft.world.level.block.entity.BannerPatterns.SQUARE_BOTTOM_LEFT, "   ", "   ", "#  "), 
    SQUARE_BOTTOM_RIGHT(net.minecraft.world.level.block.entity.BannerPatterns.SQUARE_BOTTOM_RIGHT, "   ", "   ", "  #"), 
    SQUARE_TOP_LEFT(net.minecraft.world.level.block.entity.BannerPatterns.SQUARE_TOP_LEFT, "#  ", "   ", "   "), 
    SQUARE_TOP_RIGHT(net.minecraft.world.level.block.entity.BannerPatterns.SQUARE_TOP_RIGHT, "  #", "   ", "   "), 
    STRIPE_BOTTOM(net.minecraft.world.level.block.entity.BannerPatterns.STRIPE_BOTTOM, "   ", "   ", "###"), 
    STRIPE_TOP(net.minecraft.world.level.block.entity.BannerPatterns.STRIPE_TOP, "###", "   ", "   "), 
    STRIPE_LEFT(net.minecraft.world.level.block.entity.BannerPatterns.STRIPE_LEFT, "#  ", "#  ", "#  "), 
    STRIPE_RIGHT(net.minecraft.world.level.block.entity.BannerPatterns.STRIPE_RIGHT, "  #", "  #", "  #"), 
    STRIPE_CENTER(net.minecraft.world.level.block.entity.BannerPatterns.STRIPE_CENTER, " # ", " # ", " # "), 
    STRIPE_MIDDLE(net.minecraft.world.level.block.entity.BannerPatterns.STRIPE_MIDDLE, "   ", "###", "   "), 
    STRIPE_DOWNRIGHT(net.minecraft.world.level.block.entity.BannerPatterns.STRIPE_DOWNRIGHT, "#  ", " # ", "  #"), 
    STRIPE_DOWNLEFT(net.minecraft.world.level.block.entity.BannerPatterns.STRIPE_DOWNLEFT, "  #", " # ", "#  "), 
    STRIPE_SMALL(net.minecraft.world.level.block.entity.BannerPatterns.STRIPE_SMALL, "# #", "# #", "   "), 
    CROSS(net.minecraft.world.level.block.entity.BannerPatterns.CROSS, "# #", " # ", "# #"), 
    STRAIGHT_CROSS(net.minecraft.world.level.block.entity.BannerPatterns.STRAIGHT_CROSS, " # ", "###", " # "), 
    TRIANGLE_BOTTOM(net.minecraft.world.level.block.entity.BannerPatterns.TRIANGLE_BOTTOM, "   ", " # ", "# #"), 
    TRIANGLE_TOP(net.minecraft.world.level.block.entity.BannerPatterns.TRIANGLE_TOP, "# #", " # ", "   "), 
    TRIANGLES_BOTTOM(net.minecraft.world.level.block.entity.BannerPatterns.TRIANGLES_BOTTOM, "   ", "# #", " # "), 
    TRIANGLES_TOP(net.minecraft.world.level.block.entity.BannerPatterns.TRIANGLES_TOP, " # ", "# #", "   "), 
    DIAGONAL_LEFT(net.minecraft.world.level.block.entity.BannerPatterns.DIAGONAL_LEFT, "## ", "#  ", "   "), 
    DIAGONAL_RIGHT(net.minecraft.world.level.block.entity.BannerPatterns.DIAGONAL_RIGHT_MIRROR, "   ", "  #", " ##"), 
    DIAGONAL_LEFT_MIRROR(net.minecraft.world.level.block.entity.BannerPatterns.DIAGONAL_LEFT_MIRROR, "   ", "#  ", "## "), 
    DIAGONAL_RIGHT_MIRROR(net.minecraft.world.level.block.entity.BannerPatterns.DIAGONAL_RIGHT, " ##", "  #", "   "), 
    CIRCLE_MIDDLE(net.minecraft.world.level.block.entity.BannerPatterns.CIRCLE_MIDDLE, "   ", " # ", "   "), 
    RHOMBUS_MIDDLE(net.minecraft.world.level.block.entity.BannerPatterns.RHOMBUS_MIDDLE, " # ", "# #", " # "), 
    HALF_VERTICAL(net.minecraft.world.level.block.entity.BannerPatterns.HALF_VERTICAL, "## ", "## ", "## "), 
    HALF_HORIZONTAL(net.minecraft.world.level.block.entity.BannerPatterns.HALF_HORIZONTAL, "###", "###", "   "), 
    HALF_VERTICAL_MIRROR(net.minecraft.world.level.block.entity.BannerPatterns.HALF_VERTICAL_MIRROR, " ##", " ##", " ##"), 
    HALF_HORIZONTAL_MIRROR(net.minecraft.world.level.block.entity.BannerPatterns.HALF_HORIZONTAL_MIRROR, "   ", "###", "###"), 
    BORDER(net.minecraft.world.level.block.entity.BannerPatterns.BORDER, "###", "# #", "###"), 
    CURLY_BORDER(net.minecraft.world.level.block.entity.BannerPatterns.CURLY_BORDER, net.minecraft.world.item.ItemStack(net.minecraft.world.level.block.Blocks.VINE)), 
    GRADIENT(net.minecraft.world.level.block.entity.BannerPatterns.GRADIENT, "# #", " # ", " # "), 
    GRADIENT_UP(net.minecraft.world.level.block.entity.BannerPatterns.GRADIENT_UP, " # ", " # ", "# #"), 
    BRICKS(net.minecraft.world.level.block.entity.BannerPatterns.BRICKS, net.minecraft.world.item.ItemStack(net.minecraft.world.level.block.Blocks.BRICKS)), 
    GLOBE(net.minecraft.world.level.block.entity.BannerPatterns.GLOBE), 
    CREEPER(net.minecraft.world.level.block.entity.BannerPatterns.CREEPER, net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.CREEPER_HEAD)), 
    SKULL(net.minecraft.world.level.block.entity.BannerPatterns.SKULL, net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.WITHER_SKELETON_SKULL)), 
    FLOWER(net.minecraft.world.level.block.entity.BannerPatterns.FLOWER, net.minecraft.world.item.ItemStack(net.minecraft.world.level.block.Blocks.OXEYE_DAISY)), 
    MOJANG(net.minecraft.world.level.block.entity.BannerPatterns.MOJANG, net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.ENCHANTED_GOLDEN_APPLE));
    
    val key: net.minecraft.resources.ResourceKey<net.minecraft.world.level.block.entity.BannerPattern>
    val recipePattern: kotlin.Array<kotlin.String?>
    lateinit var baseStack: net.minecraft.world.item.ItemStack
private set
    
    init {
        this.recipePattern = kotlin.arrayOfNulls<kotlin.String>(3)
        this.baseStack = net.minecraft.world.item.ItemStack.EMPTY
        this.key = pattern
    }
    
    constructor(pattern: net.minecraft.resources.ResourceKey<net.minecraft.world.level.block.entity.BannerPattern>, baseStack: net.minecraft.world.item.ItemStack) : this(pattern) {
        this.baseStack = baseStack
    }
    
    constructor(pattern: net.minecraft.resources.ResourceKey<net.minecraft.world.level.block.entity.BannerPattern>, recipe1: kotlin.String, recipe2: kotlin.String, recipe3: kotlin.String) : this(pattern) {
        this.recipePattern[0] = recipe1
        this.recipePattern[1] = recipe2
        this.recipePattern[2] = recipe3
    }
    
    val isCraftable: Boolean
get() =!this.baseStack.isEmpty() || this.recipePattern[0] != null
    
    fun hasBaseStack(): kotlin.Boolean {
        return !this.baseStack.isEmpty()
    }
    
    }
