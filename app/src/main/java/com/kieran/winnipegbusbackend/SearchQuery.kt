package com.kieran.winnipegbusbackend

import com.kieran.winnipegbusbackend.enums.SearchQueryType

import java.io.Serializable

class SearchQuery(val query: String, val searchQueryType: SearchQueryType) : Serializable
