package no.delalt.back.configuration.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = StringArraySizeValidator.class)
@Target(
  {
    ElementType.FIELD,
    ElementType.METHOD,
    ElementType.PARAMETER,
    ElementType.ANNOTATION_TYPE
  }
)
@Retention(RetentionPolicy.RUNTIME)
public @interface StringArraySize {
  String message() default "Each string in the array must be exactly 21 characters long";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
