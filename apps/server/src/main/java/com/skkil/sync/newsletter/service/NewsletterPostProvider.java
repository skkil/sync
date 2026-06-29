package com.skkil.sync.newsletter.service;

import com.skkil.sync.newsletter.dto.NewsletterPost;
import java.util.List;

public interface NewsletterPostProvider {

  List<NewsletterPost> getPosts(Long recipientId, int limit);
}
