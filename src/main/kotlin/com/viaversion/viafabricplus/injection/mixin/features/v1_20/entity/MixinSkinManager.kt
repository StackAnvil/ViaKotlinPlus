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
package com.viaversion.viafabricplus.injection.mixin.features.v1_20.entity

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import java.util.concurrent.CompletableFuture
import com.mojang.authlib.services.ProfileResult

@Mixin(net.minecraft.client.resources.SkinManager::class) 
class MixinSkinManager {
private companion object {
        @JvmStatic @org.spongepowered.asm.mixin.injection.Redirect(method = ["createLookup"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/resources/SkinManager;get(Lcom/mojang/authlib/GameProfile;)Ljava/util/concurrent/CompletableFuture;")]) fun fetchGameProfileProperties(instance: net.minecraft.client.resources.SkinManager, profile: com.mojang.authlib.GameProfile): CompletableFuture<java.util.Optional<net.minecraft.world.entity.player.PlayerSkin>> {
            if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20) && !profile.properties().containsKey("textures")) {
                return CompletableFuture.supplyAsync<com.mojang.authlib.GameProfile>(java.util.function.Supplier {
                val profileResult: ProfileResult = net.minecraft.client.Minecraft.getInstance().services().sessionService().fetchProfile(profile.id(), true)!!
                if (profileResult == null) profile else profileResult.profile()
                }, net.minecraft.util.Util.backgroundExecutor()).thenCompose<java.util.Optional<net.minecraft.world.entity.player.PlayerSkin>>(java.util.function.Function {profile: com.mojang.authlib.GameProfile -> instance.get(profile)})
            } else {
                return instance.get(profile)
            }
        }
    
    }
}
