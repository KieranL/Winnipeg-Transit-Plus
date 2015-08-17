package winnipegbusbackend;

public enum FavouritesListSortTypeId {
    SAVED_INDEX(0),
    STOP_NUBMER_ASC(1),
    STOP_NUBMER_DESC(2),
    FREQUENCY_ASC(3),
    FREQUENCY_DESC(4);

    public int value;

    FavouritesListSortTypeId(int sortTypeId) {
        this.value = sortTypeId;
    }
}
