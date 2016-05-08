package com.kieran.winnipegbusbackend;

import com.kieran.winnipegbusbackend.enums.SearchQueryType;

import java.io.Serializable;

public class SearchQuery implements Serializable {
    private String query;
    private String queryUrl;
    private SearchQueryType searchQueryType;

    public SearchQuery(String query, String queryURL, SearchQueryType searchQueryType) {
        this.query = query;
        this.queryUrl = queryURL;
        this.searchQueryType = searchQueryType;
    }

    public String getQuery() {
        return query;
    }

    public SearchQueryType getSearchQueryType() {
        return searchQueryType;
    }

    public String getQueryUrl() {
        return queryUrl;
    }
}
