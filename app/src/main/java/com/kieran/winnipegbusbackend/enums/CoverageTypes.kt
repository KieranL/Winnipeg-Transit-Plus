package com.kieran.winnipegbusbackend.enums

import java.io.Serializable

enum class CoverageTypes private constructor(var typeName: String) : Serializable {
    REGULAR("regular"),
    EXPRESS("express"),
    SUPER_EXPRESS("super express"),
    RAPID_TRANSIT("rapid transit");


    companion object {

        fun getEnum(coverageType: String?): CoverageTypes {
            return if (coverageType != null) {
                if (coverageType == CoverageTypes.EXPRESS.typeName)
                    CoverageTypes.EXPRESS
                else if (coverageType == CoverageTypes.SUPER_EXPRESS.typeName)
                    CoverageTypes.SUPER_EXPRESS
                else if (coverageType == CoverageTypes.RAPID_TRANSIT.typeName)
                    CoverageTypes.RAPID_TRANSIT
                else
                    CoverageTypes.REGULAR
            } else {
                CoverageTypes.REGULAR
            }
        }
    }
}
