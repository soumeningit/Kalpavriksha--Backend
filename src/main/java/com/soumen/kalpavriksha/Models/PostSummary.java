package com.soumen.kalpavriksha.Models;

import com.soumen.kalpavriksha.Entity.NoSQL.Blog.BlogComment;
import lombok.Data;

import java.util.List;

@Data
public class PostSummary
{
    private String id;
    private String title;
    private int totalLikes;
    private long totalComments;
    private List<BlogComment> recent5Comments;
}
