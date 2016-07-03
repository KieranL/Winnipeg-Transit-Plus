package com.kieran.winnipegbusbackend.enums;

import java.io.Serializable;

public enum CoverageTypes implements Serializable {
    REGULAR("regular"),
    EXPRESS("express"),
    SUPER_EXPRESS("super express"),
    RAPID_TRANSIT("rapid transit");

    public String typeName;

    CoverageTypes(String typeName) {
        this.typeName = typeName;
    }

    public static CoverageTypes getEnum(String coverageType) {
        if(coverageType != null) {
            if (coverageType.equals(CoverageTypes.EXPRESS.typeName))
                return CoverageTypes.EXPRESS;
            else if (coverageType.equals(CoverageTypes.SUPER_EXPRESS.typeName))
                return CoverageTypes.SUPER_EXPRESS;
            else if (coverageType.equals(CoverageTypes.RAPID_TRANSIT.typeName))
                return CoverageTypes.RAPID_TRANSIT;
            else
                return CoverageTypes.REGULAR;
        }else {
            return CoverageTypes.REGULAR;
        }
    }
}
