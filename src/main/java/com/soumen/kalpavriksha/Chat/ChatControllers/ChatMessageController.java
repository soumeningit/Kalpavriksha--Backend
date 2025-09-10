package com.soumen.kalpavriksha.Chat.ChatControllers;

import com.soumen.kalpavriksha.Chat.ChatModel.ChatMessage;
import com.soumen.kalpavriksha.Chat.ChatService.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Controller
public class ChatMessageController
{
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatMessageService chatMessageService;

    private static final Set<String> onlineUsers = new HashSet<>();

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor)
    {
        // System.out.println("Payload: " + chatMessage);
        String senderId = chatMessage.getSenderId();
        headerAccessor.getSessionAttributes().put("userId", senderId);
        onlineUsers.add(senderId);

        // This part needs a 'users' field in ChatMessage to broadcast the list
        // For simplicity, let's just log it for now. We can enhance this later.
        System.out.println(senderId + " joined!");

        // To properly broadcast the user list, you would enhance ChatMessage
        // and send a message to /topic/public
    }

    @MessageMapping("/chat.communityMessage")
    public void sendCommunityMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor)
    {
         System.out.println("Payload: " + chatMessage);
         Map<String , Object> output = chatMessageService.saveMessage(chatMessage);
         Map<String , Object> resp = new HashMap<>();

         resp.put("data", output.get("data"));
         resp.put("type", chatMessage.getType());

        System.out.println("output.get(\"data\")"+output.get("data"));

        messagingTemplate.convertAndSend("/topic/public", resp);
    }

    @MessageMapping("/chat.addLikes")
    public void addLikes(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor)
    {
        System.out.println("Payload addLikes: " + chatMessage);
        // chatMessageService.saveMessage(chatMessage);
        messagingTemplate.convertAndSend("/topic/public", chatMessage);
    }
}
