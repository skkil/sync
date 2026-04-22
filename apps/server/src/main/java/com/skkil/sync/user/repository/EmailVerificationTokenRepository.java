package com.skkil.sync.user.repository;

import com.skkil.sync.user.model.EmailVerificationToken;
import com.skkil.sync.user.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationTokenRepository
    extends JpaRepository<EmailVerificationToken, Long> {

  Optional<EmailVerificationToken> findByUser(User user);

  Optional<EmailVerificationToken> findByUserAndToken(User user, String token);
}
