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
package com.viaversion.viafabricplus.injection.mixin.features.v1_21_11.entity

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.objectweb.asm.Opcodes
import org.spongepowered.asm.mixin.Final

@Mixin(net.minecraft.world.entity.animal.equine.SkeletonHorse::class) 
abstract class MixinSkeletonHorse {

    @org.spongepowered.asm.mixin.injection.Redirect(method = ["getDefaultDimensions"], at = [At(value = "FIELD", target = "Lnet/minecraft/world/entity/animal/equine/SkeletonHorse;BABY_DIMENSIONS:Lnet/minecraft/world/entity/EntityDimensions;", opcode = Opcodes.GETSTATIC)]) private fun changeBabyDimensions(): net.minecraft.world.entity.EntityDimensions {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_11)) {
            return com.viaversion.viafabricplus.injection.mixin.features.v1_21_11.entity.MixinSkeletonHorse.Companion.`viaFabricPlus$baby_dimensions_r1_21_11`
        } else {
            return com.viaversion.viafabricplus.injection.mixin.features.v1_21_11.entity.MixinSkeletonHorse.Companion.BABY_DIMENSIONS
        }
    }
    
    private companion object {
        @field:org.spongepowered.asm.mixin.Shadow @Final 
        @JvmField val BABY_DIMENSIONS: net.minecraft.world.entity.EntityDimensions = uninitialized()
        @field:org.spongepowered.asm.mixin.Unique 
        @JvmField val `viaFabricPlus$baby_dimensions_r1_21_11`: net.minecraft.world.entity.EntityDimensions = net.minecraft.world.entity.EntityTypes.SKELETON_HORSE
        .getDimensions()
        .withAttachments(net.minecraft.world.entity.EntityAttachments.builder().attach(net.minecraft.world.entity.EntityAttachment.PASSENGER, 0.0f, net.minecraft.world.entity.EntityTypes.SKELETON_HORSE.getHeight() - 0.03125f, 0.0f))
        .scale(0.5f)
        
    }}
