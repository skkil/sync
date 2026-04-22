package com.skkil.sync.common.integration.email;

import com.skkil.sync.common.integration.email.dto.EmailMessage;
import com.skkil.sync.common.integration.email.exception.EmailSendingFailedException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

  private final JavaMailSender mailSender;

  public EmailService(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  @Async
  @Retryable(value = EmailSendingFailedException.class, maxRetries = 3, delay = 2000)
  public void sendMessage(EmailMessage emailMessage) {
    if (emailMessage == null) {
      log.warn("Message is null, skipping sending email");
      return;
    }

    String to = emailMessage.to();
    String subject = emailMessage.subject();

    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true);

      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(emailMessage.text(), true);

      log.debug("Sending email to {} with subject '{}'", to, subject);
      mailSender.send(message);
    } catch (Exception e) {
      log.error("Failed to send email to {}: {}", to, e.getMessage());
      throw new EmailSendingFailedException(e);
    }
  }
}
