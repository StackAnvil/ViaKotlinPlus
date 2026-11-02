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
package com.viaversion.viafabricplus.injection.mixin.features.v1_20_2.network

import org.spongepowered.asm.mixin.Mixin
import com.viaversion.viafabricplus.injection.access.v1_20_2.ILevelLoadingScreen
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(net.minecraft.client.gui.screens.LevelLoadingScreen::class) 
abstract class MixinLevelLoadingScreen (title: net.minecraft.network.chat.Component) : net.minecraft.client.gui.screens.Screen(title), ILevelLoadingScreen {

    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$loadStartTime`: kotlin.Long = 0L
    
    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$tickCounter`: kotlin.Int = 0
    
    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$ready`: kotlin.Boolean = false
    
    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$closeOnNextTick`: kotlin.Boolean = false
    
    init {
        this.`viaFabricPlus$loadStartTime` = net.minecraft.util.Util.getMillis()
    }
    
    @Inject(method = ["tick"], at = [At("HEAD")], cancellable = true) private fun modifyCloseCondition(ci: CallbackInfo) {
        if (net.minecraft.client.Minecraft.getInstance() != null && net.minecraft.client.Minecraft.getInstance().isLocalServer()) {
            // When joining the singleplayer, we set the target version to the native version when the integrated server is started,
            // However, this is already too late, and the screen was already opened (and ticked), causing NPEs due to the network handler being null
            return 
        }
        
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20_2)) {
            ci.cancel()
            
            if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_18)) {
                if (this.`viaFabricPlus$ready`) {
                    this.onClose()
                }
                
                if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_1)) {
                    this.`viaFabricPlus$tickCounter`++
                    if (this.`viaFabricPlus$tickCounter` % 20 == 0) {
                        this.minecraft.getConnection().send(net.minecraft.network.protocol.common.ServerboundKeepAlivePacket(0))
                    }
                }
            } else {
                if (java.lang.System.currentTimeMillis() > this.`viaFabricPlus$loadStartTime` + 30000L) {
                    this.onClose()
                } else {
                    if (this.`viaFabricPlus$closeOnNextTick`) {
                        if (this.minecraft.player == null) return 
                        
                        val blockPos: net.minecraft.core.BlockPos = this.minecraft.player.blockPosition()
                        val isOutOfHeightLimit: kotlin.Boolean = this.minecraft.level != null && this.minecraft.level.isOutsideBuildHeight(blockPos.getY())
                        val chunkFadeDuration: kotlin.Long = net.minecraft.util.Util.toMillis(this.minecraft.options.chunkSectionFadeInTime().get())
                        if (isOutOfHeightLimit || this.minecraft.levelRenderer.isSectionCompiledAndVisible(blockPos, chunkFadeDuration) || this.minecraft.player.isSpectator() || !this.minecraft.player.isAlive()) {
                            this.onClose()
                        }
                    } else {
                        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_19_1)) {
                            this.`viaFabricPlus$closeOnNextTick` = this.`viaFabricPlus$ready` || java.lang.System.currentTimeMillis() > this.`viaFabricPlus$loadStartTime` + 2000
                        } else {
                            this.`viaFabricPlus$closeOnNextTick` = this.`viaFabricPlus$ready`
                        }
                    }
                }
            }
        }
    }
    
    override fun `viaFabricPlus$setReady`() {
        this.`viaFabricPlus$ready` = true
    }
    
}
