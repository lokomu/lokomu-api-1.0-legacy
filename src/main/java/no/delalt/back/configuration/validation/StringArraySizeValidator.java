package no.delalt.back.configuration.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StringArraySizeValidator
  implements ConstraintValidator<StringArraySize, String[]> {
  private static final int EXACT_SIZE = 21;

  /**
   * Validates an array of strings.
   *
   * @param  array    the array of strings to be validated
   * @param  context  the context for the validation
   * @return          a boolean indicating whether the array is valid or not
   */
  @Override
  public boolean isValid(String[] array, ConstraintValidatorContext context) {
    if (array == null) {
      return true;
    }

    for (String str : array) {
      if (str == null || str.length() != EXACT_SIZE) {
        return false;
      }
    }
    return true;
  }
}
