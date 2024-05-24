package com.skyhorsemanpower.chatService.review.infrastructure;

import com.skyhorsemanpower.chatService.review.domain.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewSyncRepository extends MongoRepository<Review, String> {

}
