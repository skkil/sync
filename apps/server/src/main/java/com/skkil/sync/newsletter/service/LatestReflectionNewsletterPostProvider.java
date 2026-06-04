package com.skkil.sync.newsletter.service;

import com.skkil.sync.newsletter.dto.NewsletterPost;
import com.skkil.sync.newsletter.dto.NewsletterPostCandidate;
import com.skkil.sync.newsletter.repository.NewsletterPostQueryRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class LatestReflectionNewsletterPostProvider implements NewsletterPostProvider {

  private static final int EXCERPT_MAX_LENGTH = 160;

  private final NewsletterPostQueryRepository newsletterPostQueryRepository;
  private final String frontendBaseUrl;

  public LatestReflectionNewsletterPostProvider(
      NewsletterPostQueryRepository newsletterPostQueryRepository,
      @Value("${app.frontend-base-url:http://localhost:3000}") String frontendBaseUrl) {
    this.newsletterPostQueryRepository = newsletterPostQueryRepository;
    this.frontendBaseUrl = frontendBaseUrl;
  }

  @Override
  public List<NewsletterPost> getPosts(Long recipientId, int limit) {
    return newsletterPostQueryRepository.findLatestReflections(limit).stream()
        .map(this::toNewsletterPost)
        .toList();
  }

  private NewsletterPost toNewsletterPost(NewsletterPostCandidate candidate) {
    String excerpt = createExcerpt(candidate.content());
    String title =
        candidate.title() == null || candidate.title().isBlank() ? excerpt : candidate.title();
    String url =
        UriComponentsBuilder.fromUriString(frontendBaseUrl)
            .pathSegment("posts", candidate.slug())
            .build()
            .toUriString();

    return new NewsletterPost(
        candidate.id(),
        candidate.slug(),
        title,
        excerpt,
        candidate.authorName(),
        candidate.createdAt(),
        url);
  }

  private String createExcerpt(String content) {
    String plainText = content.replaceAll("<[^>]*>", " ").replaceAll("\\s+", " ").trim();
    if (plainText.length() <= EXCERPT_MAX_LENGTH) {
      return plainText;
    }

    return plainText.substring(0, EXCERPT_MAX_LENGTH).trim() + "...";
  }
}
