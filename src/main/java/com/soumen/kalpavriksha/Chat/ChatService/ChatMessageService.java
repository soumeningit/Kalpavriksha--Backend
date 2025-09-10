package com.soumen.kalpavriksha.Chat.ChatService;

import com.soumen.kalpavriksha.Chat.ChatModel.ChatMessage;
import com.soumen.kalpavriksha.Chat.ChatRepo.ChatMessageRepo;
import com.soumen.kalpavriksha.Entity.NoSQL.CommunityPostDocument;
import com.soumen.kalpavriksha.Utills.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class ChatMessageService
{
    @Autowired
    private ChatMessageRepo chatMessageRepo;

    public Map<String , Object> saveChatMessage(ChatMessage chatMessage) // called from upload image controller
    {
        CommunityPostDocument communityPostDocument = new CommunityPostDocument();
        communityPostDocument.setSenderId(chatMessage.getSenderId());
        communityPostDocument.setImageUrl(chatMessage.getImageUrl());

        try {
            CommunityPostDocument response = chatMessageRepo.save(communityPostDocument);

            System.out.println("response : " + response);

            return Response.success(response);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Map<String , Object> saveMessage(ChatMessage chatMessage)
    {
        CommunityPostDocument communityPostDocument = new CommunityPostDocument();
        communityPostDocument.setSenderId(chatMessage.getSenderId());
        communityPostDocument.setImageUrl(chatMessage.getImageUrl());
        communityPostDocument.setContent(chatMessage.getMessage());
        communityPostDocument.setName(chatMessage.getName());
        communityPostDocument.setCreatedAt(LocalDateTime.now());

        try {
            CommunityPostDocument response = chatMessageRepo.save(communityPostDocument);
            System.out.println("response : " + response);
            return Response.success("Message saved successfully",response);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }
}
