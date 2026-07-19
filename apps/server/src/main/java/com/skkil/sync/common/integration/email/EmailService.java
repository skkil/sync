package com.skkil.sync.common.integration.email;

import com.skkil.sync.common.integration.email.dto.EmailMessage;
import com.skkil.sync.common.integration.email.exception.EmailSendingFailedException;
import jakarta.mail.internet.MimeMessage;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

  /**
   * Wall-clock cap on a single send attempt. JavaMail's {@code connectiontimeout} does not cover
   * DNS resolution, so a stalled resolver can otherwise block the worker thread indefinitely.
   * Running the blocking send on its own task and bounding it with {@code Future.get(timeout)}
   * guarantees this method always returns (and logs) within {@link #SEND_TIMEOUT}, even if the
   * underlying thread that performed the send stays permanently stuck.
   */
  private static final Duration SEND_TIMEOUT = Duration.ofSeconds(15);

  private final JavaMailSender mailSender;
  private final AsyncTaskExecutor emailTaskExecutor;

  public EmailService(
      JavaMailSender mailSender,
      @Qualifier("emailTaskExecutor") AsyncTaskExecutor emailTaskExecutor) {
    this.mailSender = mailSender;
    this.emailTaskExecutor = emailTaskExecutor;
  }

  @Async("emailTaskExecutor")
  @Retryable(value = EmailSendingFailedException.class, maxRetries = 3, delay = 2000)
  public CompletableFuture<Void> sendMessage(EmailMessage emailMessage) {
    if (emailMessage == null) {
      log.warn("Message is null, skipping sending email");
      return CompletableFuture.completedFuture(null);
    }

    String to = emailMessage.to();
    String subject = emailMessage.subject();

    Future<?> attempt =
        emailTaskExecutor.submit(
            () -> {
              try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper =
                    new MimeMessageHelper(
                        message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(emailMessage.text(), true);

                log.debug("Sending email to {} with subject '{}'", to, subject);
                mailSender.send(message);
              } catch (Exception e) {
                throw new EmailSendingFailedException(e);
              }
            });

    try {
      attempt.get(SEND_TIMEOUT.toSeconds(), TimeUnit.SECONDS);
      return CompletableFuture.completedFuture(null);
    } catch (TimeoutException e) {
      attempt.cancel(true);
      log.error("Timed out sending email to {} after {}", to, SEND_TIMEOUT);
      throw new EmailSendingFailedException(e);
    } catch (ExecutionException e) {
      Throwable cause = e.getCause();
      log.error("Failed to send email to {}: {}", to, cause.getMessage());
      if (cause instanceof EmailSendingFailedException failure) {
        throw failure;
      }
      throw new EmailSendingFailedException(cause);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new EmailSendingFailedException(e);
    }
  }
}
