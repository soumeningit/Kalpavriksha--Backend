package com.soumen.kalpavriksha.Models;

import com.soumen.kalpavriksha.Entity.NoSQL.Blog.BlogComment;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CommentDTO
{
    private String id;
    private String blogId;
    private String userId;
    private String name;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String parentCommentId;

    // Nested replies
    private List<CommentDTO> replies = new ArrayList<>();

    // Constructor from entity
    public CommentDTO(BlogComment comment)
    {
        this.id = comment.getId();
        this.blogId = comment.getBlogId();
        this.userId = comment.getUserId();
        this.name = comment.getName();
        this.comment = comment.getComment();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
        this.parentCommentId = comment.getParentCommentId();
    }
}

