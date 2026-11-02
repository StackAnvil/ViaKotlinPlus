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
package com.viaversion.viafabricplus.features.global

import de.florianreuth.classic4j.model.classicube.account.CCAccount
import com.viaversion.viafabricplus.ViaFabricPlusImpl
import com.viaversion.viafabricplus.util.LegacySaveMigrator
import com.viaversion.viafabricplus.util.JsonSave
import com.viaversion.viafabricplus.features.global.ClassiCubeAccount
import java.io.IOException
import de.florianreuth.classic4j.api.LoginProcessHandler
import de.florianreuth.classic4j.ClassiCubeHandler

object ClassiCubeAccount {
    var account: CCAccount? = null
    private var authenticated: kotlin.Boolean = false
    
    fun init() {
        val directory: java.nio.file.Path = ViaFabricPlusImpl.impl().path()
        LegacySaveMigrator.migrateClassiCubeAccount(directory)
        
        val path: java.nio.file.Path = directory.resolve("classicube.json")
        JsonSave.load(path, java.util.function.Consumer { jsonObject: com.google.gson.JsonObject -> ClassiCubeAccount.account = CCAccount.fromJson(jsonObject) }, java.util.function.Supplier {
            val account = ClassiCubeAccount.account
            if (account != null) {
                return@Supplier account.asJson()
        } else {
            try {
                java.nio.file.Files.deleteIfExists(path)
            }catch (e: IOException) {
                ViaFabricPlusImpl.impl().logger().error("Failed to delete classicube.json!", e)
            }
            return@Supplier null
        }
        })
    }
    
    fun authenticate(loginCode: kotlin.String?, handler: LoginProcessHandler) {
        ClassiCubeHandler.requestAuthentication(ClassiCubeAccount.account, loginCode, object : LoginProcessHandler {
        
            override fun handleSuccessfulLogin(account: CCAccount) {
                ClassiCubeAccount.authenticated = true
                handler.handleSuccessfulLogin(account)
            }
            
            override fun handleMfa(account: CCAccount) {
                handler.handleMfa(account)
            }
            
            override fun handleException(throwable: Throwable) {
                handler.handleException(throwable)
            }
        })
    }
    
    fun authenticated(): kotlin.Boolean {
        return ClassiCubeAccount.authenticated
    }
    
    fun set(account: CCAccount?) {
        ClassiCubeAccount.account = account
        ClassiCubeAccount.authenticated = false
    }
    
    fun get(): CCAccount {
        return ClassiCubeAccount.account ?: error("Not authenticated")
    }
    
}
