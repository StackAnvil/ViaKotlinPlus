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
package com.viaversion.viafabricplus.injection.mixin.features.v1_17

import org.spongepowered.asm.mixin.Mixin
import com.viaversion.viafabricplus.injection.access.v1_17.IEntity

@Mixin(net.minecraft.world.entity.Entity::class) 
abstract class MixinEntity : IEntity {

    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$isInLoadedChunkAndShouldTick`: kotlin.Boolean = false
    
    override fun `viaFabricPlus$isInLoadedChunkAndShouldTick`(): kotlin.Boolean {
        return this.`viaFabricPlus$isInLoadedChunkAndShouldTick` || !com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().betweenExclusive(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8, com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_17)
    }
    
    override fun `viaFabricPlus$setInLoadedChunkAndShouldTick`(inLoadedChunkAndShouldTick: kotlin.Boolean) {
        this.`viaFabricPlus$isInLoadedChunkAndShouldTick` = inLoadedChunkAndShouldTick
    }
    
}
