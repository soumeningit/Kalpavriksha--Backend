package com.soumen.kalpavriksha.Repository;

import com.soumen.kalpavriksha.Entity.NoSQL.Blog.PostContent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogPostRepository extends MongoRepository<PostContent, String > {
    @Query(
            value = "{ 'creatorId': ?0 }",
            fields = "{ '_id': 1, 'creatorId': 1, 'thumbnail': 1, 'title': 1, 'trimContent': 1, 'description': 1, 'status': 1, 'createdAt': 1, 'likes': 1, }",
            sort = "{ 'createdAt': -1 }"
    )
    List<PostContent> findByCreatorId(String userId);

    @Query(value = "{ $text: { $search: ?0 } }",
            fields = "{ _id: 1, title: 1, description: 1, creatorName: 1, status: 1, thumbnail: 1, trimContent: 1, tags: 1, category: 1, createdAt: 1, }"
    )
    List<PostContent> searchPosts(String query);
}
