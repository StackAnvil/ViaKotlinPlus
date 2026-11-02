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
package com.viaversion.viafabricplus.injection.mixin.features.v1_13_2

import org.spongepowered.asm.mixin.Mixin
import com.viaversion.viaversion.protocols.v1_13_2to1_14.rewriter.ItemPacketRewriter1_14
import com.viaversion.viaversion.protocols.v1_13_2to1_14.Protocol1_13_2To1_14
import com.viaversion.viaversion.protocols.v1_12_2to1_13.packet.ClientboundPackets1_13
import com.viaversion.viaversion.protocols.v1_13_2to1_14.packet.ServerboundPackets1_14
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.viaversion.viaversion.protocols.v1_12_2to1_13.packet.ServerboundPackets1_13
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper
import com.viaversion.viafabricplus.util.network.SyncTasks
import com.viaversion.viaversion.protocols.v1_13_2to1_14.packet.ClientboundPackets1_14

@Mixin(value = [ItemPacketRewriter1_14::class], remap = false) 
abstract class MixinItemPacketRewriter1_14 (protocol: Protocol1_13_2To1_14, itemType: com.viaversion.viaversion.api.type.Type<com.viaversion.viaversion.api.minecraft.item.Item>, itemArrayType: com.viaversion.viaversion.api.type.Type<kotlin.Array<com.viaversion.viaversion.api.minecraft.item.Item>>) : com.viaversion.viaversion.rewriter.ItemRewriter<ClientboundPackets1_13, ServerboundPackets1_14, Protocol1_13_2To1_14>(protocol, itemType, itemArrayType) {

    @Inject(method = ["registerPackets"], at = [At("RETURN")]) private fun dontResyncInventory(ci: CallbackInfo) {
        this.protocol.registerServerbound(ServerboundPackets1_14.SELECT_TRADE, ServerboundPackets1_13.SELECT_TRADE, null, true)
    }
    
    @Inject(method = ["lambda\$registerPackets$0"], at = [At(value = "INVOKE", target = "Lcom/viaversion/viaversion/util/ProtocolLogger;warning(Ljava/lang/String;)V", remap = false)], cancellable = true) private fun supportLargeContainers(wrapper: PacketWrapper, ci: CallbackInfo, @com.llamalad7.mixinextras.sugar.Local(ordinal = 0) windowId: java.lang.Short, @com.llamalad7.mixinextras.sugar.Local type: kotlin.String, @com.llamalad7.mixinextras.sugar.Local title: com.viaversion.viaversion.libs.gson.JsonElement, @com.llamalad7.mixinextras.sugar.Local(ordinal = 1) slots: java.lang.Short) {
        if ((type == "minecraft:container" || type == "minecraft:chest") && (slots > 54 || slots <= 0)) {
            ci.cancel()
            
            val uuid: kotlin.String = SyncTasks.executeSyncTask(java.util.function.Consumer {
            data: net.minecraft.network.RegistryFriendlyByteBuf -> val mc: net.minecraft.client.Minecraft = net.minecraft.client.Minecraft.getInstance()
            
            try {
                val syncId: kotlin.Int = data.readUnsignedByte().toInt()
                val size: kotlin.Int = data.readUnsignedByte().toInt()
                val mcTitle: net.minecraft.network.chat.Component = net.minecraft.network.chat.ComponentSerialization.TRUSTED_STREAM_CODEC.decode(data)
                
                val screenHandler: net.minecraft.world.inventory.ChestMenu = net.minecraft.world.inventory.ChestMenu(null, syncId, mc.player.getInventory(), net.minecraft.world.SimpleContainer(size), net.minecraft.util.Mth.ceil(size / 9f))
                mc.player.containerMenu = screenHandler
                mc.gui.setScreen(net.minecraft.client.gui.screens.inventory.ContainerScreen(screenHandler, mc.player.getInventory(), mcTitle))
            }catch (t: Throwable) {
                throw java.lang.RuntimeException("Failed to handle OpenWindow packet data", t)
            }
            
            })
            
            wrapper.clearPacket()
            wrapper.setPacketType(ClientboundPackets1_14.CUSTOM_PAYLOAD)
            wrapper.write<kotlin.String>(com.viaversion.viaversion.api.type.Types.STRING, SyncTasks.PACKET_SYNC_IDENTIFIER) // sync task header
            wrapper.write<kotlin.String>(com.viaversion.viaversion.api.type.Types.STRING, uuid) // sync task id
            wrapper.write<kotlin.Short>(com.viaversion.viaversion.api.type.Types.UNSIGNED_BYTE, windowId.toShort())
            wrapper.write<kotlin.Short>(com.viaversion.viaversion.api.type.Types.UNSIGNED_BYTE, slots.toShort())
            wrapper.write<com.viaversion.nbt.tag.Tag>(com.viaversion.viaversion.api.type.Types.TAG, com.viaversion.viafabricplus.features.v1_13_2.TextComponentTranslator.via1_14toViaLatest(title))
        }
    }
    
}
