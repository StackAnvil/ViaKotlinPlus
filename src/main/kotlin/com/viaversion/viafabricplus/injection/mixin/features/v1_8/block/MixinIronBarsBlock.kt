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
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
import org.spongepowered.asm.mixin.injection.At
import com.llamalad7.mixinextras.sugar.Share
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(net.minecraft.world.level.block.IronBarsBlock::class) 
abstract class MixinIronBarsBlock protected constructor(radius1: kotlin.Float, radius2: kotlin.Float, boundingHeight1: kotlin.Float, boundingHeight2: kotlin.Float, collisionHeight: kotlin.Float, settings: net.minecraft.world.level.block.state.BlockBehaviour.Properties) : net.minecraft.world.level.block.CrossCollisionBlock(radius1, radius2, boundingHeight1, boundingHeight2, collisionHeight, settings) {

    @WrapOperation(method = ["getStateForPlacement"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/IronBarsBlock;attachsTo(Lnet/minecraft/world/level/block/state/BlockState;Z)Z")]) private fun countConnections(instance: net.minecraft.world.level.block.IronBarsBlock, state: net.minecraft.world.level.block.state.BlockState, faceSolid: kotlin.Boolean, original: com.llamalad7.mixinextras.injector.wrapoperation.Operation<kotlin.Boolean>, @Share("count") countRef: LocalIntRef): kotlin.Boolean {
        val connectsTo: kotlin.Boolean = original.call(instance, state, faceSolid)
        if (connectsTo) {
            countRef.set(countRef.get() + 1)
        }
        return connectsTo
    }
    
    @Inject(method = ["getStateForPlacement"], at = [At("RETURN")], cancellable = true) private fun changePlacementState(context: net.minecraft.world.item.context.BlockPlaceContext, cir: CallbackInfoReturnable<net.minecraft.world.level.block.state.BlockState>, @Share("count") countRef: LocalIntRef) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8) && countRef.get() == 0) {
            cir.setReturnValue(cir.getReturnValue().setValue<kotlin.Boolean, kotlin.Boolean>(net.minecraft.world.level.block.CrossCollisionBlock.NORTH, true).setValue<kotlin.Boolean, kotlin.Boolean>(net.minecraft.world.level.block.CrossCollisionBlock.SOUTH, true).setValue<kotlin.Boolean, kotlin.Boolean>(net.minecraft.world.level.block.CrossCollisionBlock.WEST, true).setValue<kotlin.Boolean, kotlin.Boolean>(net.minecraft.world.level.block.CrossCollisionBlock.EAST, true))
        }
    }
    
}
