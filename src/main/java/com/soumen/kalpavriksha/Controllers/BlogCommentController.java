package com.soumen.kalpavriksha.Controllers;

import com.soumen.kalpavriksha.Auth.CustomUserDetails;
import com.soumen.kalpavriksha.Models.BlogCommentRequest;
import com.soumen.kalpavriksha.Service.BlogCommentService;
import com.soumen.kalpavriksha.Utills.Common;
import com.soumen.kalpavriksha.Utills.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/blogComment")
public class BlogCommentController
{
    @Autowired
    private BlogCommentService commentService;

    @PostMapping("/post-comment")
    public ResponseEntity<Map<String, Object>> postComment(@RequestBody BlogCommentRequest blogCommentRequest, Authentication authentication)
    {
        System.out.println("Inside postComment method in controller");

        System.out.println("blogCommentRequest : " + blogCommentRequest);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String userId = userDetails.getUsername();

        String postId = blogCommentRequest.getPostId();
        String comment = blogCommentRequest.getComment();
        String name = blogCommentRequest.getName();
        String parentCommentId = blogCommentRequest.getParentCommentId();

        if(Common.isNullOrEmpty(postId) || Common.isNullOrEmpty(comment) || Common.isNullOrEmpty(name))
        {
            return new ResponseEntity<>(Response.error("Please provide all details"), HttpStatus.BAD_REQUEST);
        }

        Map<String, Object> output = commentService.postComment(postId, comment, name, userId, parentCommentId);

        if(!(boolean) output.get("success"))
        {
            return new ResponseEntity<>(Response.error(output.get("message")), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(Response.success(output.get("message").toString(), output.get("data")), HttpStatus.OK);
    }

    @GetMapping("/public-get-comments")
    public ResponseEntity<Map<String, Object>> getComments(@RequestParam String postId)
    {
        System.out.println("Inside getComments method in controller");

        System.out.println("postId : " + postId);

        Map<String, Object> output = commentService.getComments(postId);

        if(!(boolean) output.get("success"))
        {
            return new ResponseEntity<>(Response.error(output.get("message")), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(Response.success(output.get("message").toString(), output.get("data")), HttpStatus.OK);
    }

}
