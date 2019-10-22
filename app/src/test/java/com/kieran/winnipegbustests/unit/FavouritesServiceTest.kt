package com.kieran.winnipegbustests.unit

import com.kieran.winnipegbus.data.DataFavourite
import com.kieran.winnipegbusbackend.agency.winnipegtransit.WinnipegTransitStopIdentifier
import com.kieran.winnipegbusbackend.favourites.FavouritesService
import com.kieran.winnipegbusbackend.interfaces.FavouritesRepository
import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FavouritesServiceTest {
    private lateinit var favouritesService: FavouritesService

    @BeforeAll
    fun init() {
        val favouritesRepositoryMock = mock<FavouritesRepository> {}

        this.favouritesService = FavouritesService.getInstance(favouritesRepositoryMock, 2)
    }

    @Test
    fun testConvertAll() {
        val favourites = listOf<DataFavourite>(
                DataFavourite(1, 2, null, null, "test", null, "12345", 9, 15.05, 97.0123, null, "1;2"),
                DataFavourite(2, 0, null, null, "test", null, "12345", 9, null, null, null, null)
        )

        val converted = favouritesService.convertFromDataClass(favourites)
        assertNotNull(converted)
        assertEquals(1, converted.size)
    }

    @Test
    fun testConvertSingleSucceeds() {
        val favourite = DataFavourite(1, 2, null, null, "test", null, "12345", 9, 15.05, 97.0123, null, null)
        val converted = favouritesService.convertFromDataClass(favourite)

        assertNotNull(converted)
        assertEquals("test", converted!!.name)
        assertEquals("test", converted.displayName)
        assertEquals(WinnipegTransitStopIdentifier(12345), converted.identifier)
        assertEquals(9, converted.timesUsed)
    }

    @Test
    fun testConvertSingleFails() {
        val favourite = DataFavourite(1, 0, null, null, "test", null, "12345", 9, null, null, null, null)
        val converted = favouritesService.convertFromDataClass(favourite)

        assertNull(converted)
    }
}