package com.soumen.kalpavriksha.Entity.NoSQL.Blog;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document
public class BlogComment
{
    @Id
    private String id;
    private String blogId;
    private String userId;
    private String comment;
    private LocalDateTime createdAt;
    private String parentCommentId;
    private String name;
    private LocalDateTime updatedAt;
}
