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
package com.viaversion.viafabricplus.injection.mixin.features.b1_8

import org.spongepowered.asm.mixin.Mixin
import com.viaversion.viaversion.protocols.v1_20_3to1_20_5.Protocol1_20_3To1_20_5
import com.viaversion.viaversion.protocols.v1_20_2to1_20_3.packet.ClientboundPacket1_20_3
import com.viaversion.viaversion.protocols.v1_20_3to1_20_5.packet.ServerboundPacket1_20_5
import java.util.HashSet
import java.util.HashMap
import com.viaversion.viaversion.api.minecraft.item.data.ToolProperties
import java.util.LinkedHashMap
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.viaversion.viafabricplus.protocoltranslator.impl.ViaFabricPlusMappingDataLoader
import com.viaversion.viaversion.api.Via
import com.viaversion.viaversion.api.minecraft.item.data.ToolRule
import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viaversion.api.minecraft.data.StructuredDataContainer
import net.raphimc.vialegacy.api.LegacyProtocolVersion
import com.viaversion.viaversion.api.minecraft.data.StructuredDataKey

@Mixin(value = [com.viaversion.viaversion.protocols.v1_20_3to1_20_5.rewriter.BlockItemPacketRewriter1_20_5::class], remap = false) 
abstract class MixinBlockItemPacketRewriter1_20_5 (protocol: Protocol1_20_3To1_20_5, itemType: com.viaversion.viaversion.api.type.Type<com.viaversion.viaversion.api.minecraft.item.Item>, itemArrayType: com.viaversion.viaversion.api.type.Type<kotlin.Array<com.viaversion.viaversion.api.minecraft.item.Item>>, mappedItemType: com.viaversion.viaversion.api.type.Type<com.viaversion.viaversion.api.minecraft.item.Item>, mappedItemArrayType: com.viaversion.viaversion.api.type.Type<kotlin.Array<com.viaversion.viaversion.api.minecraft.item.Item>>) : com.viaversion.viaversion.rewriter.ItemRewriter<ClientboundPacket1_20_3, ServerboundPacket1_20_5, Protocol1_20_3To1_20_5>(protocol, itemType, itemArrayType, mappedItemType, mappedItemArrayType) {

    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField val `viaFabricPlus$foodItems_b1_7_3`: kotlin.collections.MutableSet<kotlin.String> = HashSet<kotlin.String>()
    
    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField val `viaFabricPlus$armorMaxDamage_b1_8_1`: kotlin.collections.MutableMap<kotlin.String, kotlin.Int> = HashMap<kotlin.String, kotlin.Int>()
    
    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField val `viaFabricPlus$toolDataChanges`: kotlin.collections.MutableMap<com.viaversion.viaversion.api.protocol.version.ProtocolVersion, kotlin.collections.MutableMap<kotlin.String, ToolProperties>> = LinkedHashMap<com.viaversion.viaversion.api.protocol.version.ProtocolVersion, kotlin.collections.MutableMap<kotlin.String, ToolProperties>>()
    
    @Inject(method = ["<init>"], at = [At("RETURN")]) private fun loadItemMappings(protocol: Protocol1_20_3To1_20_5, ci: CallbackInfo) {
        // Technically, it would be cleaner to split mapping loading into their respective protocols, but that will be impossible
        // in a clean way, so let's just wait for Via* to load all protocols and then load everything in here.
        this.`viaFabricPlus$foodItems_b1_7_3`.add("minecraft:apple")
        this.`viaFabricPlus$foodItems_b1_7_3`.add("minecraft:mushroom_stew")
        this.`viaFabricPlus$foodItems_b1_7_3`.add("minecraft:bread")
        this.`viaFabricPlus$foodItems_b1_7_3`.add("minecraft:porkchop")
        this.`viaFabricPlus$foodItems_b1_7_3`.add("minecraft:cooked_porkchop")
        this.`viaFabricPlus$foodItems_b1_7_3`.add("minecraft:golden_apple")
        this.`viaFabricPlus$foodItems_b1_7_3`.add("minecraft:cod")
        this.`viaFabricPlus$foodItems_b1_7_3`.add("minecraft:cooked_cod")
        this.`viaFabricPlus$foodItems_b1_7_3`.add("minecraft:cookie")
        
        val armorMaxDamages: com.viaversion.viaversion.libs.gson.JsonObject = ViaFabricPlusMappingDataLoader.INSTANCE.loadData("armor-damages-b1.8.1.json")!!
        for (entry in armorMaxDamages.entrySet()) {
            this.`viaFabricPlus$armorMaxDamage_b1_8_1`.put(entry.key, entry.value.getAsInt())
        }
        
        Via.getManager().addPostEnableListener(java.lang.Runnable {
        val itemToolComponents: com.viaversion.viaversion.libs.gson.JsonObject = ViaFabricPlusMappingDataLoader.INSTANCE.loadData("item-tool-components.json")!!
        for (entry in itemToolComponents.entrySet()) {
            val version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion = com.viaversion.viaversion.api.protocol.version.ProtocolVersion.getClosest(entry.key)!!
            kotlin.checkNotNull(version) {"Unknown protocol version: " + entry.key} 
            val toolProperties: kotlin.collections.MutableMap<kotlin.String, ToolProperties> = HashMap<kotlin.String, ToolProperties>()
            val toolComponents: com.viaversion.viaversion.libs.gson.JsonArray = entry.value.getAsJsonArray()
            for (toolComponent in toolComponents) {
                val toolComponentObject: com.viaversion.viaversion.libs.gson.JsonObject = toolComponent.getAsJsonObject()
                val item: kotlin.String = toolComponentObject.get("item").getAsString()
                val defaultMiningSpeed: kotlin.Float = toolComponentObject.get("default_mining_speed").getAsFloat()
                val damagePerBlock: kotlin.Int = toolComponentObject.get("damage_per_block").getAsInt()
                val suitableFor: IntArray = this.`viaFabricPlus$blockJsonArrayToIds`(version, toolComponentObject.getAsJsonArray("suitable_for"))
                val toolRules: kotlin.collections.MutableList<ToolRule> = java.util.ArrayList<ToolRule>()
                val miningSpeeds: com.viaversion.viaversion.libs.gson.JsonArray = toolComponentObject.getAsJsonArray("mining_speeds")
                for (miningSpeed in miningSpeeds) {
                    val miningSpeedObject: com.viaversion.viaversion.libs.gson.JsonObject = miningSpeed.getAsJsonObject()
                    val blocks: IntArray = this.`viaFabricPlus$blockJsonArrayToIds`(version, miningSpeedObject.getAsJsonArray("blocks"))
                    val speed: kotlin.Float = miningSpeedObject.get("speed").getAsFloat()
                    toolRules.add(ToolRule(com.viaversion.viaversion.api.minecraft.HolderSet.of(blocks), speed, null))
                }
                if (suitableFor.size > 0) {
                    toolRules.add(ToolRule(com.viaversion.viaversion.api.minecraft.HolderSet.of(suitableFor), null, true))
                }
                toolProperties.put(item, ToolProperties(toolRules.toTypedArray<ToolRule>(), defaultMiningSpeed, damagePerBlock))
            }
            this.`viaFabricPlus$toolDataChanges`.put(version, toolProperties)
        }
        
        })
    }
    
