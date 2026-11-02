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

import net.raphimc.vialegacy.protocol.classic.c0_28_30toa1_0_15.provider.ClassicMpPassProvider
import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viafabricplus.protocoltranslator.impl.provider.vialegacy.ViaFabricPlusClassicMPPassProvider
import com.viaversion.viafabricplus.ViaFabricPlusImpl
import de.florianreuth.classic4j.BetaCraftHandler
import de.florianreuth.classic4j.api.JoinServerInterface
import com.viaversion.viaversion.api.Via
import net.raphimc.vialegacy.protocol.release.r1_2_4_5tor1_3_1_2.provider.OldAuthProvider

class ViaFabricPlusClassicMPPassProvider : ClassicMpPassProvider() {

    override fun getMpPass(connection: UserConnection): kotlin.String {
        if (ViaFabricPlusClassicMPPassProvider.Companion.classicubeMPPass != null) {
            val mpPass: kotlin.String = ViaFabricPlusClassicMPPassProvider.Companion.classicubeMPPass!!
            ViaFabricPlusClassicMPPassProvider.Companion.classicubeMPPass = null
            return mpPass
        }
        
        if (ViaFabricPlusImpl.impl().options().betaCraftAuthentication().isActive()) {
            // Doesn't use the MPPass system anymore, but still kept here for simplicity
            BetaCraftHandler.authenticate(JoinServerInterface {serverId: kotlin.String -> try {
                Via.getManager().getProviders().get<OldAuthProvider>(OldAuthProvider::class.java)!!.sendAuthRequest(connection, serverId)
            }catch (e: Throwable) {
                ViaFabricPlusImpl.impl().logger().error("Error occurred while verifying session", e)
            }
            }, java.util.function.Consumer {throwable: Throwable -> ViaFabricPlusImpl.impl().logger().error("Error occurred while requesting the MP-Pass to verify session", throwable)})
        }
        
        return super.getMpPass(connection)
    }
    
    companion object {
        var classicubeMPPass: kotlin.String? = null
    }}
