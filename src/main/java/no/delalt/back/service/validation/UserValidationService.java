package no.delalt.back.service.validation;

import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.repository.UserRepository;
import no.delalt.back.util.SecurityUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
public class UserValidationService {
  private final UserRepository userRepository;

  public UserValidationService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Validates if a user exists and returns the UserDAO object.
   *
   * @param userID the ID of the user
   * @return the UserDAO object if the user exists
   * @throws ResponseStatusException if the user does not exist
   */
  public UserDAO validateUserExistsAndReturn(String userID)
    throws ResponseStatusException {
    return findUserByUserID(userID);
  }

  /**
   * Validates if a user with the given userID exists.
   *
   * @param userID the ID of the user to validate
   * @throws ResponseStatusException if the user does not exist
   */
  public void validateUserExists(String userID) throws ResponseStatusException {
    if (!userExists(userID)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }
  }

  /**
   * Validates if the authenticated user is the same as the user with the given userID.
   *
   * @param userID the ID of the user to validate
   * @throws ResponseStatusException if the authenticated user is not the same as the user
   */
  public void validateAuthenticatedUserIsSame(String userID)
    throws ResponseStatusException {
    if (!Objects.equals(SecurityUtil.getAuthenticatedAccountID(), userID)) {
      throw new ResponseStatusException(
        HttpStatus.FORBIDDEN,
        "You do not have permission"
      );
    }
  }

  /**
   * Validates if the authenticated user is different from the user with the given userID.
   *
   * @param userID the ID of the user to validate
   * @throws ResponseStatusException if the authenticated user is the same as the user
   */
  public void validateAuthenticatedUserIsDifferent(String userID)
    throws ResponseStatusException {
    if (Objects.equals(SecurityUtil.getAuthenticatedAccountID(), userID)) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Users are the same"
      );
    }
  }

  /**
   * Returns the UserDAO object for the user with the given userID.
   *
   * @param userID the ID of the user
   * @return the UserDAO object
   * @throws ResponseStatusException if the user does not exist
   */
  public UserDAO findUserByUserID(String userID)
    throws ResponseStatusException {
    return userRepository
      .findById(userID)
      .orElseThrow(
        () ->
          new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
      );
  }

  /**
   * Returns the UserDAO object for the user with the given email.
   *
   * @param email the email of the user
   * @return the UserDAO object
   */
  public UserDAO findUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  /**
   * Returns true if the user with the given userID exists.
   *
   * @param userID the ID of the user
   * @return true if the user exists
   */
  private boolean userExists(String userID) {
    return userRepository.existsById(userID);
  }
}
