package com.skkil.sync.project.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotReservedHandleValidator.class)
public @interface NotReservedHandle {

  String message() default "This handle is reserved and cannot be used.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
