package com.skkil.sync.user.service;

import com.skkil.sync.common.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class UserEmailService {

  private final EmailService emailService;
  private final SpringTemplateEngine templateEngine;

  public void sendVerificationEmail(String to, String code) {
    Context context = new Context();
    context.setVariable("code", code);

    String html = templateEngine.process("verification-mail", context);

    emailService.sendHtmlEmail(to, "[Sync] 이메일 인증 코드", html);
  }
}
