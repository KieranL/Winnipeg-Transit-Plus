package com.kieran.winnipegbusbackend

import android.util.Log
import com.kieran.winnipegbusbackend.interfaces.Logger
import org.jetbrains.anko.getStackTraceString
import java.lang.Exception

object NowhereLogger: Logger {
    override fun info(exception: Exception, message: String) {
        Log.i("INFO", exception.getStackTraceString())
    }

    override fun error(exception: Exception, message: String) {
        Log.d("ERROR", exception.getStackTraceString())
    }
}