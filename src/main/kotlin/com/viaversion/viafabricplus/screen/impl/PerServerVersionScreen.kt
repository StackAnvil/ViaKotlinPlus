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
package com.viaversion.viafabricplus.screen.impl

import com.viaversion.viafabricplus.screen.impl.protocol.AbstractProtocolSelectionScreen

class PerServerVersionScreen (prevScreen: net.minecraft.client.gui.screens.Screen, selectionConsumer: java.util.function.Consumer<com.viaversion.viaversion.api.protocol.version.ProtocolVersion?>, selectionSupplier: java.util.function.Supplier<com.viaversion.viaversion.api.protocol.version.ProtocolVersion?>) : AbstractProtocolSelectionScreen(net.minecraft.network.chat.Component.translatable("screen.viafabricplus.force_version"), true) {

    private val selectionConsumer: java.util.function.Consumer<com.viaversion.viaversion.api.protocol.version.ProtocolVersion?>
    private val selectionSupplier: java.util.function.Supplier<com.viaversion.viaversion.api.protocol.version.ProtocolVersion?>
    
    init {
        this.prevScreen = prevScreen
        this.selectionConsumer = selectionConsumer
        this.selectionSupplier = selectionSupplier
    }
    
    override fun init() {
        super.init()
        
        this.addFooter(net.minecraft.client.gui.components.Button.builder(net.minecraft.network.chat.Component.translatable("base.viafabricplus.reset"), net.minecraft.client.gui.components.Button.OnPress {`_`: net.minecraft.client.gui.components.Button -> this.selectionConsumer.accept(null)}).build())
    }
    
    override fun select(version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion) {
        this.selectionConsumer.accept(version)
    }
    
    override fun selected(version: com.viaversion.viaversion.api.protocol.version.ProtocolVersion): kotlin.Boolean {
        return version == this.selectionSupplier.get()
    }
    
    override fun selectable(): kotlin.Boolean {
        return true
    }
    
}
