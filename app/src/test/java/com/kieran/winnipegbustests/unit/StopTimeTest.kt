package com.kieran.winnipegbustests.unit

import com.kieran.winnipegbusbackend.StopTime
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class StopTimeTest {
    @ParameterizedTest
    @MethodSource("toString24hrDataSet")
    fun stopTimeShouldCreateCorrect24HourString(millis: Long, expectedString: String) {
        val time = StopTime(millis)

        Assertions.assertEquals(expectedString, time.to24hrTimeString())
    }

        @ParameterizedTest
        @MethodSource("toString12hrDataSet")
    fun stopTimeShouldCreateCorrect12HourString(millis: Long, expectedString: String) {
        val time = StopTime(millis)

        Assertions.assertEquals(expectedString, time.to12hrTimeString())
    }

    companion object {
        @JvmStatic
        fun toString24hrDataSet() = listOf(
                Arguments.of(1468606042000L, "13:07"),
                Arguments.of(1468600000000L, "11:26"),
                Arguments.of(1468500000000L, "07:40"),
                Arguments.of(1468906042000L, "00:27")
        )

        @JvmStatic
        fun toString12hrDataSet() = listOf(
                Arguments.of(1468606042000L, "1:07p"),
                Arguments.of(1468600000000L, "11:26a"),
                Arguments.of(1468500000000L, "7:40a"),
                Arguments.of(1468906042000L, "12:27a")
        )
    }
}