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
package com.viaversion.viafabricplus.injection.mixin.features.v1_12_2

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import net.raphimc.vialegacy.api.LegacyProtocolVersion
import com.viaversion.viafabricplus.injection.mixin.features.v1_12_2.MixinItemRenderer

@Mixin(net.minecraft.client.renderer.item.ItemModelResolver::class) 
abstract class MixinItemRenderer {

    @org.spongepowered.asm.mixin.injection.Redirect(method = ["appendItemLayers"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;")]) private fun removeModel(instance: net.minecraft.world.item.ItemStack, componentType: net.minecraft.core.component.DataComponentType<*>): kotlin.Any {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().betweenInclusive(LegacyProtocolVersion.r1_3_1tor1_3_2, com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2) && instance.`is`(net.minecraft.world.item.Items.PETRIFIED_OAK_SLAB)) {
            return MixinItemRenderer.Companion.`viaFabricPlus$missingIdentifier`
        } else {
            return instance.get(componentType)
        }
    }
    
    private companion object {
        @field:org.spongepowered.asm.mixin.Unique 
        @JvmField val `viaFabricPlus$missingIdentifier`: net.minecraft.resources.Identifier = net.minecraft.resources.Identifier.parse(java.lang.System.currentTimeMillis().toString())
        
    }}
