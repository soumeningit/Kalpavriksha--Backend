package com.soumen.kalpavriksha.Entity.NoSQL.Blog;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "post")
@Data
public class PostContent
{
    @Id
    private String id;
    private String creatorId;
    private String creatorName;
    private String thumbnail;
    private String title;
    private String content;
    private String trimContent; // store smallest version of content
    private String description;
    private Status status;
    private LocalDateTime createdAt;
    private List<String> tags = new ArrayList<>();
    private String category;
    private List<String> visitors = new ArrayList<>();
    private List<String> likes = new ArrayList<>();
    public LocalDateTime updatedAt;
    public String readTime;
}
/*
db.post.createIndex({
    creatorName: "text",
            content: "text",
            description: "text",
            title: "text"
})
*/
