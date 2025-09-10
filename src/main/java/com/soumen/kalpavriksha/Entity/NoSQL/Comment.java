package com.soumen.kalpavriksha.Entity.NoSQL;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "comments")
public class Comment
{
    @Id
    private String id;
    private String postId;
    private String userId;
    private String comment;
    private LocalDateTime createdAt;
    private String name;
}
