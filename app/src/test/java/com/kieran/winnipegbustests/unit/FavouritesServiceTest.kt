package com.kieran.winnipegbustests.unit

import com.kieran.winnipegbus.Favourite
import com.kieran.winnipegbusbackend.agency.winnipegtransit.WinnipegTransitStopIdentifier
import com.kieran.winnipegbusbackend.common.FavouriteStop
import com.kieran.winnipegbusbackend.favourites.FavouritesService
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class FavouritesServiceTest {
    lateinit var favouritesService: FavouritesService
    fun testConvertAll() {

    }

    @Test
    fun testConvertSingleSucceeds() {
        val favourite = Favourite(1,1,null,null, "test", null, "12345", 9, 15.05, 97.0123, null)
        val converted = favouritesService.convert(favourite)
    }

    @Test
    fun testConvertSingleFails(favourite: Favourite) {
        val converted = favouritesService.convert(favourite)
    }
}