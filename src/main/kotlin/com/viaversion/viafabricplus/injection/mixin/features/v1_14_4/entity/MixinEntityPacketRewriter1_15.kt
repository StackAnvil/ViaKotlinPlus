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
package com.viaversion.viafabricplus.injection.mixin.features.v1_14_4.entity

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import com.viaversion.viaversion.rewriter.entitydata.EntityDataFilter
import com.viaversion.viaversion.rewriter.entitydata.EntityDataHandler
import com.viaversion.viaversion.rewriter.entitydata.EntityDataHandlerEvent
import com.viaversion.viafabricplus.features.v1_21_5.WolfHealthTracker1_14_4

@Mixin(value = [com.viaversion.viaversion.protocols.v1_14_4to1_15.rewriter.EntityPacketRewriter1_15::class], remap = false) 
abstract class MixinEntityPacketRewriter1_15 {

    @org.spongepowered.asm.mixin.injection.Redirect(method = ["registerRewrites"], at = [At(value = "INVOKE", target = "Lcom/viaversion/viaversion/rewriter/entitydata/EntityDataFilter\$Builder;removeIndex(I)V")]) private fun removeAndTrackHealth(instance: EntityDataFilter.Builder, index: kotlin.Int) {
        instance.handler(EntityDataHandler {
        event: EntityDataHandlerEvent, meta: com.viaversion.viaversion.api.minecraft.entitydata.EntityData -> val metaIndex: kotlin.Int = event.index()
        if (metaIndex == index) {
            event.user().get<WolfHealthTracker1_14_4>(WolfHealthTracker1_14_4::class.java)!!.setWolfHealth(event.entityId(), meta.value<kotlin.Float>()!!)
            event.cancel()
        } else if (metaIndex > index) {
            event.setIndex(metaIndex - 1)
        }
        
        })
    }
    
}
