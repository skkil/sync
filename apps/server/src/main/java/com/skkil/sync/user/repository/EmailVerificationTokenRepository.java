package com.skkil.sync.user.repository;

import com.skkil.sync.user.model.EmailVerificationToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationTokenRepository
    extends JpaRepository<EmailVerificationToken, Long> {

  Optional<EmailVerificationToken> findByEmail(String email);

  Optional<EmailVerificationToken> findByEmailAndToken(String email, String code);
}
