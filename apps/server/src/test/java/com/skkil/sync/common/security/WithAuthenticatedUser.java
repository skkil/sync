package com.skkil.sync.common.security;

import com.skkil.sync.user.constant.Role;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.test.context.support.WithSecurityContext;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithAuthenticatedUserSecurityContextFactory.class)
public @interface WithAuthenticatedUser {

  long id() default 1L;

  String name() default "User Name";

  String email() default "email@email.com";

  String password() default "password";

  Role role() default Role.USER;
}
