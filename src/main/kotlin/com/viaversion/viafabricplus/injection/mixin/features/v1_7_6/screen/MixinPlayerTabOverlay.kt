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
package com.viaversion.viafabricplus.injection.mixin.features.v1_7_6.screen

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import com.viaversion.viafabricplus.injection.access.v1_7_6.IPlayerTabOverlay
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import com.viaversion.viafabricplus.ViaFabricPlusImpl
import com.viaversion.viafabricplus.injection.mixin.features.v1_7_6.screen.MixinPlayerTabOverlay
import java.util.stream.Collectors
import com.llamalad7.mixinextras.injector.ModifyExpressionValue
import java.util.function.ToIntFunction
import com.viaversion.viafabricplus.injection.access.v1_7_6.IPlayerInfo

@Mixin(net.minecraft.client.gui.components.PlayerTabOverlay::class) 
abstract class MixinPlayerTabOverlay : IPlayerTabOverlay {

    @field:org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField val minecraft: net.minecraft.client.Minecraft = uninitialized()
    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$maxSlots`: kotlin.Int = 0
    
    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$hideSkins`: kotlin.Boolean = true
    
    @Inject(method = ["getPlayerInfos"], at = [At("HEAD")], cancellable = true) private fun collectPlayerEntries(result: CallbackInfoReturnable<kotlin.collections.MutableList<net.minecraft.client.multiplayer.PlayerInfo>>) {
        if (ViaFabricPlusImpl.impl().visuals().enableLegacyTablist().isActive()) {
            result.setReturnValue(this.minecraft.player.connection.getListedOnlinePlayers().stream()
            .sorted(java.util.Comparator.comparingInt<net.minecraft.client.multiplayer.PlayerInfo>(java.util.function.ToIntFunction { e: net.minecraft.client.multiplayer.PlayerInfo -> (e as IPlayerInfo).`viaFabricPlus$getIndex`() }))
            .limit(`viaFabricPlus$maxSlots`.toLong())
            .collect(Collectors.collectingAndThen(Collectors.toList(), java.util.function.Function {list: kotlin.collections.MutableList<net.minecraft.client.multiplayer.PlayerInfo> -> this.`viaFabricPlus$transpose`(list)})))
        } else {
            `viaFabricPlus$hideSkins` = false
        }
    }
    
    @ModifyExpressionValue(method = ["extractRenderState"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;onlineMode()Z")]) private fun hideSkins(original: kotlin.Boolean): kotlin.Boolean {
        return original && !`viaFabricPlus$hideSkins`
    }
    
    override fun `viaFabricPlus$setMaxPlayers`(maxPlayers: kotlin.Int) {
        this.`viaFabricPlus$maxSlots` = java.lang.Math.clamp((((maxPlayers + net.minecraft.client.gui.components.PlayerTabOverlay.MAX_ROWS_PER_COL - 1) / net.minecraft.client.gui.components.PlayerTabOverlay.MAX_ROWS_PER_COL) * net.minecraft.client.gui.components.PlayerTabOverlay.MAX_ROWS_PER_COL).toLong(), 20, 200)
    }
    
    @org.spongepowered.asm.mixin.Unique private fun `viaFabricPlus$transpose`(list: kotlin.collections.MutableList<net.minecraft.client.multiplayer.PlayerInfo>): kotlin.collections.MutableList<net.minecraft.client.multiplayer.PlayerInfo> {
        // Only bother transposing if we know the list is full
        if (list.size != `viaFabricPlus$maxSlots`) {
            `viaFabricPlus$hideSkins` = list.stream().noneMatch {e: net.minecraft.client.multiplayer.PlayerInfo -> e.getProfile().properties().containsKey("textures")} 
            return list
        }
        
        val result: kotlin.collections.MutableList<net.minecraft.client.multiplayer.PlayerInfo> = java.util.ArrayList<net.minecraft.client.multiplayer.PlayerInfo>(list.size)
        
        val columns: kotlin.Int = `viaFabricPlus$maxSlots` / net.minecraft.client.gui.components.PlayerTabOverlay.MAX_ROWS_PER_COL
        var anyHasSkinData: kotlin.Boolean = false
        for (i in 0 ..< `viaFabricPlus$maxSlots`) {
            val row: kotlin.Int = i % net.minecraft.client.gui.components.PlayerTabOverlay.MAX_ROWS_PER_COL
            val col: kotlin.Int = i / net.minecraft.client.gui.components.PlayerTabOverlay.MAX_ROWS_PER_COL
            val current: net.minecraft.client.multiplayer.PlayerInfo = list.get(row * columns + col)
            result.add(current)
            anyHasSkinData = anyHasSkinData || current.getProfile().properties().containsKey("textures")
        }
        `viaFabricPlus$hideSkins` = !anyHasSkinData
        return result
    }
    
}
