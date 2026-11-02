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
package com.viaversion.viafabricplus.features.v1_20

import com.viaversion.viafabricplus.ViaFabricPlus
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EntityTypes
import net.minecraft.world.entity.Pose
import net.minecraft.world.entity.animal.Animal
import net.minecraft.world.entity.animal.allay.Allay
import net.minecraft.world.entity.animal.camel.Camel
import net.minecraft.world.entity.animal.chicken.Chicken
import net.minecraft.world.entity.animal.equine.AbstractChestedHorse
import net.minecraft.world.entity.animal.equine.AbstractHorse
import net.minecraft.world.entity.animal.equine.Llama
import net.minecraft.world.entity.animal.equine.SkeletonHorse
import net.minecraft.world.entity.animal.sniffer.Sniffer
import net.minecraft.world.entity.boss.enderdragon.EnderDragon
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.monster.Endermite
import net.minecraft.world.entity.monster.PatrollingMonster
import net.minecraft.world.entity.monster.Phantom
import net.minecraft.world.entity.monster.Ravager
import net.minecraft.world.entity.monster.Shulker
import net.minecraft.world.entity.monster.Silverfish
import net.minecraft.world.entity.monster.Strider
import net.minecraft.world.entity.monster.Vex
import net.minecraft.world.entity.monster.Zoglin
import net.minecraft.world.entity.monster.hoglin.Hoglin
import net.minecraft.world.entity.monster.piglin.AbstractPiglin
import net.minecraft.world.entity.monster.piglin.Piglin
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton
import net.minecraft.world.entity.monster.spider.Spider
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.vehicle.boat.AbstractBoat
import net.minecraft.world.entity.vehicle.boat.AbstractChestBoat
import net.minecraft.world.entity.vehicle.boat.Boat
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart
import net.minecraft.world.phys.Vec3

object EntityRidingOffsetsPre1_20_2 {

    @JvmStatic
    fun getMountedHeightOffset(entity: Entity, passenger: Entity?): Vec3 {
        var yOffset = entity.bbHeight * 0.75f

        when (entity) {
            is AbstractBoat -> {
                if (!entity.hasPassenger(passenger)) return Vec3.ZERO

                if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
                    yOffset = -0.3f
                    val xOffset = Mth.cos((entity.yRot * Mth.PI / 180f).toDouble())
                    val zOffset = Mth.sin((entity.yRot * Mth.PI / 180f).toDouble())
                    return Vec3((0.4f * xOffset).toDouble(), yOffset.toDouble(), (0.4f * zOffset).toDouble())
                }

                yOffset = if (entity.isRemoved) {
                    0.01f
                } else {
                    if (entity.type == EntityTypes.BAMBOO_RAFT || entity.type == EntityTypes.BAMBOO_CHEST_RAFT) 0.25f else -0.1f
                }

                var xOffset = if (entity is AbstractChestBoat) 0.15f else 0f
                if (entity.passengers.size > 1) {
                    val idx = entity.passengers.indexOf(passenger)
                    xOffset = if (idx == 0) 0.2f else -0.6f
                    if (passenger is Animal) xOffset += 0.2f
                }
                return Vec3(xOffset.toDouble(), yOffset.toDouble(), 0.0).yRot(-(Math.PI / 2).toFloat())
            }

            is Camel -> {
                if (!entity.hasPassenger(passenger)) return Vec3.ZERO

                val firstPassenger = entity.passengers.indexOf(passenger) == 0
                yOffset = entity.getDimensions(if (entity.isCamelSitting) Pose.SITTING else Pose.STANDING).height -
                    (if (entity.isBaby) 0.35f else 0.6f)
                yOffset = if (entity.isRemoved) {
                    0.01f
                } else {
                    entity.getBodyAnchorAnimationYOffset(
                        firstPassenger, 0f,
                        EntityDimensions.fixed(0f, (0.375f * entity.ageScale) + yOffset),
                        entity.ageScale
                    ).toFloat()
                }

                var zOffset = 0.5f
                if (entity.passengers.size > 1) {
                    if (!firstPassenger) zOffset = -0.7f
                    if (passenger is Animal) zOffset += 0.2f
                }
                return Vec3(0.0, yOffset.toDouble(), zOffset.toDouble())
            }

            is Chicken -> return Vec3(0.0, entity.getY(0.5) - entity.y, -0.1)
            is EnderDragon -> yOffset = entity.body.bbHeight
            is Hoglin -> yOffset = entity.bbHeight - (if (entity.isBaby) 0.2f else 0.15f)
            is Llama -> return Vec3(0.0, (entity.bbHeight * 0.6f).toDouble(), -0.3)
            is Phantom -> yOffset = entity.eyeHeight
            is Piglin -> yOffset = entity.bbHeight * 0.92f
            is Ravager -> yOffset = 2.1f
            is SkeletonHorse -> yOffset -= 0.1875f
            is Sniffer -> yOffset = 1.8f
            is Spider -> yOffset = entity.bbHeight * 0.5f
            is Strider -> {
                val speed = Math.min(0.25f, entity.walkAnimation.speed())
                val animationProgress = entity.walkAnimation.position()
                yOffset = entity.bbHeight - 0.19f + (0.12f * Mth.cos((animationProgress * 1.5f).toDouble()) * 2f * speed)
            }
            is Zoglin -> yOffset = entity.bbHeight - (if (entity.isBaby) 0.2f else 0.15f)
            is AbstractChestedHorse -> yOffset -= 0.25f
            is AbstractMinecart -> yOffset = 0f
            else -> {}
        }

        if (entity is AbstractHorse && entity.standAnimO > 0.0f) {
            return Vec3(0.0, (yOffset + 0.15f * entity.standAnimO).toDouble(), (-0.7f * entity.standAnimO).toDouble())
        }

        return Vec3(0.0, yOffset.toDouble(), 0.0)
    }

    @JvmStatic
    fun getHeightOffset(entity: Entity): Double {
        return when {
            entity is Allay || entity is Vex -> {
                if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_19_1)) 0.0 else 0.4
            }
            entity is ArmorStand -> if (entity.isMarker) 0.0 else 0.1
            entity is Endermite -> 0.1
            entity is Shulker -> {
                val vehicle = entity.vehicle!!
                val vehicleType: EntityType<*> = vehicle.type
                if (vehicle !is Boat && vehicleType != EntityTypes.MINECART) 0.0
                else 0.1875 - getMountedHeightOffset(vehicle, null).y
            }
            entity is Silverfish -> 0.1
            entity is ZombifiedPiglin -> if (entity.isBaby) -0.05 else -0.45
            entity is Zombie -> if (entity.isBaby) 0.0 else -0.45
            entity is Animal -> 0.14
            entity is PatrollingMonster -> -0.45
            entity is Player -> -0.35
            entity is AbstractPiglin -> if (entity.isBaby) -0.05 else -0.45
            entity is AbstractSkeleton -> -0.6
            else -> 0.0
        }
    }
}
