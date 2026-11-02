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
package com.viaversion.viafabricplus.injection.mixin.features.v1_17

import com.viaversion.viafabricplus.util.uninitialized

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.viaversion.viafabricplus.injection.access.v1_17.IEntity
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation

@Mixin(net.minecraft.client.multiplayer.ClientLevel::class) 
abstract class MixinClientLevel protected constructor(properties: net.minecraft.world.level.storage.WritableLevelData, registryRef: net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level>, registryManager: net.minecraft.core.RegistryAccess, dimensionEntry: net.minecraft.core.Holder<net.minecraft.world.level.dimension.DimensionType>, isClient: kotlin.Boolean, debugWorld: kotlin.Boolean, seed: kotlin.Long, maxChainedNeighborUpdates: kotlin.Int) : net.minecraft.world.level.Level(properties, registryRef, registryManager, dimensionEntry, isClient, debugWorld, seed, maxChainedNeighborUpdates) {

    @org.spongepowered.asm.mixin.Shadow @Final 
    @JvmField val tickingEntities: net.minecraft.world.level.entity.EntityTickList = uninitialized()
    @org.spongepowered.asm.mixin.Shadow protected abstract fun tickPassenger(vehicle: net.minecraft.world.entity.Entity, entity: net.minecraft.world.entity.Entity)
    
    @Inject(method = ["tickNonPassenger"], at = [At("HEAD")], cancellable = true) private fun alwaysTickEntities(entity: net.minecraft.world.entity.Entity, ci: CallbackInfo) {
        val mixinEntity: IEntity = entity as IEntity
        if (!mixinEntity.`viaFabricPlus$isInLoadedChunkAndShouldTick`() && !entity.isSpectator()) {
            entity.setOldPosAndRot()
            this.`viaFabricPlus$checkChunk`(entity)
            if (mixinEntity.`viaFabricPlus$isInLoadedChunkAndShouldTick`()) {
                for (entity2 in entity.getPassengers()) {
                    this.tickPassenger(entity, entity2)
                }
            }
            ci.cancel()
        }
    }
    
    @Inject(method = ["tickPassenger"], at = [At("HEAD")], cancellable = true) private fun alwaysTickEntities(vehicle: net.minecraft.world.entity.Entity, entity: net.minecraft.world.entity.Entity, ci: CallbackInfo) {
        val mixinPassenger: IEntity = entity as IEntity
        if (!mixinPassenger.`viaFabricPlus$isInLoadedChunkAndShouldTick`()) {
            if (entity.isRemoved() || entity.getVehicle() !== vehicle) {
                entity.stopRiding()
            } else if (entity is net.minecraft.world.entity.player.Player || this.tickingEntities.contains(entity)) {
                entity.setOldPosAndRot()
                this.`viaFabricPlus$checkChunk`(entity)
                if (mixinPassenger.`viaFabricPlus$isInLoadedChunkAndShouldTick`()) {
                    for (entity2 in entity.getPassengers()) {
                        this.tickPassenger(entity, entity2)
                    }
                }
            }
            ci.cancel()
        }
    }
    
    @WrapOperation(method = ["tickNonPassenger"], at = [At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getPassengers()Ljava/util/List;")]) private fun alwaysTickEntities(instance: net.minecraft.world.entity.Entity, original: com.llamalad7.mixinextras.injector.wrapoperation.Operation<kotlin.collections.MutableList<net.minecraft.world.entity.Entity>>): kotlin.collections.MutableList<net.minecraft.world.entity.Entity> {
        this.`viaFabricPlus$checkChunk`(instance)
        val mixinEntity: IEntity = instance as IEntity
        if (mixinEntity.`viaFabricPlus$isInLoadedChunkAndShouldTick`()) {
            return original.call(instance)
        } else {
            return kotlin.collections.mutableListOf<net.minecraft.world.entity.Entity>()
        }
    }
    
    @org.spongepowered.asm.mixin.Unique private fun `viaFabricPlus$checkChunk`(entity: net.minecraft.world.entity.Entity) {
        val mixinEntity: IEntity = entity as IEntity
        val chunkX: kotlin.Int = net.minecraft.util.Mth.floor(entity.getX() / 16.0)
        val chunkZ: kotlin.Int = net.minecraft.util.Mth.floor(entity.getZ() / 16.0)
        if (!mixinEntity.`viaFabricPlus$isInLoadedChunkAndShouldTick`() || entity.chunkPosition().x() != chunkX || entity.chunkPosition().z() != chunkZ) {
            if (!(this.getChunk(chunkX, chunkZ).isEmpty())) {
                mixinEntity.`viaFabricPlus$setInLoadedChunkAndShouldTick`(true)
            }
        }
    }
    
}
