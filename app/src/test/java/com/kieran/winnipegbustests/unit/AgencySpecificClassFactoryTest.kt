package com.kieran.winnipegbustests.unit

import com.kieran.winnipegbusbackend.AgencySpecificClassFactory
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class AgencySpecificClassFactoryTest {
    @ParameterizedTest
    @MethodSource("stopIdentifierDataSet")
    fun testConvertsStopIdentifiers(agencyId: Long, identifierString: String, expectedType: String, expectedToString: String) {
        val converted = AgencySpecificClassFactory.createStopIdentifier(agencyId, identifierString)

        Assertions.assertNotNull(converted)
        Assertions.assertEquals(converted!!::javaClass.get().simpleName, expectedType)
        Assertions.assertEquals(converted.toString(), expectedToString)
    }

    @Test
    fun testReturnsNullWithInvalidAgencyId() {
        val converted = AgencySpecificClassFactory.createStopIdentifier(-1, "")

        Assertions.assertNull(converted)
    }

    @Test
    fun testReturnsNullWithInvalidIdentifierString() {
        val converted = AgencySpecificClassFactory.createStopIdentifier(1, "asdfasdf")

        Assertions.assertNull(converted)
    }

    companion object {
        @JvmStatic
        fun stopIdentifierDataSet() = listOf(
                Arguments.of(1L, "12345", "WinnipegTransitStopIdentifier", "12345")
        )
    }
}