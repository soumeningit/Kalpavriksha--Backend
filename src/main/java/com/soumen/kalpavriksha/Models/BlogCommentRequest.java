package com.soumen.kalpavriksha.Models;

import lombok.Data;

@Data
public class BlogCommentRequest
{
    private String comment;
    private String postId;
    private String parentCommentId;
    private String name;
}
