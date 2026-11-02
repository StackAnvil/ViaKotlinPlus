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
package com.viaversion.viafabricplus.injection.mixin.features.v1_14_4.movement

import org.spongepowered.asm.mixin.Mixin

@Mixin(net.minecraft.world.level.block.SoulSandBlock::class) 
abstract class MixinSoulSandBlock (settings: net.minecraft.world.level.block.state.BlockBehaviour.Properties) : net.minecraft.world.level.block.Block(settings) {

    override fun entityInside(state: net.minecraft.world.level.block.state.BlockState, world: net.minecraft.world.level.Level, pos: net.minecraft.core.BlockPos, entity: net.minecraft.world.entity.Entity, handler: net.minecraft.world.entity.InsideBlockEffectApplier, bl: kotlin.Boolean) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_14_4)) {
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.4, 1.0, 0.4))
        }
    }
    
    override fun getSpeedFactor(): kotlin.Float {
        return if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_14_4)) 1f else super.getSpeedFactor()
    }
    
}
