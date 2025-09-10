package com.soumen.kalpavriksha.Models;

import lombok.Data;

import java.util.List;

@Data
public class BlogIncomingRequest
{
    private String thumbnail;
    private String title;
    private String content;
    private List<String> tags;
    private String category;
    private String status;
    private String description;
    private String id;
    private String creatorName;
    private String readTime;
}
