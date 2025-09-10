package com.soumen.kalpavriksha.Models;

import com.soumen.kalpavriksha.Entity.NoSQL.Blog.PostContent;
import com.soumen.kalpavriksha.Entity.NoSQL.Blog.Status;
import lombok.Data;

import java.util.List;

@Data
public class PostResponse
{
    private String id;
    private String thumbnail;
    private String title;
    private String content;
    private String trimContent;
    private String description;
    private Status status;
    private long totalPages;
    private long totalItems;
    private long currentPageNumber;
    private long currentPageSize;
    private boolean hasNextPage;
    private boolean hasPreviousPage;
    private List <PostContent> postContent;
    private String creatorId;
}
