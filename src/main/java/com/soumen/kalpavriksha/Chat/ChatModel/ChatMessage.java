package com.soumen.kalpavriksha.Chat.ChatModel;

import lombok.Data;

import java.util.List;

@Data
public class ChatMessage
{
    private String senderId;
    private String message;
    private String imageUrl;
    private String id; // communityMessageId
    private String name;
    private String userId;
    private List<String > likes;
    private String type;
    private boolean flag;
}
