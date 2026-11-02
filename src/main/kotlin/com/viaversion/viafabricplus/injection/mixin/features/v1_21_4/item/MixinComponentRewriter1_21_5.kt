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
package com.viaversion.viafabricplus.injection.mixin.features.v1_21_4.item

import org.spongepowered.asm.mixin.Mixin
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
import org.spongepowered.asm.mixin.injection.At
import com.viaversion.viaversion.util.TagUtil
import com.viaversion.viafabricplus.protocoltranslator.util.ItemUtil
import com.viaversion.viaversion.protocols.v1_21_4to1_21_5.Protocol1_21_4To1_21_5

@Mixin(value = [com.viaversion.viaversion.protocols.v1_21_4to1_21_5.rewriter.ComponentRewriter1_21_5::class], remap = false) 
abstract class MixinComponentRewriter1_21_5 {

    @WrapOperation(method = ["handleShowItem"], at = [At(value = "INVOKE", target = "Lcom/viaversion/viaversion/util/TagUtil;removeNamespaced(Lcom/viaversion/nbt/tag/CompoundTag;Ljava/lang/String;)Z")]) private fun storeBackupTag(tag: com.viaversion.nbt.tag.CompoundTag, key: kotlin.String, original: com.llamalad7.mixinextras.injector.wrapoperation.Operation<kotlin.Boolean>): kotlin.Boolean {
        if (key == "hide_additional_tooltip") {
            var customData: com.viaversion.nbt.tag.CompoundTag = TagUtil.getNamespacedCompoundTag(tag, "custom_data")!!
            if (customData == null) {
                tag.put("custom_data", com.viaversion.nbt.tag.CompoundTag().also { customData = it })
            }
            
            val backupTag: com.viaversion.nbt.tag.CompoundTag = com.viaversion.nbt.tag.CompoundTag()
            backupTag.putBoolean("hide_additional_tooltip", true)
            customData.put(ItemUtil.vvNbtName(Protocol1_21_4To1_21_5::class.java, "backup"), backupTag)
        }
        
        return original.call(tag, key)
    }
    
}
