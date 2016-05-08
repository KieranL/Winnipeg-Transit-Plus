package com.kieran.winnipegbusbackend.enums;

public enum FavouritesListSortType {
    STOP_NUMBER_ASC,
    STOP_NUMBER_DESC,
    FREQUENCY_ASC,
    FREQUENCY_DESC;

    public static FavouritesListSortType getEnum(String value) {
        switch (Integer.parseInt(value)) {
            default: return STOP_NUMBER_ASC;
            case 1: return STOP_NUMBER_DESC;
            case 2: return FREQUENCY_ASC;
            case 3: return FREQUENCY_DESC;
        }
    }

}
