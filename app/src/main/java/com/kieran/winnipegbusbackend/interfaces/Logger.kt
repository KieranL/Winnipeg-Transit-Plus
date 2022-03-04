package com.kieran.winnipegbusbackend.interfaces

import com.kieran.winnipegbusbackend.NowhereLogger
import java.lang.Exception

interface Logger {
    fun info (exception: Exception, message: String)

    fun error (exception: Exception, message: String)

    companion object {
        fun getLogger(): Logger {
            return NowhereLogger
        }
    }
}