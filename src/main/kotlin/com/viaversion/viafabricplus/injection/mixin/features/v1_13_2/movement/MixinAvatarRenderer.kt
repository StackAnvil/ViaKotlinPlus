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
package com.viaversion.viafabricplus.injection.mixin.features.v1_13_2.movement

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import net.raphimc.vialegacy.api.LegacyProtocolVersion

@Mixin(net.minecraft.client.renderer.entity.player.AvatarRenderer::class) 
abstract class MixinAvatarRenderer {

    @Inject(method = ["getRenderOffset(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)Lnet/minecraft/world/phys/Vec3;"], at = [At("RETURN")], cancellable = true) private fun modifySleepingOffset(state: net.minecraft.client.renderer.entity.state.AvatarRenderState, cir: CallbackInfoReturnable<net.minecraft.world.phys.Vec3>) {
        if (state.pose == net.minecraft.world.entity.Pose.SLEEPING) {
            val sleepingDir: net.minecraft.core.Direction = state.bedOrientation
            if (sleepingDir != null) {
                if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_13_2)) {
                    cir.setReturnValue(cir.getReturnValue().subtract(sleepingDir.getStepX() * 0.4, 0.0, sleepingDir.getStepZ() * 0.4))
                }
                if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(LegacyProtocolVersion.b1_5tob1_5_2)) {
                    cir.setReturnValue(cir.getReturnValue().subtract(sleepingDir.getStepX() * 0.1, 0.0, sleepingDir.getStepZ() * 0.1))
                }
                if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().betweenInclusive(LegacyProtocolVersion.b1_6tob1_6_6, com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_7_6)) {
                    cir.setReturnValue(cir.getReturnValue().subtract(0.0, 0.3, 0.0))
                }
            }
        }
    }
    
}
