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
package com.viaversion.viafabricplus.protocoltranslator.util

import java.util.logging.LogRecord

class JLoggerToSLF4J (logger: org.slf4j.Logger) : java.util.logging.Logger("logger", null) {

    private val base: org.slf4j.Logger
    
    init {
        this.base = logger
    }
    
    override fun log(record: LogRecord) {
        this.log(record.getLevel(), record.getMessage())
    }
    
    override fun log(level: java.util.logging.Level, msg: kotlin.String) {
        if (level === java.util.logging.Level.FINE) {
            this.base.debug(msg)
        } else if (level === java.util.logging.Level.WARNING) {
            this.base.warn(msg)
        } else if (level === java.util.logging.Level.SEVERE) {
            this.base.error(msg)
        } else if (level === java.util.logging.Level.INFO) {
            this.base.info(msg)
        } else {
            this.base.trace(msg)
        }
    }
    
    override fun log(level: java.util.logging.Level, msg: kotlin.String, param1: kotlin.Any) {
        if (level === java.util.logging.Level.FINE) {
            this.base.debug(msg, param1)
        } else if (level === java.util.logging.Level.WARNING) {
            this.base.warn(msg, param1)
        } else if (level === java.util.logging.Level.SEVERE) {
            this.base.error(msg, param1)
        } else if (level === java.util.logging.Level.INFO) {
            this.base.info(msg, param1)
        } else {
            this.base.trace(msg, param1)
        }
        
    }
    
    override fun log(level: java.util.logging.Level, msg: kotlin.String, params: kotlin.Array<kotlin.Any>) {
        log(level, java.text.MessageFormat.format(msg, *params))
    }
    
    override fun log(level: java.util.logging.Level, msg: kotlin.String, params: Throwable) {
        if (level === java.util.logging.Level.FINE) {
            this.base.debug(msg, params)
        } else if (level === java.util.logging.Level.WARNING) {
            this.base.warn(msg, params)
        } else if (level === java.util.logging.Level.SEVERE) {
            this.base.error(msg, params)
        } else if (level === java.util.logging.Level.INFO) {
            this.base.info(msg, params)
        } else {
            this.base.trace(msg, params)
        }
    }
    
}
