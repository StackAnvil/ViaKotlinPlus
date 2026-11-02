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
package com.viaversion.viafabricplus.injection.mixin.features.global

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import com.viaversion.viafabricplus.features.global.ResourcePackHeaderDiff
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import java.util.LinkedHashMap

@Mixin(targets = ["net.minecraft.client.resources.server.DownloadedPackSource$4"]) 
abstract class MixinDownloadedPackSource_4 {

    @org.spongepowered.asm.mixin.injection.Redirect(method = ["createDownloadHeaders"], at = [At(value = "INVOKE", target = "Lnet/minecraft/SharedConstants;getCurrentVersion()Lnet/minecraft/WorldVersion;")]) private fun editHeaders(): net.minecraft.WorldVersion {
        return ResourcePackHeaderDiff.get(com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion())
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["createDownloadHeaders"], at = [At(value = "INVOKE", target = "Ljava/lang/String;valueOf(Ljava/lang/Object;)Ljava/lang/String;")]) private fun editHeaders(obj: kotlin.Any): kotlin.String {
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_7) && obj is net.minecraft.server.packs.metadata.pack.PackFormat) {
            return obj.major().toString()
        } else {
            return obj.toString()
        }
    }
    
    @Inject(method = ["createDownloadHeaders"], at = [At("TAIL")], cancellable = true) private fun removeHeaders(cir: CallbackInfoReturnable<kotlin.collections.MutableMap<kotlin.String, kotlin.String>>) {
        val modifiableMap: LinkedHashMap<kotlin.String, kotlin.String> = LinkedHashMap<kotlin.String, kotlin.String>(cir.getReturnValue())
        if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_14_3)) {
            modifiableMap.remove("X-Minecraft-Version-ID")
            if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)) {
                modifiableMap.remove("X-Minecraft-Pack-Format")
                modifiableMap.remove("User-Agent")
            }
        }
        cir.setReturnValue(modifiableMap)
    }
    
}
