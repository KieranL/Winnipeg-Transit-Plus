package com.kieran.winnipegbusbackend.enums;

public enum CoverageTypes {
    REGULAR(1, "regular"),
    EXPRESS(2, "express"),
    SUPER_EXPRESS(3, "super express"),
    RAPID_TRANSIT(4, "rapid transit");

    public int typeId;
    public String typeName;

    CoverageTypes(int typeId, String typeName) {
        this.typeId = typeId;
        this.typeName = typeName;
    }
}
