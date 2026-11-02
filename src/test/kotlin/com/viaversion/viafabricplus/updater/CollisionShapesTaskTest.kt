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
package com.viaversion.viafabricplus.updater

import com.viaversion.viafabricplus.updater.CollisionShapesTaskTest
import java.io.IOException
import com.viaversion.viafabricplus.util.JsonSave
import java.util.HashSet

 /**
 * Generates a file containing all collision shapes of static blocks. Can be used for game updates where Mojang refactors the collision shapes.
 * Please only use this when there are large changes where manual comparison is not feasible. Otherwise, manually checking the changes is recommended.
 */
class CollisionShapesTaskTest {

     //@Test
    fun generate() {
        val toCompare: java.io.File = java.io.File("old_blocks_collision_shapes.json")
        
                // First step, generate the "before" file if it doesn't exist
        if (!toCompare.exists() || !toCompare.isFile()) {
            val path: java.nio.file.Path = java.io.File("blocks_collision_shapes.json").toPath()
            try {
                java.nio.file.Files.write(path, CollisionShapesTaskTest.Companion.dumpData().toString().toByteArray())
            }catch (e: IOException) {
                throw java.lang.RuntimeException(e)
            }
            return 
        }
        
                // Compare current data against before file
        kotlin.io.println("Starting comparison...")
        try {
            val first: com.google.gson.JsonObject = JsonSave.GSON.fromJson<com.google.gson.JsonObject>(java.nio.file.Files.readString(toCompare.toPath()), com.google.gson.JsonObject::class.java)
            val second: com.google.gson.JsonObject = JsonSave.GSON.fromJson<com.google.gson.JsonObject>(java.nio.file.Files.readString(java.io.File("blocks_collision_shapes.json").toPath()), com.google.gson.JsonObject::class.java)
            CollisionShapesTaskTest.Companion.compare(first, second)
        }catch (e: IOException) {
            throw java.lang.RuntimeException(e)
        }
    }
    
    companion object {
        private fun printVoxelShape(shape: net.minecraft.world.phys.shapes.VoxelShape) {
//        if (shape.getBoundingBoxes().size() > 1) {
//            System.out.println("Multiple boxes in shape: " + shape);
//        }
            val box: net.minecraft.world.phys.AABB = shape.bounds()
            kotlin.io.println("Min: " + box.minX * 16 + ", " + box.minY * 16 + ", " + box.minZ * 16 + " Max: " + box.maxX * 16 + ", " + box.maxY * 16 + ", " + box.maxZ * 16)
        }
        
        private fun dumpData(): java.lang.StringBuilder {
            val skippedBlocks: kotlin.collections.MutableSet<java.lang.Class<out net.minecraft.world.level.block.Block>> = HashSet<java.lang.Class<out net.minecraft.world.level.block.Block>>()
            val data: com.google.gson.JsonObject = com.google.gson.JsonObject()
            for (block in net.minecraft.core.registries.BuiltInRegistries.BLOCK) {
                val blockData: com.google.gson.JsonObject = com.google.gson.JsonObject()
                for (blockStates in block.getStateDefinition().getPossibleStates()) {
                    val blockStateData: com.google.gson.JsonObject = com.google.gson.JsonObject()
                    try {
                        val collisionShape: net.minecraft.world.phys.shapes.VoxelShape = blockStates.getCollisionShape(null, null)
                        blockStateData.addProperty("collisionShape", collisionShape.toString())
                        val outlineShape: net.minecraft.world.phys.shapes.VoxelShape = blockStates.getShape(null, null)
                        blockStateData.addProperty("outlineShape", outlineShape.toString())
                        val raycastShape: net.minecraft.world.phys.shapes.VoxelShape = blockStates.getInteractionShape(null, null)
                        blockStateData.addProperty("raycastShape", raycastShape.toString())
                    }catch (e: java.lang.Exception) {
                        skippedBlocks.add(block.javaClass)
                        break
                    }
                    blockData.add(blockStates.toString(), blockStateData)
                }
                
                data.add(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).toString(), blockData)
            }
            
            val skippedBlocksData: com.google.gson.JsonArray = com.google.gson.JsonArray()
            for (skippedBlock in skippedBlocks) {
                kotlin.io.println("Skipped Block, check this one manually: " + skippedBlock.getSimpleName())
                skippedBlocksData.add(skippedBlock.getSimpleName())
            }
            data.add("skippedBlocks", skippedBlocksData)
            return java.lang.StringBuilder(data.toString())
        }
        
