package com.soumen.kalpavriksha.Chat.ChatService;

import com.soumen.kalpavriksha.Chat.ChatModel.ChatMessage;
import com.soumen.kalpavriksha.Chat.ChatRepo.ChatMessageRepo;
import com.soumen.kalpavriksha.Chat.ChatRepo.CommentRepo;
import com.soumen.kalpavriksha.Entity.NoSQL.Comment;
import com.soumen.kalpavriksha.Entity.NoSQL.CommunityPostDocument;
import com.soumen.kalpavriksha.Utills.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ChatRestService
{
    @Autowired
    private ChatMessageRepo repo;

    @Autowired
    private CommentRepo commentRepo;

    public Map<String , Object> getCommunityPosts(int page, int size)
    {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        try{
            Page<CommunityPostDocument> response =  repo.findAll(pageable);

            System.out.println("response : " + response);

            if(response.isEmpty())
            {
                return Response.error("No data found");
            }

            System.out.println("response : " + response.getContent());
            System.out.println("total pages : " + response.getTotalPages());
            System.out.println("total elements : " + response.getTotalElements());
            System.out.println("current page number : " + response.getNumber());
            System.out.println("current page size : " + response.getSize());
            System.out.println("has next page : " + response.hasNext());
            System.out.println("has previous page : " + response.hasPrevious());

            Map<String , Object> data = new HashMap<>();

            data.put("totalPages", response.getTotalPages());
            data.put("totalElements", response.getTotalElements());
            data.put("currentPageNumber", response.getNumber());
            data.put("currentPageSize", response.getSize());
            data.put("hasNextPage", response.hasNext());
            data.put("hasPreviousPage", response.hasPrevious());
            data.put("data", response.getContent());

            return Response.success("Data found",data);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }

    }

    public Map<String, Object> addLikes(ChatMessage chatMessage)
    {
        try {
           CommunityPostDocument communityPostDocument = repo.findById(chatMessage.getId()).get();

           List<String > likes = communityPostDocument.getLikes();

           CommunityPostDocument response;

           if(likes.contains(chatMessage.getUserId()))
           {
               likes.remove(chatMessage.getUserId());
               communityPostDocument.setLikes(likes);
               response = repo.save(communityPostDocument);
               System.out.println("remove like response : " + response);

               return Response.success("Likes removed successfully", response);
           }

           likes.add(chatMessage.getUserId());


           response = repo.save(communityPostDocument);

            System.out.println("add like response : " + response);

            return Response.success("Likes added successfully", response);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Map<String, Object> addComments(String postId, String comment, String userId, String name)
    {
        try{
           Optional<CommunityPostDocument> post = repo.findById(postId);

           if(post.isEmpty())
           {
               return Response.error("Post not found");
           }

            Comment com = new Comment();

            com.setPostId(postId);
            com.setUserId(userId);
            com.setComment(comment);
            com.setCreatedAt(LocalDateTime.now());
            com.setName(name);

            Comment response = commentRepo.save(com);

            System.out.println("response : " + response);

            return Response.success("Comment added successfully", response);

        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Map<String , Object> findCommentByPostId(String postId)
    {
        try{
            List<Comment> response = commentRepo.findByPostId(postId);

            System.out.println("response : " + response);

            return Response.success("Comment found successfully", response);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }
}
