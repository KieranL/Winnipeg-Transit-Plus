package com.kieran.winnipegbusbackend;

public enum SearchQueryTypeIds {
    GENERAL(0),
    ROUTE_NUMBER(1);

    public int searchQueryTypeId;

    SearchQueryTypeIds(int searchQueryTypeId) {
        this.searchQueryTypeId = searchQueryTypeId;
    }
}
