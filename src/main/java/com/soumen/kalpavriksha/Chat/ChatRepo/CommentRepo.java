package com.soumen.kalpavriksha.Chat.ChatRepo;

import com.soumen.kalpavriksha.Entity.NoSQL.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentRepo extends MongoRepository<Comment, String>
{
    List<Comment> findByPostId(String postId);
}
