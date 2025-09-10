package com.soumen.kalpavriksha.Repository;

import com.soumen.kalpavriksha.Entity.NoSQL.Blog.BlogComment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BlogCommentRepository extends MongoRepository<BlogComment, String >
{
    List<BlogComment> findByBlogId(String postId);

    int countByBlogId(String id);

    void deleteByBlogId(String postId);
}
