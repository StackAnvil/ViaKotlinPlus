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
import com.viaversion.viafabricplus.injection.access.core.IEditBox
import org.spongepowered.asm.mixin.injection.At

 /**
 * ClassicCube allows using forbidden characters which the game doesn't allow in passwords, so we have to bypass this check
 * to allow the user to enter their password.
 */
@Mixin(net.minecraft.client.gui.components.EditBox::class) 
abstract class MixinEditBox : IEditBox {

    @field:org.spongepowered.asm.mixin.Unique 
    @JvmField var `viaFabricPlus$forbiddenCharactersUnlocked`: kotlin.Boolean = false
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["charTyped"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/input/CharacterEvent;isAllowedChatCharacter()Z")]) private fun allowForbiddenCharacters(instance: net.minecraft.client.input.CharacterEvent): kotlin.Boolean {
        return this.`viaFabricPlus$forbiddenCharactersUnlocked` || instance.isAllowedChatCharacter()
    }
    
    @org.spongepowered.asm.mixin.injection.Redirect(method = ["insertText"], at = [At(value = "INVOKE", target = "Lnet/minecraft/util/StringUtil;filterText(Ljava/lang/String;)Ljava/lang/String;")]) private fun allowForbiddenCharacters(input: kotlin.String): kotlin.String {
        if (this.`viaFabricPlus$forbiddenCharactersUnlocked`) {
            return input
        } else {
            return net.minecraft.util.StringUtil.filterText(input)
        }
    }
    
    override fun `viaFabricPlus$unlockForbiddenCharacters`() {
        this.`viaFabricPlus$forbiddenCharactersUnlocked` = true
    }
    
}
