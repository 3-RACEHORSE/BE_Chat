package com.skyhorsemanpower.chatService.review.infrastructure;

import com.skyhorsemanpower.chatService.review.domain.Review;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {

    List<Review> findAllByInfluencerUuid(String influencerUuid);

    List<Review> findAllByReviewWriterUuid(String reviewWriterUuid);
}
