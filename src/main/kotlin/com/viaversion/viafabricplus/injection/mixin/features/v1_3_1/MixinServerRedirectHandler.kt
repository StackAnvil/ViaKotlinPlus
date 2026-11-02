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
package com.viaversion.viafabricplus.injection.mixin.features.v1_3_1

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import javax.naming.directory.DirContext
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import net.raphimc.vialegacy.api.LegacyProtocolVersion

@Mixin(net.minecraft.client.multiplayer.resolver.ServerRedirectHandler::class) 
interface MixinServerRedirectHandler {
    private companion object {

            @JvmStatic @Inject(method = ["lambda\$createDnsSrvRedirectHandler$0"], at = [At("HEAD")], cancellable = true) fun disableSrvForPre1_3(context: DirContext, originalAddress: net.minecraft.client.multiplayer.resolver.ServerAddress, cir: CallbackInfoReturnable<java.util.Optional<net.minecraft.client.multiplayer.resolver.ServerAddress>>) {
                if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().olderThan(LegacyProtocolVersion.r1_3_1tor1_3_2)) {
                    cir.setReturnValue(java.util.Optional.empty<net.minecraft.client.multiplayer.resolver.ServerAddress>())
                }
            }
    }
}
