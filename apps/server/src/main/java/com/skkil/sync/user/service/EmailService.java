package com.skkil.sync.user.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

  private final JavaMailSender mailSender;

  public EmailService(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  public void sendVerificationEmail(String to, String code) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setTo(to);
      helper.setSubject("[Sync] 이메일 인증 코드");
      helper.setText(
          """
          <html>
          <body>
            <h2>이메일 인증</h2>
            <p>아래 인증 코드를 입력하여 회원가입을 완료해주세요.</p>
            <div style="margin:20px 0;padding:20px;background:#f5f5f5;border-radius:6px;text-align:center;">
              <p style="font-size:14px;color:#6B7280;margin:0 0 10px 0;">인증 코드</p>
              <p style="font-size:36px;font-weight:bold;color:#4F46E5;margin:0;letter-spacing:4px;">%s</p>
              <p style="font-size:12px;color:#9CA3AF;margin:10px 0 0 0;">이 코드는 10분 동안 유효합니다.</p>
            </div>
            <p style="color:#9CA3AF;font-size:12px;">이 요청을 하지 않으셨다면 본 이메일을 무시해주세요.</p>
          </body>
          </html>
          """
              .formatted(code),
          true);

      mailSender.send(message);
      log.info("Verification email sent to {}", to);
    } catch (MessagingException e) {
      log.error("Failed to send verification email to {}", to, e);
      throw new RuntimeException("이메일 발송에 실패했습니다.", e);
    }
  }
}
