package com.kieran.winnipegbusbackend.enums

enum class FavouritesListSortType {
    STOP_NUMBER_ASC,
    STOP_NUMBER_DESC,
    FREQUENCY_ASC,
    FREQUENCY_DESC;


    companion object {

        fun getEnum(value: String): FavouritesListSortType {
            when (Integer.parseInt(value)) {
                1 -> return STOP_NUMBER_DESC
                2 -> return FREQUENCY_ASC
                3 -> return FREQUENCY_DESC
                else -> return STOP_NUMBER_ASC
            }
        }
    }

}
