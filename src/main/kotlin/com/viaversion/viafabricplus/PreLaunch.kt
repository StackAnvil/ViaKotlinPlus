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
 */
package com.viaversion.viafabricplus

import com.viaversion.viafabricplus.api.entrypoint.ViaFabricPlusEntrypoint
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint

class PreLaunch : PreLaunchEntrypoint {
    override fun onPreLaunch() {
        FabricLoader.getInstance()
            .getEntrypointContainers("viafabricplus", ViaFabricPlusEntrypoint::class.java)
            .forEach { it.entrypoint.onPreLoading() }

    }
}
