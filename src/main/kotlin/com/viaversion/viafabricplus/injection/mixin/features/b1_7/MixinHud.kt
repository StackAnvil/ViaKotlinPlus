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
package com.viaversion.viafabricplus.injection.mixin.features.b1_7

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import com.viaversion.viafabricplus.ViaFabricPlusImpl
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.llamalad7.mixinextras.injector.ModifyExpressionValue
import org.spongepowered.asm.mixin.injection.ModifyArg
import org.spongepowered.asm.mixin.injection.ModifyArgs
import com.viaversion.viafabricplus.injection.mixin.features.b1_7.MixinHud

@Mixin(net.minecraft.client.gui.Hud::class) 
abstract class MixinHud {

    @Inject(method = ["willPrioritizeJumpInfo"], at = [At("HEAD")], cancellable = true) private fun removeMountJumpBar(cir: CallbackInfoReturnable<kotlin.Boolean>) {
        if (ViaFabricPlusImpl.impl().visuals().hideModernHUDElements().isActive()) {
            cir.setReturnValue(false)
        }
    }
    
    @Inject(method = ["extractVehicleHealth"], at = [At("HEAD")], cancellable = true) private fun removeMountJumpBar(ci: CallbackInfo) {
        if (ViaFabricPlusImpl.impl().visuals().hideModernHUDElements().isActive()) {
            ci.cancel()
        }
    }
    
    @Inject(method = ["getVehicleMaxHearts"], at = [At("HEAD")], cancellable = true) private fun removeHungerBar(vehicle: net.minecraft.world.entity.LivingEntity, cir: CallbackInfoReturnable<kotlin.Int>) {
        if (ViaFabricPlusImpl.impl().visuals().hideModernHUDElements().isActive()) {
            cir.setReturnValue(1)
        }
    }
    
    @ModifyExpressionValue(method = ["extractPlayerHealth"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;guiHeight()I")], require = 0) private fun moveHealthDown(value: kotlin.Int): kotlin.Int {
        if (ViaFabricPlusImpl.impl().visuals().hideModernHUDElements().isActive()) {
            return value + 7 // Magical offset
        } else {
            return value
        }
    }
    
    @ModifyArg(method = ["extractAirBubbles"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V")], index = 2, require = 0) private fun moveAirBubbles(value: kotlin.Int): kotlin.Int {
        if (ViaFabricPlusImpl.impl().visuals().hideModernHUDElements().isActive()) {
            val client: net.minecraft.client.Minecraft = net.minecraft.client.Minecraft.getInstance()
            return client.getWindow().getGuiScaledWidth() - value - client.font.lineHeight
        } else {
            return value
        }
    }
    
    private companion object {
        @field:org.spongepowered.asm.mixin.Unique 
        @JvmField val `viaFabricPlus$ARMOR_ICON_WIDTH`: kotlin.Int = 8
        
        @JvmStatic @ModifyArgs(method = ["extractArmor"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V")], require = 0) fun moveArmorPositions(args: org.spongepowered.asm.mixin.injection.invoke.arg.Args) {
            if (ViaFabricPlusImpl.impl().visuals().hideModernHUDElements().isActive()) {
                val width: kotlin.Int = 10 * MixinHud.Companion.`viaFabricPlus$ARMOR_ICON_WIDTH`
                args.set<kotlin.Int>(2, args.get<kotlin.Any>(2) as kotlin.Int + width + 21)
                args.set<kotlin.Int>(3, args.get<kotlin.Any>(3) as kotlin.Int + 10)
            }
        }
        
    }}
