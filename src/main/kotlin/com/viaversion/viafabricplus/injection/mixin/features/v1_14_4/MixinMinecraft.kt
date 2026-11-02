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
package com.viaversion.viafabricplus.injection.mixin.features.v1_14_4

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At

@Mixin(net.minecraft.client.Minecraft::class) 
abstract class MixinMinecraft {

    @org.spongepowered.asm.mixin.injection.Redirect(method = ["startUseItem"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/InteractionResult\$Success;swingSource()Lnet/minecraft/world/InteractionResult\$SwingSource;", ordinal = 0)]) private fun disableSwing(instance: net.minecraft.world.InteractionResult.Success): net.minecraft.world.InteractionResult.SwingSource {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_14_4)) {
            return net.minecraft.world.InteractionResult.SwingSource.NONE
        } else {
            return instance.swingSource()
        }
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["startUseItem"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/InteractionResult\$Success;swingSource()Lnet/minecraft/world/InteractionResult\$SwingSource;", ordinal = 2)]) private fun disableSwing2(instance: net.minecraft.world.InteractionResult.Success): net.minecraft.world.InteractionResult.SwingSource {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_14_4)) {
            return net.minecraft.world.InteractionResult.SwingSource.NONE
        } else {
            return instance.swingSource()
        }
    }
    
}
