package com.kieran.winnipegbusbackend;

public class SearchQuery {
    private String query;
    private String queryUrl;
    private int searchQueryTypeId;

    public SearchQuery(String query, String queryURL, int searchQueryTypeId) {
        this.query = query;
        this.queryUrl = queryURL;
        this.searchQueryTypeId = searchQueryTypeId;
    }

    public String getQuery() {
        return query;
    }

    public int getSearchQueryTypeId() {
        return searchQueryTypeId;
    }

    public String getQueryUrl() {
        return queryUrl;
    }
}
