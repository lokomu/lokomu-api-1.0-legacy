package no.delalt.back.configuration.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ValidIDValidator.class)
@Target(
  {
    ElementType.METHOD,
    ElementType.FIELD,
    ElementType.PARAMETER,
    ElementType.ANNOTATION_TYPE
  }
)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidID {
  String message() default "Invalid ID";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
