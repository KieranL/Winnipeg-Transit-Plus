package com.kieran.winnipegbustests.unit

import com.kieran.winnipegbusbackend.favourites.DataFavourite
import com.kieran.winnipegbusbackend.agency.winnipegtransit.WinnipegTransitStopIdentifier
import com.kieran.winnipegbusbackend.favourites.FavouritesService
import com.kieran.winnipegbusbackend.favourites.FavouritesRepository
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FavouritesServiceTest {
    private lateinit var favouritesService: FavouritesService
    private lateinit var favouritesRepoMock: FavouritesRepository

    @BeforeAll
    fun init() {
        favouritesRepoMock = mock {}

        this.favouritesService = FavouritesService.getInstance(favouritesRepoMock, 1)
    }

    @Test
    fun testConvertAll() {
        val favourites = listOf(
                DataFavourite(1, 1, null, null, "test", null, "12345", 9, 15.05, 97.0123, null),
                DataFavourite(2, 0, null, null, "test", null, "12345", 9, null, null, null)
        )

        val converted = favouritesService.convertFromDataClass(favourites)
        assertNotNull(converted)
        assertEquals(1, converted.size)
    }

    @Test
    fun testConvertSingleSucceeds() {
        val favourite = DataFavourite(1, 1, null, null, "test", null, "12345", 9, 15.05, 97.0123, null)
        val converted = favouritesService.convertFromDataClass(favourite)

        assertNotNull(converted)
        assertEquals("test", converted!!.name)
        assertEquals("test", converted.displayName)
        assertEquals(WinnipegTransitStopIdentifier(12345), converted.identifier)
        assertEquals(9, converted.timesUsed)
    }

    @Test
    fun testConvertSingleFails() {
        val favourite = DataFavourite(1, 0, null, null, "test", null, "12345", 9, null, null, null)
        val converted = favouritesService.convertFromDataClass(favourite)

        assertNull(converted)
    }

    @Test
    fun testGetAll() {
        favouritesRepoMock = mock {
            on { hasBeenImported() } doReturn true
        }

        this.favouritesService = FavouritesService.getInstance(favouritesRepoMock, 1)
        favouritesService.getAll()
    }
}