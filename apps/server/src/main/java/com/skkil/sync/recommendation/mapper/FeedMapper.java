package com.skkil.sync.recommendation.mapper;

import com.skkil.sync.recommendation.dto.data.FeedDto;
import com.skkil.sync.recommendation.dto.response.GetFeedResponse;
import java.net.URL;
import java.util.Map;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FeedMapper {

  default GetFeedResponse.FeedItem toFeedItemResponse(
      FeedDto feedDto, Map<Long, URL> profileImageUrls) {
    URL profileImageUrl = profileImageUrls.get(feedDto.authorProfileImageId());

    GetFeedResponse.Author author =
        GetFeedResponse.Author.builder()
            .id(feedDto.authorId())
            .handle(feedDto.authorHandle())
            .name(feedDto.authorName())
            .profileImageUrl(profileImageUrl == null ? null : profileImageUrl.toExternalForm())
            .build();

    return GetFeedResponse.FeedItem.builder()
        .id(feedDto.id())
        .author(author)
        .content(feedDto.content())
        .likeCount(feedDto.likeCount())
        .commentCount(feedDto.commentCount())
        .createdAt(feedDto.createdAt())
        .build();
  }
}
