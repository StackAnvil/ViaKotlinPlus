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
package com.viaversion.viafabricplus.screen.impl.protocol

import com.viaversion.viafabricplus.screen.impl.protocol.AbstractProtocolSelectionScreen
import com.viaversion.viafabricplus.screen.base.list.VFPListEntry
import com.viaversion.viafabricplus.screen.impl.protocol.ProtocolVersionMetadata
import com.viaversion.viafabricplus.screen.impl.protocol.ProtocolSlot
import com.viaversion.viafabricplus.ViaFabricPlusImpl
import com.viaversion.viafabricplus.screen.base.VFPScreen

class ProtocolSlot (protocolVersion: com.viaversion.viaversion.api.protocol.version.ProtocolVersion, screen: AbstractProtocolSelectionScreen) : VFPListEntry() {

    private val protocolVersion: com.viaversion.viaversion.api.protocol.version.ProtocolVersion
    private val screen: AbstractProtocolSelectionScreen
    private val metadata: ProtocolVersionMetadata
    private val icon: net.minecraft.resources.Identifier
    
    init {
        this.protocolVersion = protocolVersion
        this.screen = screen
        this.metadata = ProtocolVersionMetadata.of(protocolVersion)
        this.icon = ProtocolSlot.Companion.resolveIcon(protocolVersion)
    }
    
    override fun getNarration(): net.minecraft.network.chat.Component {
        return net.minecraft.network.chat.Component.nullToEmpty(this.protocolVersion.getName())
    }
    
    override fun mouseClicked(click: net.minecraft.client.input.MouseButtonEvent, doubled: kotlin.Boolean): kotlin.Boolean {
        if (click.x() < this.getContentRight() - ProtocolSlot.Companion.STAR_AREA) {
            return super.mouseClicked(click, doubled)
        }
        
        this.screen.toggleFavorite(this.protocolVersion)
        net.minecraft.client.gui.components.AbstractWidget.playButtonClickSound(net.minecraft.client.Minecraft.getInstance().getSoundManager())
                // Not taking focus as unfavoriting removes this entry from the favorites list it may be part of
        return false
    }
    
    override fun mappedMouseClicked() {
        if (this.screen.selectable()) {
            this.screen.select(this.protocolVersion)
        }
    }
    
    override fun mappedRender(context: net.minecraft.client.gui.GuiGraphicsExtractor, entryWidth: kotlin.Int, entryHeight: kotlin.Int) {
        val selectable: kotlin.Boolean = this.screen.selectable()
        val selected: kotlin.Boolean = this.screen.selected(this.protocolVersion)
        if (selected && selectable) {
            context.fill(0, 0, entryWidth, entryHeight, ProtocolSlot.Companion.SELECTED_BACKGROUND_COLOR)
        }
        
        context.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, this.icon, VFPListEntry.SLOT_MARGIN, (entryHeight - ProtocolSlot.Companion.ICON_SIZE) / 2, 0f, 0f, ProtocolSlot.Companion.ICON_SIZE, ProtocolSlot.Companion.ICON_SIZE, ProtocolSlot.Companion.ICON_SIZE, ProtocolSlot.Companion.ICON_SIZE)
        
        val font: net.minecraft.client.gui.Font = net.minecraft.client.Minecraft.getInstance().font
        val nameColor: kotlin.Int = if (!selectable) net.minecraft.util.CommonColors.GRAY else if (selected) ProtocolSlot.Companion.SELECTED_COLOR else -1
        val centeredY: kotlin.Int = entryHeight / 2 - font.lineHeight / 2
        
        val starColor: kotlin.Int = if (ViaFabricPlusImpl.impl().settings().isFavorite(this.protocolVersion)) ProtocolSlot.Companion.FAVORITE_COLOR else ProtocolSlot.Companion.NO_FAVORITE_COLOR
        context.text(font, ProtocolSlot.Companion.STAR, entryWidth - ProtocolSlot.Companion.STAR_AREA + (ProtocolSlot.Companion.STAR_AREA - font.width(ProtocolSlot.Companion.STAR)) / 2, centeredY, starColor)
        
        if (this.metadata == null) {
            context.text(font, this.protocolVersion.getName(), ProtocolSlot.Companion.TEXT_OFFSET, centeredY, nameColor)
            return 
        }
        
        val subtitleColor: kotlin.Int = if (!selectable) net.minecraft.util.CommonColors.GRAY else ProtocolSlot.Companion.SUBTITLE_COLOR
        val releaseDate: net.minecraft.network.chat.Component = this.metadata.formattedReleaseDate()
        val releaseDateX: kotlin.Int = entryWidth - ProtocolSlot.Companion.STAR_AREA - font.width(releaseDate)
        context.text(font, releaseDate, releaseDateX, centeredY, subtitleColor)
        
        if (this.metadata.title == null) {
            // Versions without an update name only have a single line of text to center
            context.text(font, this.protocolVersion.getName(), ProtocolSlot.Companion.TEXT_OFFSET, centeredY, nameColor)
            return 
        }
        
        context.text(font, this.protocolVersion.getName(), ProtocolSlot.Companion.TEXT_OFFSET, entryHeight / 2 - font.lineHeight - 1, nameColor)
        
                // Long update names are cut off instead of overlapping the release date
        context.enableScissor(ProtocolSlot.Companion.TEXT_OFFSET, 0, releaseDateX - VFPListEntry.SLOT_MARGIN, entryHeight)
        context.text(font, this.metadata.title, ProtocolSlot.Companion.TEXT_OFFSET, entryHeight / 2 + 1, subtitleColor)
        context.disableScissor()
    }
    
    companion object {
        private val AUTO_DETECT_ICON: net.minecraft.resources.Identifier = net.minecraft.resources.Identifier.withDefaultNamespace("textures/item/spyglass.png")
        
        private const val ICON_SIZE: kotlin.Int = 16
        private val TEXT_OFFSET: kotlin.Int = VFPListEntry.SLOT_MARGIN + ProtocolSlot.Companion.ICON_SIZE + VFPListEntry.SLOT_MARGIN
        private val STAR_AREA: kotlin.Int = VFPListEntry.SLOT_MARGIN + ProtocolSlot.Companion.ICON_SIZE + VFPListEntry.SLOT_MARGIN // Mirrors the icon on the left side
        
        private const val STAR: kotlin.String = "★"
        private const val FAVORITE_COLOR: kotlin.Int = -0x5600
        private const val NO_FAVORITE_COLOR: kotlin.Int = -0xaaaaab
        
        private val SELECTED_COLOR: kotlin.Int = VFPScreen.ACCENT_COLOR
        private const val SELECTED_BACKGROUND_COLOR: kotlin.Int = 0x4058A6FF // ACCENT_COLOR at 25% opacity
        private const val SUBTITLE_COLOR: kotlin.Int = -0x555556
        
        private fun resolveIcon(protocolVersion: com.viaversion.viaversion.api.protocol.version.ProtocolVersion): net.minecraft.resources.Identifier {
        // Auto Detect is no Minecraft version and therefore has no metadata to take an icon from
            if (protocolVersion === com.viaversion.viafabricplus.api.protocoltranslator.ProtocolTranslation.AUTO_DETECT_VERSION) {
                return ProtocolSlot.Companion.AUTO_DETECT_ICON
            }
            
            return ProtocolVersionMetadata.icon(protocolVersion)
        }
        
    }}
