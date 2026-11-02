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
import com.viaversion.viaversion.protocols.v1_8to1_9.rewriter.EntityPacketRewriter1_9
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.objectweb.asm.Opcodes
import com.viaversion.viaversion.rewriter.entitydata.EntityDataHandlerEvent
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.viaversion.viaversion.api.connection.ProtocolStorables
import com.viaversion.viaversion.protocols.v1_8to1_9.Protocol1_8To1_9

@Mixin(value = [EntityPacketRewriter1_9::class], remap = false) 
abstract class MixinEntityPacketRewriter1_9 {

    @Inject(method = ["handleEntityData"], at = [At(value = "FIELD", target = "Lcom/viaversion/viaversion/api/minecraft/entities/EntityTypes1_9\$EntityType;PLAYER:Lcom/viaversion/viaversion/api/minecraft/entities/EntityTypes1_9\$EntityType;", ordinal = 0, opcode = Opcodes.GETSTATIC)], cancellable = true) private fun preventMetadataForClientPlayer(event: EntityDataHandlerEvent, data: com.viaversion.viaversion.api.minecraft.entitydata.EntityData, ci: CallbackInfo) {
        if (event.user().storables<ProtocolStorables>(Protocol1_8To1_9::class.java).entityTracker().clientEntityId() == event.entityId()) {
            ci.cancel()
        }
    }
    
}
