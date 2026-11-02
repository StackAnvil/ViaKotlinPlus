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
package com.viaversion.viafabricplus.injection.mixin.features.v1_8.item

import org.spongepowered.asm.mixin.Mixin
import com.viaversion.viaversion.protocols.v1_8to1_9.rewriter.ItemPacketRewriter1_9
import com.viaversion.viaversion.protocols.v1_8to1_9.Protocol1_8To1_9
import com.viaversion.viaversion.protocols.v1_8to1_9.packet.ClientboundPackets1_8
import com.viaversion.viaversion.protocols.v1_8to1_9.packet.ServerboundPackets1_9
import com.viaversion.viaversion.api.minecraft.item.data.AttributeModifiers1_20_5
import java.util.HashMap
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.viaversion.viafabricplus.protocoltranslator.impl.ViaFabricPlusMappingDataLoader
import java.util.UUID
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(value = [ItemPacketRewriter1_9::class], remap = false) 
abstract class MixinItemPacketRewriter1_9 (protocol: Protocol1_8To1_9, itemType: com.viaversion.viaversion.api.type.Type<com.viaversion.viaversion.api.minecraft.item.Item>, itemArrayType: com.viaversion.viaversion.api.type.Type<kotlin.Array<com.viaversion.viaversion.api.minecraft.item.Item>>, mappedItemType: com.viaversion.viaversion.api.type.Type<com.viaversion.viaversion.api.minecraft.item.Item>, mappedItemArrayType: com.viaversion.viaversion.api.type.Type<kotlin.Array<com.viaversion.viaversion.api.minecraft.item.Item>>) : com.viaversion.viaversion.rewriter.ItemRewriter<ClientboundPackets1_8, ServerboundPackets1_9, Protocol1_8To1_9>(protocol, itemType, itemArrayType, mappedItemType, mappedItemArrayType) {

    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField val `viaFabricPlus$itemIdentifiers`: com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap<kotlin.String> = com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap<kotlin.String>()
    
    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField val `viaFabricPlus$itemAttributes`: kotlin.collections.MutableMap<kotlin.String, kotlin.collections.MutableMap<kotlin.String, com.viaversion.viaversion.util.Pair<kotlin.String, AttributeModifiers1_20_5.ModifierData>>> = HashMap<kotlin.String, kotlin.collections.MutableMap<kotlin.String, com.viaversion.viaversion.util.Pair<kotlin.String, AttributeModifiers1_20_5.ModifierData>>>()
    
    @Inject(method = ["<init>"], at = [At("RETURN")]) private fun loadAdditionalData(ci: CallbackInfo) {
        val itemIdentifiers: com.viaversion.viaversion.libs.gson.JsonObject = ViaFabricPlusMappingDataLoader.INSTANCE.loadData("item-identifiers-1.8.json")!!
        for (entry in itemIdentifiers.entrySet()) {
            `viaFabricPlus$itemIdentifiers`.put(entry.value.getAsInt(), entry.key)
        }
        
        val itemAttributes: com.viaversion.viaversion.libs.gson.JsonObject = ViaFabricPlusMappingDataLoader.INSTANCE.loadData("item-attributes-1.8.json")!!
        for (itemEntry in itemAttributes.entrySet()) {
            val itemIdentifier: kotlin.String = itemEntry.key
            val attributes: kotlin.collections.MutableMap<kotlin.String, com.viaversion.viaversion.util.Pair<kotlin.String, AttributeModifiers1_20_5.ModifierData>> = HashMap<kotlin.String, com.viaversion.viaversion.util.Pair<kotlin.String, AttributeModifiers1_20_5.ModifierData>>()
            for (attributeEntry in itemEntry.value.getAsJsonObject().entrySet()) {
                val attribute: kotlin.String = attributeEntry.key
                val attributeData: com.viaversion.viaversion.libs.gson.JsonObject = attributeEntry.value.getAsJsonObject()
                val modifierData: AttributeModifiers1_20_5.ModifierData = AttributeModifiers1_20_5.ModifierData(UUID.fromString(attributeData.get("id").getAsString()), attributeData.get("name").getAsString(), attributeData.get("amount").getAsDouble(), attributeData.get("operation").getAsInt())
                val slot: kotlin.String = attributeData.get("slot").getAsString()
                attributes.put(attribute, com.viaversion.viaversion.util.Pair<kotlin.String, AttributeModifiers1_20_5.ModifierData>(slot, modifierData))
            }
            `viaFabricPlus$itemAttributes`.put(itemIdentifier, attributes)
        }
    }
    
