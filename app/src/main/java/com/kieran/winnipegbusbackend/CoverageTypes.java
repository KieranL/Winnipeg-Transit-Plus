package com.kieran.winnipegbusbackend;

public enum CoverageTypes {
    REGULAR(1, "regular"),
    EXPRESS(2, "express"),
    RAPID_TRANSIT(3, "rapid transit");

    public int typeId;
    public String typeName;

    CoverageTypes(int typeId, String typeName) {
        this.typeId = typeId;
        this.typeName = typeName;
    }
}