        private fun compare(first: com.google.gson.JsonObject, second: com.google.gson.JsonObject) {
            val firstSkippedBlocks: com.google.gson.JsonArray = first.getAsJsonArray("skippedBlocks")
            val secondSkippedBlocks: com.google.gson.JsonArray = second.getAsJsonArray("skippedBlocks")
            if (firstSkippedBlocks.size() != secondSkippedBlocks.size()) {
                kotlin.io.println("The skipped blocks are different.")
                kotlin.io.println("First: " + firstSkippedBlocks)
                kotlin.io.println("Second: " + secondSkippedBlocks)
            } else {
                for (block in firstSkippedBlocks) {
                    kotlin.io.println("Skipped Block, check this one manually: " + block.getAsString())
                }
            }
            
            for (block in net.minecraft.core.registries.BuiltInRegistries.BLOCK) {
                val firstBlock: com.google.gson.JsonObject = first.getAsJsonObject(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).toString())
                val secondBlock: com.google.gson.JsonObject = second.getAsJsonObject(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).toString())
                if (firstBlock == null) {
                // New block (or renamed), can be ignored
                //System.out.println("The block " + Registries.BLOCK.getId(block) + " is missing in the first file.");
                    continue 
                }
                
                var missingBlockState: kotlin.Boolean = false
                for (blockState in block.getStateDefinition().getPossibleStates()) {
                    val firstBlockState: com.google.gson.JsonObject = firstBlock.getAsJsonObject(blockState.toString())
                    val secondBlockState: com.google.gson.JsonObject = secondBlock.getAsJsonObject(blockState.toString())
                    if (firstBlockState == null || secondBlockState == null) {
                    // Can be ignored for comparing (usually)
                    //System.out.println("BlockState " + blockState + " is missing in one of the files.");
                        missingBlockState = true
                        continue 
                    }
                    
                    val firstCollisionShape: kotlin.String = firstBlockState.get("collisionShape").getAsString()
                    val secondCollisionShape: kotlin.String = secondBlockState.get("collisionShape").getAsString()
                    if (firstCollisionShape != secondCollisionShape) {
                        kotlin.io.println("Collision shape of " + blockState + " in " + net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block) + " is different.")
                        kotlin.io.println("First: " + firstCollisionShape)
                        kotlin.io.println("Second: " + secondCollisionShape)
                    }
                    
                    val firstOutlineShape: kotlin.String = firstBlockState.get("outlineShape").getAsString()
                    val secondOutlineShape: kotlin.String = secondBlockState.get("outlineShape").getAsString()
                    if (firstOutlineShape != secondOutlineShape) {
                        kotlin.io.println("Outline shape of " + blockState + " in " + net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block) + " is different.")
                        kotlin.io.println("First: " + firstOutlineShape)
                        kotlin.io.println("Second: " + secondOutlineShape)
                    }
                    
                    val firstRaycastShape: kotlin.String = firstBlockState.get("raycastShape").getAsString()
                    val secondRaycastShape: kotlin.String = secondBlockState.get("raycastShape").getAsString()
                    if (firstRaycastShape != secondRaycastShape) {
                        kotlin.io.println("Raycast shape of " + blockState + " in " + net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block) + " is different.")
                        kotlin.io.println("First: " + firstRaycastShape)
                        kotlin.io.println("Second: " + secondRaycastShape)
                    }
                }
                if (missingBlockState && !firstSkippedBlocks.contains(com.google.gson.JsonPrimitive(block.javaClass.getSimpleName()))) {
                    kotlin.io.println("The block " + net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block) + " is missing block states in one of the files.")
                }
            }
        }
        
    }}
