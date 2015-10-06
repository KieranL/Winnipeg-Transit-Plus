package com.kieran.winnipegbusbackend.enums;

public enum SearchQueryTypeIds {
    GENERAL(0),
    ROUTE_NUMBER(1);

    public int searchQueryTypeId;

    SearchQueryTypeIds(int searchQueryTypeId) {
        this.searchQueryTypeId = searchQueryTypeId;
    }
}