     // Older servers don't have these components, so we can use them to emulate old item behaviour without the need
 // of modifying tons of code in the game.
    @Inject(method = ["appendItemDataFixComponents"], at = [At("RETURN")]) private fun appendItemDataFixComponents(connection: UserConnection, item: com.viaversion.viaversion.api.minecraft.item.Item, ci: CallbackInfo) {
        val data: StructuredDataContainer = item.dataContainer()
        val identifier: kotlin.String = this.protocol.getMappingData()!!.getFullItemMappings()!!.identifier(item.identifier()) ?: return
        
                // Fix durability tooltip displaying wrong
        if (connection.getProtocolInfo().serverProtocolVersion().olderThanOrEqualTo(LegacyProtocolVersion.b1_8tob1_8_1)) {
            if (this.`viaFabricPlus$armorMaxDamage_b1_8_1`.containsKey(identifier)) {
                data.set<kotlin.Int>(StructuredDataKey.MAX_DAMAGE, this.`viaFabricPlus$armorMaxDamage_b1_8_1`.get(identifier))
            }
        }
        
                // Fix item desyncs
        if (connection.getProtocolInfo().serverProtocolVersion().olderThanOrEqualTo(LegacyProtocolVersion.b1_7tob1_7_3)) {
            if (this.`viaFabricPlus$foodItems_b1_7_3`.contains(identifier)) {
                data.set<kotlin.Int>(StructuredDataKey.MAX_STACK_SIZE, 1)
                data.setEmpty(StructuredDataKey.FOOD1_20_5)
            }
        }
        
                // Tool data changes include mining speeds as well as suitable blocks and damage values
        for (entry in this.`viaFabricPlus$toolDataChanges`.entries) {
            if (connection.getProtocolInfo().serverProtocolVersion().olderThanOrEqualTo(entry.key)) {
                val toolProperties: ToolProperties? = entry.value.get(identifier)
                if (toolProperties != null) {
                    data.set<ToolProperties>(StructuredDataKey.TOOL1_20_5, toolProperties)
                    break
                }
            }
        }
    }
    
     // Converts block identifiers as well as materials (prefixed with #) to block ids
    @org.spongepowered.asm.mixin.Unique private fun `viaFabricPlus$blockJsonArrayToIds`(protocolVersion: com.viaversion.viaversion.api.protocol.version.ProtocolVersion, jsonArray: com.viaversion.viaversion.libs.gson.JsonArray): IntArray {
        val ids: com.viaversion.viaversion.libs.fastutil.ints.IntSet = com.viaversion.viaversion.libs.fastutil.ints.IntOpenHashSet()
        for (element in jsonArray) {
            val name: kotlin.String = element.getAsString()
            if (name.startsWith("#")) { // Material name
                val material: kotlin.String = name.substring(1)
                for (entry in ViaFabricPlusMappingDataLoader.BLOCK_MATERIALS.entries) {
                    for (materialEntry in entry.value.entries) {
                        if (protocolVersion.olderThanOrEqualTo(materialEntry.key) && materialEntry.value == material) {
                            ids.add(this.protocol.getMappingData()!!.blockId(entry.key))
                            break
                        }
                    }
                }
            } else if (name.startsWith("-")) { // Block name
                ids.remove(this.protocol.getMappingData()!!.blockId(name.substring(1)))
            } else { // Block name
                ids.add(this.protocol.getMappingData()!!.blockId(name))
            }
        }
        return ids.toIntArray()
    }
    
}
