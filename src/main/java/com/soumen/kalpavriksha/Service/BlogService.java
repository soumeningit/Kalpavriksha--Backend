package com.soumen.kalpavriksha.Service;

import com.soumen.kalpavriksha.Entity.NoSQL.Blog.BlogComment;
import com.soumen.kalpavriksha.Entity.NoSQL.Blog.PostContent;
import com.soumen.kalpavriksha.Entity.NoSQL.Blog.Status;
import com.soumen.kalpavriksha.Models.BlogIncomingRequest;
import com.soumen.kalpavriksha.Models.CreatorOverview;
import com.soumen.kalpavriksha.Models.PostResponse;
import com.soumen.kalpavriksha.Repository.BlogCommentRepository;
import com.soumen.kalpavriksha.Repository.BlogPostRepository;
import com.soumen.kalpavriksha.Utills.JsoupHtmlSanitizer;
import com.soumen.kalpavriksha.Utills.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BlogService
{
    private static final int MAX_HTML_LEN = 100_000_000; // adjust as needed
    private static final int EXCERPT_LEN = 200;

    @Autowired
    private BlogPostRepository blogRepository;

    @Autowired
    private BlogCommentRepository blogCommentRepository;

    public Map<String, Object> createPost(BlogIncomingRequest blogData,String userId)
    {
        try {
            String safeHtml = JsoupHtmlSanitizer.sanitize(blogData.getContent(), MAX_HTML_LEN);
            String excerpt = JsoupHtmlSanitizer.toPlainText(safeHtml, EXCERPT_LEN);

            System.out.println("safeHtml : " + safeHtml);
            System.out.println("excerpt : " + excerpt);

            PostContent content = new PostContent();
            content.setCreatorId(userId);
            content.setCreatorName(blogData.getCreatorName());
            content.setDescription(blogData.getDescription());
            content.setThumbnail(blogData.getThumbnail());
            content.setTitle(blogData.getTitle());
            content.setContent(safeHtml);
            content.setTrimContent(excerpt);
            content.setStatus(blogData.getStatus().equalsIgnoreCase("published") ? Status.PUBLISHED : Status.DRAFT);
            content.setCategory(blogData.getCategory());
            content.setTags(blogData.getTags());
            content.setCreatedAt(LocalDateTime.now());
            content.setCreatorName(blogData.getCreatorName());
            content.setReadTime(blogData.getReadTime());

            System.out.println("content before saving : " + content);

            PostContent response = blogRepository.save(content);

            System.out.println("response : " + response);

            return Response.success("Post created successfully", response);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
    }

    public Map<String, Object> updateStatus(String postId, String status, String userId)
    {
        try{
            PostContent content = blogRepository.findById(postId).get();

            if(!content.getCreatorId().equals(userId))
            {
                return Response.error("You are not authorized to update this post, you can only update your own post");
            }

            content.setStatus(status.equalsIgnoreCase("published") ? Status.PUBLISHED : Status.DRAFT);

            PostContent response = blogRepository.save(content);

            return Response.success("Status updated successfully", response.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
    }

    public Map<String, Object> getAllPostsOfAUser(String userId)
    {
        try{

            List<PostContent>posts = blogRepository.findByCreatorId(userId);

            if(posts.isEmpty())
            {
                return Response.error("No posts found for this user");
            }

            return Response.success("Posts fetched successfully", posts);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
    }

    public Map<String, Object> getDetailsOfAPost(String postId)
    {
        try{
            Optional<PostContent> optionalPostContent = blogRepository.findById(postId);

            if(optionalPostContent.isEmpty())
            {
                return Response.error("Post not found");
            }

            PostContent post = optionalPostContent.get();

            System.out.println("post : " + post);

            return Response.success("Post fetched successfully", post);

        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
    }

    public Map<String, Object> getAllPosts(int page, int size)
    {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        try{
            Page<PostContent> response = blogRepository.findAll(pageable);

            System.out.println("response : " + response);

            if(response.isEmpty())
            {
                return Response.error("No posts found");
            }

            PostResponse postResponse = new PostResponse();

            postResponse.setTotalPages(response.getTotalPages());
            postResponse.setTotalItems(response.getTotalElements());
            postResponse.setCurrentPageNumber(response.getNumber());
            postResponse.setCurrentPageSize(response.getSize());
            postResponse.setHasNextPage(response.hasNext());
            postResponse.setPostContent(response.getContent());

            return Response.success("Posts fetched successfully", response);

        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
    }

    public Map<String, Object> addLike(String postId, String userId)
    {
        try{
           Optional<PostContent> optionalPostContent = blogRepository.findById(postId);

           if(optionalPostContent.isEmpty())
           {
               return Response.error("Post is not exist");
           }

           PostContent postContent = optionalPostContent.get();

           List<String >likes = postContent.getLikes();

           System.out.println("likes : " + likes);

           if(likes == null)
           {
               likes = new ArrayList<>();
           }

           String message="";

           if(!likes.contains(userId))
           {
               likes.add(userId);
               message = "Like added successfully";
           }
           else
           {
               likes.remove(userId);
               message = "Like removed successfully";
           }

           System.out.println("likes : " + likes);

           postContent.setLikes(likes);

           System.out.println("postContent : " + postContent);

           PostContent updatedResponse = blogRepository.save(postContent);

           System.out.println("updatedResponse : " + updatedResponse);

           return Response.success(message, updatedResponse);


        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
    }

    public Map<String, Object> searchPost(String query)
    {
        try{
            List<PostContent> response = blogRepository.searchPosts(query);
            return Response.success("Posts fetched successfully", response);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
    }

    public Map<String, Object> getOverview(String userId)
    {
        try{
            List<PostContent> postData = blogRepository.findByCreatorId(userId);

            if(postData.isEmpty())
            {
                return Response.error("No posts found for this user");
            }

            long totalPosts = postData.size();
            int likes = 0;
            int comments = 0;
            int views = 0;
            int maxLikes = Integer.MIN_VALUE;
            PostContent mostLikedPost = null;
            int count = 0;
            int length = 5;
            if(postData.size() < length)
            {
                length = postData.size();
            }

            List<String>blogIds = new ArrayList<>();
            List<PostContent> recent5Posts = new ArrayList<>();

            for(PostContent post : postData)
            {
                if(post.getLikes().size() > maxLikes)
                {
                    maxLikes = post.getLikes().size();
                    mostLikedPost = post;
                }
                if(count < length){
                    recent5Posts.add(post);
                    count++;
                }
                blogIds.add(post.getId());
                likes += post.getLikes().size();
            }

            for(String id : blogIds)
            {
                comments += blogCommentRepository.countByBlogId(id);
            }

            CreatorOverview creatorOverview = new CreatorOverview();
            creatorOverview.setTotalPosts(totalPosts);
            creatorOverview.setTotalLikes(likes);
            creatorOverview.setTotalComments(comments);
            creatorOverview.setTotalViews(views);
            creatorOverview.setMostLikedPost(mostLikedPost);
            creatorOverview.setRecentPosts(recent5Posts);

            System.out.println("creatorOverview : " + creatorOverview);

            return Response.success("Posts fetched successfully", creatorOverview);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
    }

    public Map<String, Object> deletePost(String postId, String userId)
    {
        try{
            Optional<PostContent> optionalPostContent = blogRepository.findById(postId);

            if(optionalPostContent.isEmpty())
            {
                return Response.error("Post not found");
            }

            PostContent postContent = optionalPostContent.get();

            if(!postContent.getCreatorId().equals(userId))
            {
                return Response.error("You are not authorized to delete this post");
            }

            blogCommentRepository.deleteByBlogId(postId);
            blogRepository.deleteById(postId);
            return Response.success("Post deleted successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
    }

    public Map<String, Object> updatePost(String postId, BlogIncomingRequest blogRequest, String userId)
    {
        try
        {
            Optional<PostContent> optionalPostContent = blogRepository.findById(postId);

            if(optionalPostContent.isEmpty())
            {
                return Response.error("Post not found");
            }

            PostContent postContent = optionalPostContent.get();

            if(!postContent.getCreatorId().equals(userId))
            {
                return Response.error("You are not authorized to update this post");
            }

            postContent.setTitle(blogRequest.getTitle());
            postContent.setDescription(blogRequest.getDescription());
            postContent.setContent(blogRequest.getContent());
            postContent.setThumbnail(blogRequest.getThumbnail());
            postContent.setTags(blogRequest.getTags());

            PostContent updatedResponse = blogRepository.save(postContent);

            return Response.success("Post updated successfully", updatedResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
    }
}
