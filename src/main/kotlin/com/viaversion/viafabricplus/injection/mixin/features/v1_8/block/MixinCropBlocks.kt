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
package com.viaversion.viafabricplus.injection.mixin.features.v1_8.block

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import com.viaversion.viafabricplus.ViaFabricPlusImpl
import com.viaversion.viafabricplus.injection.mixin.features.v1_8.block.MixinCropBlocks

@Mixin(net.minecraft.world.level.block.CropBlock::class, net.minecraft.world.level.block.CarrotBlock::class, net.minecraft.world.level.block.PotatoBlock::class) 
abstract class MixinCropBlocks {

    @Inject(method = ["getShape"], at = [At("HEAD")], cancellable = true) private fun changeOutlineShape(cir: CallbackInfoReturnable<net.minecraft.world.phys.shapes.VoxelShape>) {
        if (ViaFabricPlusImpl.impl().visuals().legacyCropOutlines().isActive()) {
            cir.setReturnValue(MixinCropBlocks.Companion.`viaFabricPlus$shape_r1_8_x`)
        }
    }
    
    
    
    
    private companion object {
        @field:org.spongepowered.asm.mixin.Unique 
        @JvmField val `viaFabricPlus$shape_r1_8_x`: net.minecraft.world.phys.shapes.VoxelShape = net.minecraft.world.level.block.Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0)
        
    }}
