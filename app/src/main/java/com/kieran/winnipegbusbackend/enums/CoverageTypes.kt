package com.kieran.winnipegbusbackend.enums

import java.io.Serializable

enum class CoverageTypes constructor(var typeName: String) : Serializable {
    REGULAR("regular"),
    EXPRESS("express"),
    SUPER_EXPRESS("super express"),
    RAPID_TRANSIT("rapid transit");


    companion object {

        fun getEnum(coverageType: String?): CoverageTypes {
            return if (coverageType != null) {
                when (coverageType) {
                    CoverageTypes.EXPRESS.typeName -> CoverageTypes.EXPRESS
                    CoverageTypes.SUPER_EXPRESS.typeName -> CoverageTypes.SUPER_EXPRESS
                    CoverageTypes.RAPID_TRANSIT.typeName -> CoverageTypes.RAPID_TRANSIT
                    else -> CoverageTypes.REGULAR
                }
            } else {
                CoverageTypes.REGULAR
            }
        }
    }
}
