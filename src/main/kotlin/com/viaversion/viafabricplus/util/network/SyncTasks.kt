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
package com.viaversion.viafabricplus.util.network

import java.util.concurrent.ConcurrentHashMap
import java.util.UUID
import com.viaversion.viafabricplus.util.network.DataCustomPayload
import com.viaversion.viafabricplus.util.network.SyncTasks

object SyncTasks {
     /**
 * Contains all tasks that are waiting for a packet to be received, this system can be used to sync ViaVersion tasks with the correct thread
 */
    private val PENDING_EXECUTION_TASKS: kotlin.collections.MutableMap<kotlin.String, java.util.function.Consumer<net.minecraft.network.RegistryFriendlyByteBuf>> = ConcurrentHashMap<kotlin.String, java.util.function.Consumer<net.minecraft.network.RegistryFriendlyByteBuf>>()
    
     /**
 * This identifier is an internal identifier used to identify packets that are sent by ViaFabricPlus
 */
    val PACKET_SYNC_IDENTIFIER: kotlin.String = UUID.randomUUID().toString() + ":" + UUID.randomUUID()
    
    fun init() {
        DataCustomPayload.init()
    }
    
     /**
 * Executes a task synchronized with the main thread from networking threads
 * 
 * @param task The task to execute
 * @return The uuid of the task
 */
    fun executeSyncTask(task: java.util.function.Consumer<net.minecraft.network.RegistryFriendlyByteBuf>): kotlin.String {
        val uuid: kotlin.String = UUID.randomUUID().toString()
        SyncTasks.PENDING_EXECUTION_TASKS.put(uuid, task)
        return uuid
    }
    
     /**
 * Handles a sync task sent from the networking thread
 * 
 * @param buf The packet buffer
 */
    fun handleSyncTask(buf: net.minecraft.network.FriendlyByteBuf) {
        val uuid: kotlin.String = buf.readUtf()
        
        if (SyncTasks.PENDING_EXECUTION_TASKS.containsKey(uuid)) {
            net.minecraft.client.Minecraft.getInstance().execute(java.lang.Runnable {
             // Execute the task on the main thread
            val task: java.util.function.Consumer<net.minecraft.network.RegistryFriendlyByteBuf> = SyncTasks.PENDING_EXECUTION_TASKS.remove(uuid)!!
            task.accept(net.minecraft.network.RegistryFriendlyByteBuf(buf, net.minecraft.client.Minecraft.getInstance().getConnection().registryAccess()))
            
            })
        }
    }
    
}
