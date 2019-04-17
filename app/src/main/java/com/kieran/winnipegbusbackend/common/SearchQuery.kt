package com.kieran.winnipegbusbackend.common

import com.kieran.winnipegbusbackend.enums.SearchQueryType

import java.io.Serializable

class SearchQuery(val query: String, val searchQueryType: SearchQueryType) : Serializable
