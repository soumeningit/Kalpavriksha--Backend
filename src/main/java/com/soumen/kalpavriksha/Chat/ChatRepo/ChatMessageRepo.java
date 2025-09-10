package com.soumen.kalpavriksha.Chat.ChatRepo;

import com.soumen.kalpavriksha.Entity.NoSQL.CommunityPostDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepo extends MongoRepository<CommunityPostDocument, String> {
}
