package no.delalt.back.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.model.dto.input.PasswordDTO;
import no.delalt.back.model.dto.input.UserModifyDTO;
import no.delalt.back.model.dto.output.UserDTO;
import no.delalt.back.response.AuthResponse;
import no.delalt.back.service.deletion.ImageDeletionService;
import no.delalt.back.service.save.UserSaveService;
import no.delalt.back.service.validation.ImageValidationService;
import no.delalt.back.service.validation.UserValidationService;
import no.delalt.back.service.worker.AuthWorkerSevice;
import no.delalt.back.util.SanitizationUtil;
import no.delalt.back.util.SecurityUtil;
import org.locationtech.jts.geom.Point;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {
  private final UserSaveService userSaveService;
  private final UserValidationService userValidationService;

  private final ImageDeletionService imageDeletionService;
  private final ImageValidationService imageValidationService;

  private final AuthWorkerSevice authWorkerSevice;

  private final static int LOCATION_UPDATE_LIMIT = 30; //In days

  public UserService(
    UserSaveService userSaveService,
    UserValidationService userValidationService,
    ImageDeletionService imageDeletionService,
    ImageValidationService imageValidationService,
    AuthWorkerSevice authWorkerSevice
  ) {
    this.userSaveService = userSaveService;
    this.userValidationService = userValidationService;
    this.imageDeletionService = imageDeletionService;
    this.imageValidationService = imageValidationService;
    this.authWorkerSevice = authWorkerSevice;
  }

  // -------------------- Controller Methods --------------------

  /**
   * Retrieves the current user's data access object (DAO) based on the provided user ID.
   *
   * @return         the data transfer object (DTO) representing the user
   */
  @Transactional(readOnly = true)
  public UserDTO retrieveCurrentUserDAO() {
    String userID = SecurityUtil.getAuthenticatedAccountID();
    UserDAO userDAO = userValidationService.validateUserExistsAndReturn(userID);
    return new UserDTO(userDAO);
  }

  /**
   * Updates the user profile with the provided user ID and user DTO.
   *
   * @param  userDTO   the user DTO containing the updated user information
   */
  @Transactional
  public void updateUserProfile(UserModifyDTO userDTO) {
    String userID = SecurityUtil.getAuthenticatedAccountID();
    UserDAO userDAO = userValidationService.validateUserExistsAndReturn(userID);

    if (
      userDAO.getImage() != null &&
      !Objects.equals(userDAO.getImage(), userDTO.image())
    ) {
      imageDeletionService.deleteImage(userDAO.getImage());
    }

    String safeFirstName = SanitizationUtil.sanitize(userDTO.firstName());
    String safeLastName = SanitizationUtil.sanitize(userDTO.lastName());

    userDAO.setFirstName(safeFirstName);
    userDAO.setLastName(safeLastName);

    if (userDTO.image() != null) {
      imageValidationService.validateImageOwnedByUser(userDTO.image());
    }
    //TODO Validate the image String
    userDAO.setImage(userDTO.image());

    userSaveService.saveUser(userDAO);
  }

  /**
   * Changes the user's password.
   *
   * @param  passwordDTO the new password
   * @throws ResponseStatusException if the user does not exist
   */
  @Transactional
  public AuthResponse changeUserPassword(PasswordDTO passwordDTO) {
    String userID = SecurityUtil.getAuthenticatedAccountID();
    UserDAO userDAO = userValidationService.validateUserExistsAndReturn(userID);
    AuthWorkerSevice.attemptAuthenticationOfPassword(
      userDAO,
      passwordDTO.oldPassword()
    );
    AuthWorkerSevice.validatePassword(passwordDTO.newPassword());

    UserDAO changedUser = authWorkerSevice.changePasswordForUser(
      userDAO,
      passwordDTO.newPassword()
    );

    return AuthWorkerSevice.successfulAuthentication(changedUser);
  }

  // -------------------- Helper Methods --------------------

  private void updateLocation(String userID, Point newLocation)
    throws ResponseStatusException {
    UserDAO user = userValidationService.validateUserExistsAndReturn(userID);

    LocalDate lastUpdated = user.getLastLocationUpdate();
    LocalDate now = LocalDate.now();
    long daysSinceLastUpdate = ChronoUnit.DAYS.between(lastUpdated, now);

    if (daysSinceLastUpdate >= LOCATION_UPDATE_LIMIT) {
      user.setCoordinates(newLocation);
      user.setLastLocationUpdate(now);
      userSaveService.saveUser(user);
    } else {
      long daysRemaining = LOCATION_UPDATE_LIMIT - daysSinceLastUpdate;
      throw new ResponseStatusException(
        HttpStatus.FORBIDDEN,
        "Can change location in " + daysRemaining + " days"
      );
    }
  }
}
