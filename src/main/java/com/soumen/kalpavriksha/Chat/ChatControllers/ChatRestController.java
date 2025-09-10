package com.soumen.kalpavriksha.Chat.ChatControllers;

import com.soumen.kalpavriksha.Chat.ChatModel.ChatMessage;
import com.soumen.kalpavriksha.Chat.ChatModel.PostComment;
import com.soumen.kalpavriksha.Chat.ChatService.ChatRestService;
import com.soumen.kalpavriksha.Utills.Common;
import com.soumen.kalpavriksha.Utills.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatRestController
{
    @Autowired
    private ChatRestService service;

    @GetMapping("/get-community-posts")
    public ResponseEntity<Map<String, Object>> getCommunityPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        System.out.println("Inside getCommunityPosts method in controller");

        System.out.println("page : " + page);
        System.out.println("size : " + size);

        Map<String , Object> output = service.getCommunityPosts(page, size);

        System.out.println("output in get community posts : " + output);

        System.out.println("output in get community posts : " + output.get("data"));

        if(!(boolean) output.get("success"))
        {
            return new ResponseEntity<>(Response.error(output.get("message")), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(Response.success(output.get("message").toString(), output.get("data")), HttpStatus.OK);
    }

    @PostMapping("/add-likes")
    public ResponseEntity<Map<String, Object>> addLikes(@RequestBody ChatMessage chatMessage)
    {
        System.out.println("Inside addLikes method in controller");
        System.out.println("chatMessage : " + chatMessage);
        Map<String , Object> output = service.addLikes(chatMessage);
        if(!(boolean) output.get("success"))
        {
            return new ResponseEntity<>(Response.error(output.get("message")), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(Response.success(output.get("message").toString()), HttpStatus.OK);
    }

    @PostMapping("/add-comments")
    public ResponseEntity<Map<String , Object>> addComments(@RequestBody PostComment postComment)
    {
        System.out.println("Inside addComments method in controller");
        System.out.println("postComment : " + postComment);

        String postId = postComment.getPostId();
        String comment = postComment.getComment();
        String userId = postComment.getUserId();
        String name = postComment.getName();

        if(Common.isNullOrEmpty(postId) || Common.isNullOrEmpty(comment) || Common.isNullOrEmpty(userId))
        {
            return new ResponseEntity<>(Response.error("Please provide all details"), HttpStatus.BAD_REQUEST);
        }

        Map<String , Object> output = service.addComments(postId, comment, userId, name);

        System.out.println("output in add comments : " + output);

        if(!(boolean) output.get("success"))
        {
            return new ResponseEntity<>(Response.error(output.get("message")), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(Response.success(output.get("message").toString(), output.get("data")), HttpStatus.OK);

    }

    @GetMapping("/get-comments/{postId}")
    public ResponseEntity<Map<String , Object>> getComments(@PathVariable String postId)
    {
        System.out.println("Inside getComments method in controller");
        System.out.println("postId : " + postId);

        Map<String , Object> output = service.findCommentByPostId(postId);

        System.out.println("output in get comments : " + output);

        if(!(boolean) output.get("success"))
        {
            return new ResponseEntity<>(Response.error(output.get("message")), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(Response.success(output.get("message").toString(), output.get("data")), HttpStatus.OK);
    }

}
