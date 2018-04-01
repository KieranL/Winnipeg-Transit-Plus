package com.kieran.winnipegbusbackend.enums

enum class FavouritesListSortType {
    STOP_NUMBER_ASC,
    STOP_NUMBER_DESC,
    FREQUENCY_ASC,
    FREQUENCY_DESC;


    companion object {

        fun getEnum(value: String): FavouritesListSortType {
            return when (Integer.parseInt(value)) {
                1 -> STOP_NUMBER_DESC
                2 -> FREQUENCY_ASC
                3 -> FREQUENCY_DESC
                else -> STOP_NUMBER_ASC
            }
        }
    }

}
