package com.brinquedostore.api.dto;

public class SearchSuggestionDto {

    private String type;
    private String label;
    private String subtitle;
    private String url;

    public SearchSuggestionDto(String type, String label, String subtitle, String url) {
        this.type = type;
        this.label = label;
        this.subtitle = subtitle;
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getUrl() {
        return url;
    }
}
