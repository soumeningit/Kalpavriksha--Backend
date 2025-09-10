package com.soumen.kalpavriksha.Models;

import com.soumen.kalpavriksha.Entity.NoSQL.Blog.PostContent;
import lombok.Data;

import java.util.List;

@Data
public class CreatorOverview
{
    private long totalPosts;
    private List<PostContent> posts;
    private PostContent mostLikedPost;
    private int totalLikes;
    private int totalComments;
    private long totalViews;
    private List<PostContent> recentPosts;
}
