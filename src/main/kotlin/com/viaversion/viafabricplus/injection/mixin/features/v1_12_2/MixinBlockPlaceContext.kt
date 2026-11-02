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
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import com.viaversion.viafabricplus.protocoltranslator.impl.ViaFabricPlusMappingDataLoader

@Mixin(net.minecraft.world.item.context.BlockPlaceContext::class) 
abstract class MixinBlockPlaceContext (player: net.minecraft.world.entity.player.Player, hand: net.minecraft.world.InteractionHand, hit: net.minecraft.world.phys.BlockHitResult) : net.minecraft.world.item.context.UseOnContext(player, hand, hit) {

    @Inject(method = ["getNearestLookingDirection"], at = [At("HEAD")], cancellable = true) private fun getPlayerLookDirection1_12_2(cir: CallbackInfoReturnable<net.minecraft.core.Direction>) {
        val self: net.minecraft.world.item.context.BlockPlaceContext = this as kotlin.Any as net.minecraft.world.item.context.BlockPlaceContext
        val player: net.minecraft.world.entity.player.Player = self.getPlayer()
        
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)) {
            val placementPos: net.minecraft.core.BlockPos = self.getClickedPos()
            val blockPosCenterFactor: kotlin.Double = if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_10)) 0.5 else 0.0
            
            if (kotlin.math.abs(player.getX() - (placementPos.getX() + blockPosCenterFactor)) < 2 && kotlin.math.abs(player.getZ() - (placementPos.getZ() + blockPosCenterFactor)) < 2) {
                val eyeY: kotlin.Double = player.getY() + player.getEyeHeight(player.getPose())
                
                if (eyeY - placementPos.getY() > 2) {
                    cir.setReturnValue(net.minecraft.core.Direction.DOWN)
                    return 
                }
                
                if (placementPos.getY() - eyeY > 0) {
                    cir.setReturnValue(net.minecraft.core.Direction.UP)
                    return 
                }
            }
            
            cir.setReturnValue(player.getDirection())
        }
    }
    
    @Inject(method = ["canPlace"], at = [At("RETURN")], cancellable = true) private fun canPlace1_12_2(cir: CallbackInfoReturnable<kotlin.Boolean>) {
        if (!cir.getReturnValueZ() && com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)) {
            cir.setReturnValue(ViaFabricPlusMappingDataLoader.getBlockMaterial(this.getLevel().getBlockState(this.getClickedPos()).getBlock()) == "decoration" && net.minecraft.world.level.block.Block.byItem(this.getItemInHand().getItem()) == net.minecraft.world.level.block.Blocks.ANVIL)
        }
    }
    
}
