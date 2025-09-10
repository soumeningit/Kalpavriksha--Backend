package com.soumen.kalpavriksha.Service;

import com.soumen.kalpavriksha.Entity.NoSQL.Blog.BlogComment;
import com.soumen.kalpavriksha.Models.CommentDTO;
import com.soumen.kalpavriksha.Repository.BlogCommentRepository;
import com.soumen.kalpavriksha.Utills.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BlogCommentService
{
    @Autowired
    private BlogCommentRepository blogCommentRepository;

    public Map<String, Object> postComment(String postId, String comment, String name, String userId, String parentCommentId)
    {
        try{
            BlogComment blogComment = new BlogComment();

            blogComment.setBlogId(postId);
            blogComment.setComment(comment);
            blogComment.setName(name);
            blogComment.setUserId(userId);
            blogComment.setParentCommentId(parentCommentId);
            blogComment.setCreatedAt(LocalDateTime.now());

            BlogComment response = blogCommentRepository.save(blogComment);

            System.out.println("response : " + response);

            return Response.success("Comment added successfully", response);

        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
    }

    public Map<String, Object> getComments(String postId)
    {
        try{
            List<BlogComment> comments = blogCommentRepository.findByBlogId(postId);

            if(comments.isEmpty())
            {
                return Response.error("No comments found");
            }

            System.out.println("comments : " + comments);

            // Map parentId -> list of child comments (handle null)
            Map<String, List<BlogComment>> groupedByParent =
                    comments.stream().collect(Collectors.groupingBy(c ->
                            c.getParentCommentId() == null ? "ROOT" : c.getParentCommentId()
                    ));

            // Start with root comments ("ROOT" instead of null)
            List<CommentDTO> response = groupedByParent.getOrDefault("ROOT", List.of())
                    .stream()
                    .map(c -> buildCommentTree(c, groupedByParent))
                    .toList();

            System.out.println("response : " + response);


            return Response.success("Comments fetched successfully", response);

        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
    }

    private CommentDTO buildCommentTree(BlogComment comment, Map<String, List<BlogComment>> groupedByParent) {
        CommentDTO dto = new CommentDTO(comment);
        List<BlogComment> children = groupedByParent.get(comment.getId());
        if (children != null) {
            dto.setReplies(children.stream()
                    .map(c -> buildCommentTree(c, groupedByParent))
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}
