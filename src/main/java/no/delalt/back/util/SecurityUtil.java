package no.delalt.back.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

public class SecurityUtil {
  private static final Logger LOGGER = LoggerFactory.getLogger(
    SecurityUtil.class
  );

  /**
   * Retrieves the authenticated account ID.
   *
   * @return         	the authenticated account ID as a string
   */
  public static String getAuthenticatedAccountID() {
    Authentication authentication = SecurityContextHolder
      .getContext()
      .getAuthentication();
    if (authentication != null) {
      return (String) authentication.getPrincipal();
    } else {
      LOGGER.warn(
        "User ID getter failed: " + SecurityContextHolder.getContext()
      );
      throw new ResponseStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "User unauthorized."
      );
    }
  }
}
