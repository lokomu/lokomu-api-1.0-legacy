package no.delalt.back.configuration.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidIDValidator implements ConstraintValidator<ValidID, String> {
  private static final String REGEX_PATTERN = "^[a-zA-Z0-9]+$";
  private static final int EXPECTED_LENGTH = 21;

  /**
   * Validates the given ID string.
   *
   * @param  id       the ID string to be validated
   * @param  context  the context in which the validation is performed
   * @return          true if the ID is valid, false otherwise
   */
  @Override
  public boolean isValid(String id, ConstraintValidatorContext context) {
    if (id == null) {
      return true;
    }

    return id.length() == EXPECTED_LENGTH && id.matches(REGEX_PATTERN);
  }
}