    @Inject(method = ["handleItemToClient"], at = [At("RETURN")]) private fun addAttributeFixData(cir: CallbackInfoReturnable<com.viaversion.viaversion.api.minecraft.item.Item>) {
        val item: com.viaversion.viaversion.api.minecraft.item.Item? = cir.getReturnValue()
        if (item == null) {
            return 
        }
        
        val itemId = item.identifier() ?: return
        val identifier: kotlin.String? = `viaFabricPlus$itemIdentifiers`.get(itemId)
        if (identifier != null && `viaFabricPlus$itemAttributes`.containsKey(identifier)) {
            val attributes: kotlin.collections.MutableMap<kotlin.String, com.viaversion.viaversion.util.Pair<kotlin.String, AttributeModifiers1_20_5.ModifierData>> = `viaFabricPlus$itemAttributes`.get(identifier)!!
            val attributeFixTag: com.viaversion.nbt.tag.CompoundTag = com.viaversion.nbt.tag.CompoundTag()
            var tag: com.viaversion.nbt.tag.CompoundTag? = item.tag()
            if (tag == null) {
                tag = com.viaversion.nbt.tag.CompoundTag()
                item.setTag(tag)
                attributeFixTag.putBoolean("RemoveTag", true)
            }
            tag.put(nbtTagName("attributeFix"), attributeFixTag)
            
            var attributeModifiers: com.viaversion.nbt.tag.ListTag<com.viaversion.nbt.tag.CompoundTag>? = tag.getListTag<com.viaversion.nbt.tag.CompoundTag>("AttributeModifiers", com.viaversion.nbt.tag.CompoundTag::class.java)
            if (attributeModifiers == null) {
                attributeModifiers = com.viaversion.nbt.tag.ListTag<com.viaversion.nbt.tag.CompoundTag>(com.viaversion.nbt.tag.CompoundTag::class.java)
                for (entry in attributes.entries) {
                    val attributeModifier: com.viaversion.nbt.tag.CompoundTag = com.viaversion.nbt.tag.CompoundTag()
                    attributeModifier.putString("AttributeName", entry.key)
                    attributeModifier.putString("Name", entry.value.value()!!.name())
                    attributeModifier.putDouble("Amount", entry.value.value()!!.amount())
                    attributeModifier.putInt("Operation", entry.value.value()!!.operation())
                    attributeModifier.putLong("UUIDMost", entry.value.value()!!.uuid().getMostSignificantBits())
                    attributeModifier.putLong("UUIDLeast", entry.value.value()!!.uuid().getLeastSignificantBits())
                    attributeModifier.putString("Slot", entry.value.key())
                    attributeModifiers.add(attributeModifier)
                }
                tag.put("AttributeModifiers", attributeModifiers)
                attributeFixTag.putBoolean("RemoveAttributeModifiers", true)
            }
        }
    }
    
    @Inject(method = ["handleItemToServer"], at = [At("RETURN")]) private fun removeAttributeFixData(cir: CallbackInfoReturnable<com.viaversion.viaversion.api.minecraft.item.Item>) {
        val item: com.viaversion.viaversion.api.minecraft.item.Item? = cir.getReturnValue()
        if (item == null) {
            return 
        }
        val tag: com.viaversion.nbt.tag.CompoundTag? = item.tag()
        if (tag == null) {
            return 
        }
        val attributeFixTag: com.viaversion.nbt.tag.CompoundTag = tag.removeUnchecked<com.viaversion.nbt.tag.CompoundTag>(nbtTagName("attributeFix"))
        if (attributeFixTag == null) {
            return 
        }
        
        if (attributeFixTag.contains("RemoveAttributeModifiers")) {
            tag.remove("AttributeModifiers")
        }
        if (attributeFixTag.contains("RemoveTag")) {
            item.setTag(null)
        }
    }
    
}
