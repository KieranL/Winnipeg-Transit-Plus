package com.kieran.winnipegbusbackend.enums;

public enum FavouritesListSortTypeIds {
    SAVED_INDEX(0),
    STOP_NUMBER_ASC(1),
    STOP_NUMBER_DESC(2),
    FREQUENCY_ASC(3),
    FREQUENCY_DESC(4);

    public int value;

    FavouritesListSortTypeIds(int sortTypeId) {
        this.value = sortTypeId;
    }
}
