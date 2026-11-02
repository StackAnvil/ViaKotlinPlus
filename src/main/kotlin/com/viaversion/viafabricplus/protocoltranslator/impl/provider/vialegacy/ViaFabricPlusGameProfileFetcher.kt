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
package com.viaversion.viafabricplus.protocoltranslator.impl.provider.vialegacy

import net.raphimc.vialegacy.protocol.release.r1_7_6_10tor1_8.provider.GameProfileFetcher
import java.util.UUID
import com.viaversion.viafabricplus.protocoltranslator.impl.provider.vialegacy.ViaFabricPlusGameProfileFetcher
import com.mojang.authlib.services.ProfileNotFoundException
import com.mojang.authlib.services.ProfileResult
import com.mojang.authlib.services.MinecraftServicesDiscoveryService
import com.mojang.authlib.minecraft.SessionService
import com.mojang.authlib.GameProfileRepository

class ViaFabricPlusGameProfileFetcher : GameProfileFetcher() {

    override fun loadMojangUuid(playerName: kotlin.String): UUID {
        val nameAndId: java.util.Optional<com.mojang.authlib.services.response.NameAndId> = ViaFabricPlusGameProfileFetcher.Companion.GAME_PROFILE_REPOSITORY.findProfileByName(playerName)
        if (nameAndId.isEmpty()) {
            throw ProfileNotFoundException()
        }
        
        return nameAndId.get().id()
    }
    
    override fun loadGameProfile(uuid: UUID): com.viaversion.viaversion.api.minecraft.GameProfile {
        val result: ProfileResult = ViaFabricPlusGameProfileFetcher.Companion.SESSION_SERVICE.fetchProfile(uuid, true)!!
        if (result == null) {
            throw ProfileNotFoundException()
        }
        
        val gameProfile: com.mojang.authlib.GameProfile = result.profile()
        val properties = mutableListOf<com.viaversion.viaversion.api.minecraft.GameProfile.Property>()
        for (entry in gameProfile.properties().entries()) {
            properties.add(com.viaversion.viaversion.api.minecraft.GameProfile.Property(entry.value.name(), entry.value.value(), entry.value.signature()))
        }
        return com.viaversion.viaversion.api.minecraft.GameProfile(gameProfile.name(), gameProfile.id(), properties.toTypedArray())
    }
    
    companion object {
        private val DISCOVERY_SERVICE: com.mojang.authlib.DiscoveryService = MinecraftServicesDiscoveryService.create(java.net.Proxy.NO_PROXY)
        private val SESSION_SERVICE: SessionService = ViaFabricPlusGameProfileFetcher.Companion.DISCOVERY_SERVICE.createMinecraftSessionService()
        private val GAME_PROFILE_REPOSITORY: GameProfileRepository = ViaFabricPlusGameProfileFetcher.Companion.DISCOVERY_SERVICE.createProfileRepository()
        
    }}
