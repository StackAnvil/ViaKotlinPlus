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
package com.viaversion.viafabricplus.injection.mixin.features.v1_15_2

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import net.raphimc.vialegacy.api.LegacyProtocolVersion

@Mixin(net.minecraft.client.multiplayer.MultiPlayerGameMode::class) 
abstract class MixinMultiPlayerGameMode {

    @org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField val minecraft: net.minecraft.client.Minecraft = uninitialized()
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["lambda\$startDestroyBlock$0", "lambda\$continueDestroyBlock$0"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;destroyBlock(Lnet/minecraft/core/BlockPos;)Z")]) private fun checkFireBlock(instance: net.minecraft.client.multiplayer.MultiPlayerGameMode, pos: net.minecraft.core.BlockPos, @com.llamalad7.mixinextras.sugar.Local(argsOnly = true) direction: net.minecraft.core.Direction): kotlin.Boolean {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_15_2)) {
            return !this.`viaFabricPlus$extinguishFire`(pos, direction) && instance.destroyBlock(pos)
        } else {
            return instance.destroyBlock(pos)
        }
    }
    
    @Inject(method = ["handleContainerInput"], at = [At("HEAD")], cancellable = true) private fun removeClickActions(containerId: kotlin.Int, slotNum: kotlin.Int, buttonNum: kotlin.Int, containerInput: net.minecraft.world.inventory.ContainerInput, player: net.minecraft.world.entity.player.Player, ci: CallbackInfo) {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(LegacyProtocolVersion.b1_5tob1_5_2) && containerInput != net.minecraft.world.inventory.ContainerInput.PICKUP) {
            ci.cancel()
        } else if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_4_6tor1_4_7) && (containerInput != net.minecraft.world.inventory.ContainerInput.PICKUP) && (containerInput != net.minecraft.world.inventory.ContainerInput.QUICK_MOVE) && (containerInput != net.minecraft.world.inventory.ContainerInput.SWAP) && (containerInput != net.minecraft.world.inventory.ContainerInput.CLONE)) {
            ci.cancel()
        }
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_15_2) && containerInput == net.minecraft.world.inventory.ContainerInput.SWAP && buttonNum == 40) { // Pressing 'F' in inventory
            ci.cancel()
        }
    }
    
    @org.spongepowered.asm.mixin.Unique private fun `viaFabricPlus$extinguishFire`(blockPos: net.minecraft.core.BlockPos, direction: net.minecraft.core.Direction): kotlin.Boolean {
        var blockPos: net.minecraft.core.BlockPos = blockPos
        blockPos = blockPos.relative(direction)
        if (this.minecraft.level.getBlockState(blockPos).getBlock() === net.minecraft.world.level.block.Blocks.FIRE) {
            this.minecraft.level.levelEvent(this.minecraft.player, net.minecraft.world.level.block.LevelEvent.SOUND_EXTINGUISH_FIRE, blockPos, 0)
            this.minecraft.level.removeBlock(blockPos, false)
            return true
        }
        return false
    }
    
}
