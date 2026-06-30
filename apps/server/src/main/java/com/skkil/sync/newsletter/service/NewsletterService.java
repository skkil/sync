package com.skkil.sync.newsletter.service;

import com.skkil.sync.common.integration.email.EmailService;
import com.skkil.sync.common.integration.email.dto.EmailMessage;
import com.skkil.sync.newsletter.dto.NewsletterPost;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.repository.UserRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@Slf4j
public class NewsletterService {

  private static final int NEWSLETTER_POST_LIMIT = 3;
  private static final int RECIPIENT_PAGE_SIZE = 100;

  private final UserRepository userRepository;
  private final NewsletterPostProvider newsletterPostProvider;
  private final EmailService emailService;
  private final SpringTemplateEngine templateEngine;

  public NewsletterService(
      UserRepository userRepository,
      NewsletterPostProvider newsletterPostProvider,
      EmailService emailService,
      SpringTemplateEngine templateEngine) {
    this.userRepository = userRepository;
    this.newsletterPostProvider = newsletterPostProvider;
    this.emailService = emailService;
    this.templateEngine = templateEngine;
  }

  @Transactional(readOnly = true)
  public int sendNewsletterToVerifiedUsers() {
    int sentCount = 0;
    PageRequest pageRequest = PageRequest.of(0, RECIPIENT_PAGE_SIZE);
    Page<User> recipients;

    do {
      recipients = userRepository.findVerifiedEmailUsers(pageRequest);

      for (User recipient : recipients) {
        if (sendNewsletter(recipient)) {
          sentCount++;
        }
      }

      pageRequest = pageRequest.next();
    } while (recipients.hasNext());

    log.info("뉴스레터 발송 요청 완료: {}명", sentCount);
    return sentCount;
  }

  private boolean sendNewsletter(User recipient) {
    List<NewsletterPost> posts =
        newsletterPostProvider.getPosts(recipient.getId(), NEWSLETTER_POST_LIMIT);
    if (posts.isEmpty()) {
      log.debug("뉴스레터에 포함할 포스트가 없어 사용자 {} 발송을 건너뜁니다.", recipient.getId());
      return false;
    }

    Context context = new Context();
    context.setVariable("recipientName", recipient.getFullName());
    context.setVariable("posts", posts);

    EmailMessage email =
        EmailMessage.builder()
            .to(recipient.getEmail())
            .subject("sync 뉴스레터")
            .text(templateEngine.process("email/newsletter", context))
            .build();

    emailService.sendMessage(email);
    return true;
  }
}
