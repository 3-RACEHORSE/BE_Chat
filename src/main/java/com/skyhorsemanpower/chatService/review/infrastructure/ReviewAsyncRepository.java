package com.skyhorsemanpower.chatService.review.infrastructure;

import com.skyhorsemanpower.chatService.review.data.vo.SearchReviewResponseVo;
import com.skyhorsemanpower.chatService.review.domain.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ReviewAsyncRepository extends ReactiveMongoRepository<Review, String> {
    @Tailable
    @Query("{ 'uuid' : ?0}")
    Flux<SearchReviewResponseVo> findAllByUuid(String uuid);
}