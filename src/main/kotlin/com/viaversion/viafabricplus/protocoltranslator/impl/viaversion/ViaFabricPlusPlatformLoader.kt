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
package com.viaversion.viafabricplus.protocoltranslator.impl.viaversion

import com.viaversion.viaversion.api.platform.ViaPlatformLoader
import com.viaversion.viaversion.api.platform.providers.ViaProviders
import com.viaversion.viaversion.api.Via
import com.viaversion.viaversion.api.protocol.version.VersionProvider
import com.viaversion.viafabricplus.protocoltranslator.impl.provider.viaversion.ViaFabricPlusBaseVersionProvider
import com.viaversion.viaversion.protocols.v1_8to1_9.provider.HandItemProvider
import com.viaversion.viafabricplus.protocoltranslator.impl.provider.viaversion.ViaFabricPlusHandItemProvider
import com.viaversion.viaversion.protocols.v1_12_2to1_13.provider.PlayerLookTargetProvider
import com.viaversion.viafabricplus.protocoltranslator.impl.provider.viaversion.ViaFabricPlusPlayerLookTargetProvider
import com.viaversion.viaversion.protocols.v1_15_2to1_16.provider.PlayerAbilitiesProvider
import com.viaversion.viafabricplus.protocoltranslator.impl.provider.viaversion.ViaFabricPlusPlayerAbilitiesProvider
import com.viaversion.viaversion.api.minecraft.signature.SignableCommandArgumentsProvider
import com.viaversion.viafabricplus.protocoltranslator.impl.provider.viaversion.ViaFabricPlusCommandArgumentsProvider
import com.viaversion.viaversion.protocols.v1_18_2to1_19.provider.AckSequenceProvider
import com.viaversion.viafabricplus.protocoltranslator.impl.provider.viaversion.ViaFabricPlusAckSequenceProvider
import com.viaversion.viaversion.protocols.v1_21_2to1_21_4.provider.PickItemProvider
import com.viaversion.viafabricplus.protocoltranslator.impl.provider.viaversion.ViaFabricPlusPickItemProvider
import com.viaversion.viaversion.protocols.v1_8to1_9.provider.CompressionProvider
import com.viaversion.viafabricplus.protocoltranslator.impl.provider.viaversion.ViaFabricPlusCompressionProvider
import net.raphimc.vialegacy.protocol.release.r1_2_4_5tor1_3_1_2.provider.OldAuthProvider
import com.viaversion.viafabricplus.protocoltranslator.impl.provider.vialegacy.ViaFabricPlusOldAuthProvider
import net.raphimc.vialegacy.protocol.classic.c0_28_30toa1_0_15.provider.ClassicWorldHeightProvider
import com.viaversion.viafabricplus.protocoltranslator.impl.provider.vialegacy.ViaFabricPlusClassicWorldHeightProvider
import net.raphimc.vialegacy.protocol.release.r1_6_4tor1_7_2_5.provider.EncryptionProvider
import com.viaversion.viafabricplus.protocoltranslator.impl.provider.vialegacy.ViaFabricPlusEncryptionProvider
import net.raphimc.vialegacy.protocol.release.r1_7_6_10tor1_8.provider.GameProfileFetcher
import com.viaversion.viafabricplus.protocoltranslator.impl.provider.vialegacy.ViaFabricPlusGameProfileFetcher
import net.raphimc.vialegacy.protocol.classic.c0_28_30toa1_0_15.provider.ClassicMpPassProvider
import com.viaversion.viafabricplus.protocoltranslator.impl.provider.vialegacy.ViaFabricPlusClassicMPPassProvider

class ViaFabricPlusPlatformLoader : ViaPlatformLoader {

    override fun load() {
        val providers: ViaProviders = Via.getManager().getProviders()
        
        providers.use<VersionProvider>(VersionProvider::class.java, ViaFabricPlusBaseVersionProvider())
        providers.use<HandItemProvider>(HandItemProvider::class.java, ViaFabricPlusHandItemProvider())
        providers.use<PlayerLookTargetProvider>(PlayerLookTargetProvider::class.java, ViaFabricPlusPlayerLookTargetProvider())
        providers.use<PlayerAbilitiesProvider>(PlayerAbilitiesProvider::class.java, ViaFabricPlusPlayerAbilitiesProvider())
        providers.use<SignableCommandArgumentsProvider>(SignableCommandArgumentsProvider::class.java, ViaFabricPlusCommandArgumentsProvider())
        providers.use<AckSequenceProvider>(AckSequenceProvider::class.java, ViaFabricPlusAckSequenceProvider())
        providers.use<PickItemProvider>(PickItemProvider::class.java, ViaFabricPlusPickItemProvider())
        providers.use<CompressionProvider>(CompressionProvider::class.java, ViaFabricPlusCompressionProvider())
        
        providers.use<OldAuthProvider>(OldAuthProvider::class.java, ViaFabricPlusOldAuthProvider())
        providers.use<ClassicWorldHeightProvider>(ClassicWorldHeightProvider::class.java, ViaFabricPlusClassicWorldHeightProvider())
        providers.use<EncryptionProvider>(EncryptionProvider::class.java, ViaFabricPlusEncryptionProvider())
        providers.use<GameProfileFetcher>(GameProfileFetcher::class.java, ViaFabricPlusGameProfileFetcher())
        providers.use<ClassicMpPassProvider>(ClassicMpPassProvider::class.java, ViaFabricPlusClassicMPPassProvider())
        
        net.fabricmc.loader.api.FabricLoader.getInstance().invokeEntrypoints<com.viaversion.viafabricplus.api.entrypoint.ViaFabricPlusEntrypoint>("viafabricplus", com.viaversion.viafabricplus.api.entrypoint.ViaFabricPlusEntrypoint::class.java, java.util.function.Consumer {obj: com.viaversion.viafabricplus.api.entrypoint.ViaFabricPlusEntrypoint -> obj.onPostProtocolTranslationLoading()})
    }
    
    override fun unload() {
    }
    
}
