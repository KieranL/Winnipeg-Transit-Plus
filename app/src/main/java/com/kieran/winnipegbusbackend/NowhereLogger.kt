package com.kieran.winnipegbusbackend

import com.kieran.winnipegbusbackend.interfaces.Logger
import java.lang.Exception

object NowhereLogger: Logger {
    override fun info(exception: Exception, message: String) {

    }

    override fun error(exception: Exception, message: String) {

    }
}