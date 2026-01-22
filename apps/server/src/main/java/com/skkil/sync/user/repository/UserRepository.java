package com.skkil.sync.user.repository;

import com.skkil.sync.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {}
