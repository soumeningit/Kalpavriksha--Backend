package com.soumen.kalpavriksha.Entity.NoSQL;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "CommunityPost")
public class CommunityPostDocument
{
    @Id
    private String id;
    private String senderId; // who send the data
    private String name;
    private String content;
    private String imageUrl;
    private List<String> likes = new ArrayList<>();
    private LocalDateTime createdAt;
}

