package com.soumen.kalpavriksha.Controllers;

import com.soumen.kalpavriksha.Chat.ChatModel.ChatMessage;
import com.soumen.kalpavriksha.Chat.ChatService.ChatMessageService;
import com.soumen.kalpavriksha.Service.SupabaseStorageService;
import com.soumen.kalpavriksha.Utills.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/file")
public class FileUploadController
{
    @Autowired
    private SupabaseStorageService storageService;

    @Autowired
    private ChatMessageService service;

    @PostMapping("/file-upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file)
    {
        System.out.println("Inside uploadFile method in controller for file upload purpose");
        try
        {
            Map<String, Object> output = storageService.uploadFile(
                    file.getBytes(),
                    file.getOriginalFilename(),
                    file.getContentType()
            );

            if(!(boolean) output.get("success"))
            {
                return new ResponseEntity<>(Response.error((String) output.get("error")), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity<>(Response.success("File uploaded successfully", output.get("data")), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Response.error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/file-upload/userId/{userId}")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable String userId)
    {
        System.out.println("Inside uploadFile method in controller");
        try {
            Map<String, Object> output = storageService.uploadFile(
                    file.getBytes(),
                    file.getOriginalFilename(),
                    file.getContentType()
            );

            if(!(boolean) output.get("success"))
            {
                return new ResponseEntity<>(Response.error((String) output.get("error")), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            System.out.println("output inside file upload controller : " + output);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setImageUrl(output.get("data").toString());
            chatMessage.setSenderId(userId);

            System.out.println("chatMessage : " + chatMessage);

            output = service.saveChatMessage(chatMessage);

            System.out.println("output inside file upload controller after message save : " + output);

            if(!(boolean) output.get("success"))
            {
                return new ResponseEntity<>(Response.error((String) output.get("error")), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity<>(Response.success("File uploaded successfully", output.get("data")), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Response.error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
