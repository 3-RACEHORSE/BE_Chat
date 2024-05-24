package com.skyhorsemanpower.chatService.review.infrastructure;

import com.skyhorsemanpower.chatService.review.data.vo.SearchAuctionReviewResponseVo;
import com.skyhorsemanpower.chatService.review.domain.Review;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewSyncRepository extends MongoRepository<Review, String> {

    Review findByAuctionUuid(String auctionUuid);
}
