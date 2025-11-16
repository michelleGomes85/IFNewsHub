package com.android.ifnewshub.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.UUID;

public class News {

    private final String id = UUID.randomUUID().toString();

    @SerializedName("titulo")
    private String title;

    @SerializedName("descricao")
    private String description;

    @SerializedName("texto")
    private String content;

    @SerializedName("link")
    private String link;

    @SerializedName("tags")
    private List<String> tags;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getContent() {
        return content;
    }

    public String getLink() {
        return link;
    }

    public List<String> getTags() {
        return tags;
    }
}
