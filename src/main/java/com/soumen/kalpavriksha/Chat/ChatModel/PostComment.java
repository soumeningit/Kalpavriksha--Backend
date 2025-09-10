package com.soumen.kalpavriksha.Chat.ChatModel;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostComment
{
    private String id;
    private String comment;
    private String userId;
    private String postId;
    private LocalDateTime createdAt;
    private String name;
}
