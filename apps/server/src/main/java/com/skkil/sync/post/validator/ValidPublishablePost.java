package com.skkil.sync.post.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPublishablePostValidator.class)
public @interface ValidPublishablePost {

  String message() default "Published article and question posts require a title";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
