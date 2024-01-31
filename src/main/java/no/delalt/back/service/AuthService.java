package no.delalt.back.service;

import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.model.dto.input.CoordinatesCreationDTO;
import no.delalt.back.model.dto.input.LoginDTO;
import no.delalt.back.model.dto.input.RegisterUserDTO;
import no.delalt.back.response.AuthResponse;
import no.delalt.back.service.save.UserSaveService;
import no.delalt.back.service.validation.UserValidationService;
import no.delalt.back.service.worker.AuthWorkerSevice;
import no.delalt.back.util.NanoIdGenerator;
import no.delalt.back.util.SanitizationUtil;
import org.locationtech.jts.geom.Point;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
public class AuthService {
  private final AuthWorkerSevice authWorkerSevice;
  private final UserValidationService userValidationService;
  private final UserSaveService userSaveService;
  private static final double MINIMUM_LATITUDE = -90;
  private static final double MAXIMUM_LATITUDE = 90;
  private static final double MINIMUM_LONGITUDE = -180;
  private static final double MAXIMUM_LONGITUDE = 180;

  public AuthService(
    AuthWorkerSevice authWorkerSevice,
    UserValidationService userValidationService,
    UserSaveService userSaveService
  ) {
    this.authWorkerSevice = authWorkerSevice;
    this.userValidationService = userValidationService;
    this.userSaveService = userSaveService;
  }

  // -------------------- Controller Methods --------------------

  /**
   * Logs in a user with the provided email and password.
   *
   * @param  loginDTO  the login information
   * @return           the authentication response
   */
  @Transactional(readOnly = true)
  public AuthResponse logInUser(LoginDTO loginDTO) {
    UserDAO user = authWorkerSevice.attemptAuthentication(
      loginDTO.email(),
      loginDTO.password()
    );

    return AuthWorkerSevice.successfulAuthentication(user);
  }

  //TODO Divide into smaller parts
  /**
   * Registers a new user with the provided information.
   *
   * @param  regInfo  the user registration information
   * @throws ResponseStatusException if the user already exists
   */
  @Transactional
  public void registerNewUser(RegisterUserDTO regInfo)
    throws ResponseStatusException {
    //VALIDATIONS
    validateUserWithEmailDoesNotExist(regInfo.email());

    AuthWorkerSevice.validatePassword(regInfo.password());

    validateCoordinates(regInfo.coordinates());


    //GENERATE SAFE INFORMATION
    String uniqueID = NanoIdGenerator.generateNanoID();

    String hashedPassword = AuthWorkerSevice.encodePassword(regInfo.password());

    String safeFirstName = SanitizationUtil.sanitize(regInfo.firstName());
    String safeLastName = SanitizationUtil.sanitize(regInfo.lastName());

    Point coordinates = authWorkerSevice.createRandomOffsetPoint(
      regInfo.coordinates().latitude(),
      regInfo.coordinates().longitude()
    );


    //CREATE USER
    UserDAO newUser = new UserDAO();

    newUser.setUserID(uniqueID);
    newUser.setEmail(regInfo.email());
    newUser.setFirstName(safeFirstName);
    newUser.setLastName(safeLastName);
    newUser.setCoordinates(coordinates);
    newUser.setImage(null);
    newUser.setLastLocationUpdate(LocalDate.now());
    newUser.setHash(hashedPassword);
    newUser.setDeletedAt(null);

    userSaveService.saveUser(newUser);
  }

  // -------------------- Helper Methods --------------------

  /**
   * Check if the given latitude is valid.
   *
   * @param  latitude  the latitude to be checked
   * @return           true if the latitude is valid, false otherwise
   */
  private static boolean isValidLatitude(double latitude) {
    return latitude >= MINIMUM_LATITUDE && latitude <= MAXIMUM_LATITUDE;
  }

  /**
   * Check if the given longitude is valid.
   *
   * @param  longitude  the longitude to be checked
   * @return            true if the longitude is valid, false otherwise
   */
  private static boolean isValidLongitude(double longitude) {
    return longitude >= MINIMUM_LONGITUDE && longitude <= MAXIMUM_LONGITUDE;
  }

  /**
   * Determines if the given coordinates are valid.
   *
   * @param  coordinates  the coordinates to be validated
   * @return              true if the coordinates are valid, false otherwise
   */
  private static boolean isValidCoordinates(
    CoordinatesCreationDTO coordinates
  ) {
    return (
      isValidLatitude(coordinates.latitude()) &&
      isValidLongitude(coordinates.longitude())
    );
  }

  /**
   * Validates the given coordinates.
   *
   * @param  coordinates  the coordinates to be validated
   */
  private static void validateCoordinates(CoordinatesCreationDTO coordinates) {
    if (!isValidCoordinates(coordinates)) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Coordinates are invalid"
      );
    }
  }

  private void validateUserWithEmailDoesNotExist(String email) {
    if (
            userValidationService.findUserByEmail(email) != null
    ) throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Unable to register user with provided information"
    );
  }
}
