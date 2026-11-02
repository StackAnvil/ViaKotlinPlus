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
package com.viaversion.viafabricplus.features

import com.viaversion.viafabricplus.util.network.SyncTasks
import com.viaversion.viafabricplus.features.c0_30cpe.CPEAdditions
import com.viaversion.viafabricplus.features.global.CollisionShapes
import com.viaversion.viaaprilfools.api.AprilFoolsProtocolVersion
import net.raphimc.vialegacy.api.LegacyProtocolVersion
import com.viaversion.viafabricplus.ViaFabricPlusImpl
import com.viaversion.viafabricplus.features.global.FontCacheReload
import com.viaversion.viafabricplus.features.v1_11_1.Recipes1_11_2
import com.viaversion.viafabricplus.features.global.ResourcePackHeaderDiff
import com.viaversion.viafabricplus.features.v1_12_2.RenderableGlyphDiff
import com.viaversion.viafabricplus.features.global.ClassiCubeAccount
import com.viaversion.viafabricplus.features.v1_12_2.FootStepParticle1_12_2
import com.viaversion.viafabricplus.features.global.EntityDimensionDiff
import com.viaversion.viafabricplus.features.v1_20_5.EnchantmentAttributesEmulation1_20_6
import com.viaversion.viafabricplus.features.v1_8.ArmorHudEmulation1_8

object FeaturesLoading {
    fun onPreLoading() {
        SyncTasks.init()
        CPEAdditions.init()
        
        com.viaversion.viafabricplus.ViaFabricPlus.api().protocolTranslation().addChangeProtocolVersionListener(java.util.function.BiConsumer {oldVersion: com.viaversion.viaversion.api.protocol.version.ProtocolVersion, newVersion: com.viaversion.viaversion.api.protocol.version.ProtocolVersion -> net.minecraft.client.Minecraft.getInstance().execute(java.lang.Runnable {
        CollisionShapes.reloadBlockShapes()
        
        if (oldVersion == AprilFoolsProtocolVersion.s3d_shareware || newVersion == AprilFoolsProtocolVersion.s3d_shareware) {
            net.minecraft.client.Minecraft.getInstance().getSoundManager().reload()
        }
        
        if (newVersion.olderThanOrEqualTo(LegacyProtocolVersion.c0_28toc0_30)) {
            ViaFabricPlusImpl.impl().screens().gridItemSelectionScreen().itemGrid = null
        }
        
        FontCacheReload.reload()
        
        if (newVersion.olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_11_1)) {
            Recipes1_11_2.reset()
        }
        
        net.minecraft.world.attribute.EnvironmentAttributes.RESPAWN_ANCHOR_WORKS.isSyncable = newVersion.olderThanOrEqualTo(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_9)
        
        })})
    }
    
    fun onPostRegistryLoading() {
        ResourcePackHeaderDiff.init()
        RenderableGlyphDiff.init()
        ClassiCubeAccount.init()
        FootStepParticle1_12_2.init()
    }
    
    fun onPostGameLoading() {
        EntityDimensionDiff.init()
        EnchantmentAttributesEmulation1_20_6.init()
        Recipes1_11_2.init()
        ArmorHudEmulation1_8.init()
        CPEAdditions.postInit()
    }
    
}
