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
package com.viaversion.viafabricplus.injection.mixin.compat.classic4j

import org.spongepowered.asm.mixin.Mixin
import de.florianreuth.classic4j.model.classicube.CCAuthenticationResponse
import org.spongepowered.asm.mixin.injection.At
import org.objectweb.asm.Opcodes
import de.florianreuth.classic4j.model.classicube.CCError

@Mixin(value = [CCAuthenticationResponse::class], remap = false) 
abstract class MixinCCAuthenticationResponse {

     // Classic4J doesn't support translations, so we have to map them manually
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["getErrorDisplay"], at = [At(value = "FIELD", target = "Lde/florianreuth/classic4j/model/classicube/CCError;description:Ljava/lang/String;", opcode = Opcodes.GETFIELD)]) private fun mapTranslations(instance: CCError): kotlin.String {
        return when (instance){CCError.TOKEN -> net.minecraft.network.chat.Component.translatable("classic4j_library.viafabricplus.error.token").getString()
            CCError.USERNAME -> net.minecraft.network.chat.Component.translatable("classic4j_library.viafabricplus.error.username").getString()
            CCError.PASSWORD -> net.minecraft.network.chat.Component.translatable("classic4j_library.viafabricplus.error.password").getString()
            CCError.VERIFICATION -> net.minecraft.network.chat.Component.translatable("classic4j_library.viafabricplus.error.verification").getString()
            CCError.LOGIN_CODE -> net.minecraft.network.chat.Component.translatable("classic4j_library.viafabricplus.error.logincode").getString()}
    }
    
}
