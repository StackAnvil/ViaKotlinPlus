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
package com.viaversion.viafabricplus.injection.mixin.features.v1_14

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.Overwrite

@Mixin(net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase::class) 
abstract class MixinBlockBehaviour_BlockStateBase {

    @field:org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField val requiresCorrectToolForDrops: kotlin.Boolean = false
    
    @org.spongepowered.asm.mixin.Shadow abstract fun getBlock(): net.minecraft.world.level.block.Block
    
     /**
 * @author RK_01
 * @reason Change break speed for shulker blocks in < 1.14
 */
    @Overwrite fun requiresCorrectToolForDrops(): kotlin.Boolean {
        if (this.getBlock() is net.minecraft.world.level.block.ShulkerBoxBlock && com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_14)) {
            return true
        } else {
            return this.requiresCorrectToolForDrops
        }
    }
    
}
