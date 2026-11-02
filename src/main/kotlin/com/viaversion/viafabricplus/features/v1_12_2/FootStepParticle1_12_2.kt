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
package com.viaversion.viafabricplus.features.v1_12_2

import org.joml.Quaternionf
import com.viaversion.viafabricplus.features.v1_12_2.FootStepParticle1_12_2
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry.PendingParticleProvider
import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteSet

class FootStepParticle1_12_2 private constructor(clientWorld: net.minecraft.client.multiplayer.ClientLevel, x: kotlin.Double, y: kotlin.Double, z: kotlin.Double, sprite: net.minecraft.client.renderer.texture.TextureAtlasSprite) : net.minecraft.client.particle.SingleQuadParticle(clientWorld, x, y, z, sprite) {

    init {
        this.quadSize = 0.125f
        this.setLifetime(200)
    }
    
    override fun getLayer(): net.minecraft.client.particle.SingleQuadParticle.Layer {
        return net.minecraft.client.particle.SingleQuadParticle.Layer.TRANSLUCENT
    }
    
    override fun extractRotatedQuad(submittable: net.minecraft.client.renderer.state.level.QuadParticleRenderState, camera: net.minecraft.client.Camera, rotation: Quaternionf, tickProgress: kotlin.Float) {
        val strength: kotlin.Float = (this.age.toFloat() + tickProgress) / this.lifetime.toFloat()
        this.alpha = 2.0f - (strength * strength) * 2.0f
        if (this.alpha > 1.0f) {
            this.alpha = 0.2f
        } else {
            this.alpha *= 0.2f
        }
        
        super.extractRotatedQuad(submittable, camera, Quaternionf().rotateX(-net.minecraft.util.Mth.HALF_PI), tickProgress)
    }
    
    class Factory (spriteProvider: net.minecraft.client.particle.SpriteSet) : net.minecraft.client.particle.ParticleProvider<net.minecraft.core.particles.SimpleParticleType> {
    
        private val spriteProvider: net.minecraft.client.particle.SpriteSet
        
        init {
            this.spriteProvider = spriteProvider
        }
        
        override fun createParticle(parameters: net.minecraft.core.particles.SimpleParticleType, world: net.minecraft.client.multiplayer.ClientLevel, x: kotlin.Double, y: kotlin.Double, z: kotlin.Double, velocityX: kotlin.Double, velocityY: kotlin.Double, velocityZ: kotlin.Double, random: net.minecraft.util.RandomSource): net.minecraft.client.particle.Particle {
            if (com.viaversion.viafabricplus.ViaFabricPlus.api().targetVersion().newerThan(com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2)) {
                throw java.lang.UnsupportedOperationException("FootStepParticle is not supported on versions newer than 1.12.2")
            }
            
            val sprite: net.minecraft.client.renderer.texture.TextureAtlasSprite = spriteProvider.get(random)
            return FootStepParticle1_12_2(world, x, y, z, sprite)
        }
    }
    
    companion object {
        val ID: net.minecraft.resources.Identifier = net.minecraft.resources.Identifier.fromNamespaceAndPath("viafabricplus", "footstep")
        var RAW_ID: kotlin.Int = 0
        
        fun init() {
            val footStepType: net.minecraft.core.particles.SimpleParticleType = FabricParticleTypes.simple(true)
            
            net.minecraft.core.Registry.register<net.minecraft.core.particles.ParticleType<*>, net.minecraft.core.particles.SimpleParticleType>(net.minecraft.core.registries.BuiltInRegistries.PARTICLE_TYPE, FootStepParticle1_12_2.Companion.ID, footStepType)
            ParticleProviderRegistry.getInstance().register<net.minecraft.core.particles.SimpleParticleType>(footStepType, PendingParticleProvider {spriteProvider: FabricSpriteSet -> Factory(spriteProvider)})
            
            FootStepParticle1_12_2.Companion.RAW_ID = net.minecraft.core.registries.BuiltInRegistries.PARTICLE_TYPE.getId(footStepType)
        }
        
    }}
