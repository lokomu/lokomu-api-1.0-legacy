package no.delalt.back.util;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

public class SanitizationUtil {
  private static final PolicyFactory POLICY = Sanitizers.FORMATTING.and(
    Sanitizers.LINKS
  );

  /**
   * Sanitizes the input string.
   *
   * @param  input the string to be sanitized
   * @return       the sanitized string
   */
  public static String sanitize(String input) {
    return POLICY.sanitize(input);
  }
}
