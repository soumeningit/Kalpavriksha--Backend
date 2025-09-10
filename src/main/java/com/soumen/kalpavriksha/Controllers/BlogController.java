package com.soumen.kalpavriksha.Controllers;

import com.soumen.kalpavriksha.Auth.CustomUserDetails;
import com.soumen.kalpavriksha.Models.BlogIncomingRequest;
import com.soumen.kalpavriksha.Service.BlogService;
import com.soumen.kalpavriksha.Utills.Common;
import com.soumen.kalpavriksha.Utills.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/blog")
public class BlogController
{
    @Autowired
    private BlogService blogService;

    @PostMapping("/create-post")
    public ResponseEntity<Map<String , Object>> createPost(@RequestBody BlogIncomingRequest blogRequest, Authentication authentication)
    {
        System.out.println("Inside createPost method in controller");

        System.out.println("blogRequest : " + blogRequest);

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = customUserDetails.getUsername();

        System.out.println("userId : " + userId);

        if(userId == null)
        {
            return new ResponseEntity<>(Response.error("Try Login with your own id"), HttpStatus.BAD_REQUEST);
        }

        String thumbnail = blogRequest.getThumbnail();
        String title = blogRequest.getTitle();

        if(Common.isNullOrEmpty(title))
        {
            return new ResponseEntity<>(Response.error("Title is required"), HttpStatus.BAD_REQUEST);
        }

        Map<String , Object> output = blogService.createPost(blogRequest,userId);

        if(!(boolean) output.get("success"))
        {
            return new ResponseEntity<>(Response.error(output.get("message").toString(), output.get("data")), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(Response.success("Post created successfully", output.get("data")), HttpStatus.OK);

    }

    @GetMapping("/get-all-posts-of-a-user")
    public ResponseEntity<Map<String , Object>> getAllPostsOfAUser(Authentication authentication)
    {
        System.out.println("Inside getAllPostsOfAUser method in controller");
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = customUserDetails.getUsername();

        System.out.println("userId : " + userId);

        if(userId == null)
        {
            return new ResponseEntity<>(Response.error("Try Login with your own id"), HttpStatus.BAD_REQUEST);
        }

        Map<String , Object> output = blogService.getAllPostsOfAUser(userId);

        if(!(boolean) output.get("success"))
        {
            return new ResponseEntity<>(Response.error(output.get("message").toString(), output.get("data")), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(Response.success("Posts fetched successfully", output.get("data")), HttpStatus.OK);

    }

    @PutMapping("/update-post-status/postId/{postId}")
    public ResponseEntity<Map<String , Object>> updatePostStatus(@RequestBody BlogIncomingRequest blogRequest, Authentication authentication, @PathVariable String postId)
    {
        System.out.println("Inside updateStatus method in controller");
        String status = blogRequest.getStatus();

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = customUserDetails.getUsername();

        if(userId == null)
        {
            return new ResponseEntity<>(Response.error("Try Login with your own id"), HttpStatus.BAD_REQUEST);
        }

        Map<String , Object> output = blogService.updateStatus(postId, status, userId);

        if(!(boolean) output.get("success"))
        {
            return new ResponseEntity<>(Response.error(output.get("message").toString(), output.get("data")), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(Response.success("Status updated successfully", output.get("data")), HttpStatus.OK);

    }

    @GetMapping("/public-get-details-of-a-post/postId")
    public ResponseEntity<Map<String , Object>> getDetailsOfAPost(@RequestParam String postId)
    {
        System.out.println("Inside getDetailsOfAUser method in controller");

        Map<String, Object> output = blogService.getDetailsOfAPost(postId);

        if (!(boolean) output.get("success")) {
            return new ResponseEntity<>(Response.error(output.get("message").toString(), output.get("data")), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(Response.success("Details fetched successfully", output.get("data")), HttpStatus.OK);
    }

    @GetMapping("/get-all-public-posts")
    public ResponseEntity<Map<String , Object>> getAllPosts(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size)
    {
        System.out.println("Inside getAllPosts method in controller");

        Map<String, Object> output = blogService.getAllPosts(page, size);

        if (!(boolean) output.get("success"))
        {
            return new ResponseEntity<>(Response.error(output.get("message").toString(), output.get("data")), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(Response.success("Posts fetched successfully", output.get("data")), HttpStatus.OK);
    }

    @PutMapping("/like/post/{postId}")
    public ResponseEntity<Map<String , Object>> addLike(@PathVariable String postId, Authentication authentication)
    {
        System.out.println("Inside addLike method in controller");
        if (postId.isEmpty())
        {
            return new ResponseEntity<>(Response.error("Post Id is missing"), HttpStatus.NOT_FOUND);
        }

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String userId = customUserDetails.getUsername();

        Map<String , Object> output = blogService.addLike(postId, userId);

        if(!(boolean) output.get("success"))
        {
            return new ResponseEntity<>(Response.error("Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(Response.success("Successful operation", output.get("data")), HttpStatus.OK);
    }

    @GetMapping("/search/post")
    public ResponseEntity<Map<String , Object>> searchPost(@RequestParam String query)
    {
        System.out.println("Inside searchPost method in controller");
        System.out.println("query : " + query);

        if (query.isEmpty() || query == null)
        {
            return new ResponseEntity<>(Response.error("Query is missing"), HttpStatus.NOT_FOUND);
        }

        Map<String , Object> output = blogService.searchPost(query.trim());

        if(!(boolean) output.get("success"))
        {
            return new ResponseEntity<>(Response.error("Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(Response.success("Successful operation", output.get("data")), HttpStatus.OK);
    }

    @GetMapping("/get-overview")
    public ResponseEntity<Map<String , Object>> getOverview(Authentication authentication)
    {
        System.out.println("Inside getOverview method in controller");

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = customUserDetails.getUsername();

        if(userId == null)
        {
            return new ResponseEntity<>(Response.error("Try Login with your own id"), HttpStatus.BAD_REQUEST);
        }
        Map<String , Object> output = blogService.getOverview(userId);
        if(!(boolean) output.get("success"))
        {
            return new ResponseEntity<>(Response.error("Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(Response.success("Successful operation", output.get("data")), HttpStatus.OK);
    }

    @DeleteMapping("/delete-post/postId/{postId}")
    public ResponseEntity<Map<String , Object>> deletePost(@PathVariable String postId, Authentication authentication)
    {
        System.out.println("Inside deletePost method in controller");
        if (postId.isEmpty())
        {
            return new ResponseEntity<>(Response.error("Post Id is missing"), HttpStatus.NOT_FOUND);
        }

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String userId = customUserDetails.getUsername();

        Map<String , Object> output = blogService.deletePost(postId, userId);

        if(!(boolean) output.get("success"))
        {
            return new ResponseEntity<>(Response.error("Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(Response.success("Successful operation", output.get("data")), HttpStatus.OK);
    }

    @PutMapping("/edit-post/postId/{postId}")
    public ResponseEntity<Map<String , Object>> updatePost(@RequestBody BlogIncomingRequest blogRequest, Authentication authentication, @PathVariable String postId)
    {
        System.out.println("Inside updatePost method in controller");
        if (postId.isEmpty())
        {
            return new ResponseEntity<>(Response.error("Post Id is missing"), HttpStatus.NOT_FOUND);
        }

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String userId = customUserDetails.getUsername();

        Map<String , Object> output = blogService.updatePost(postId, blogRequest, userId);

        if(!(boolean) output.get("success"))
        {
            return new ResponseEntity<>(Response.error("Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(Response.success("Successful operation", output.get("data")), HttpStatus.OK);
    }

}
