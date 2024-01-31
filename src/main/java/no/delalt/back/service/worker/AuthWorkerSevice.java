package no.delalt.back.service.worker;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.model.dto.output.UserDTO;
import no.delalt.back.response.AuthResponse;
import no.delalt.back.service.save.UserSaveService;
import no.delalt.back.service.validation.UserValidationService;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AuthWorkerSevice {
  private final UserSaveService userSaveService;
  private final UserValidationService userValidationService;
  private final GeometryFactory geometryFactory;

  private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

  private static final long TWO_WEEKS_IN_MILLIS = 1209600000L;
  private static Algorithm algorithm;
  private static final Random random = new Random();
  //TODO Check these matchers
  private static final Pattern letter = Pattern.compile("[a-zA-Z]");
  private static final Pattern digit = Pattern.compile("\\d");
  private static final Pattern passwordCheck = Pattern.compile(
    "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$"
  );
  private static final double MINIMUM_LATITUDE = -90;
  private static final double MAXIMUM_LATITUDE = 90;
  private static final double MINIMUM_LONGITUDE = -180;
  private static final double MAXIMUM_LONGITUDE = 180;
  private static final double OFFSET_RANGE = 0.0007; //in degrees (~80 meters)

  @Value("${not.secret.key}")
  private void setAlgorithmKey(String key) {
    algorithm = Algorithm.HMAC256(key.getBytes(StandardCharsets.UTF_8));
  }

  public AuthWorkerSevice(
    UserSaveService userSaveService,
    UserValidationService userValidationService,
    GeometryFactory geometryFactory
  ) {
    this.userSaveService = userSaveService;
    this.userValidationService = userValidationService;
    this.geometryFactory = geometryFactory;
  }

  public static String encodePassword(String password) {
    return bCryptPasswordEncoder.encode(password);
  }

  /**
   * Generates an authentication response for a successful authentication.
   *
   * @param  user  the UserDAO representing the authenticated user
   * @return       an AuthResponse containing the generated authentication token and the UserDTO
   */
  public static AuthResponse successfulAuthentication(UserDAO user) {
    String authToken = JWT
      .create()
      .withClaim("accountID", user.getUserID())
      .withExpiresAt(new Date(System.currentTimeMillis() + TWO_WEEKS_IN_MILLIS))
      .sign(algorithm);
    return new AuthResponse(authToken, new UserDTO(user));
  }

  /**
   * Validates a password to ensure it meets the specified criteria.
   *
   * @param  password  the password to be validated
   * @throws ResponseStatusException  if the password does not meet the criteria
   */
  public static void validatePassword(String password)
    throws ResponseStatusException {
    Matcher hasLetter = letter.matcher(password);
    Matcher hasDigit = digit.matcher(password);
    Matcher hasPasswordCheck = passwordCheck.matcher(password);

    if (!hasLetter.find() || !hasDigit.find() || !hasPasswordCheck.find()) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "The password must contain at least one uppercase letter, one lowercase letter, and one digit"
      );
    }
  }

  /**
   * Attempts to authenticate a user's password.
   *
   * @param  user     the UserDAO object representing the user
   * @param  password the password to be authenticated
   */
  public static void attemptAuthenticationOfPassword(
    UserDAO user,
    String password
  ) {
    if (!bCryptPasswordEncoder.matches(password, user.getHash())) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Invalid login credentials"
      );
    }
  }

  /**
   * Attempts to authenticate a user's password.
   *
   * @param  email    the email of the user
   * @param  password the password to be authenticated
   * @return          the UserDAO object representing the user
   */
  public UserDAO attemptAuthentication(String email, String password) {
    UserDAO user = userValidationService.findUserByEmail(email);
    if (user == null) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Invalid login credentials"
      );
    }
    if (user.getDeletedAt() != null) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "User is deleted"
      );
    }
    attemptAuthenticationOfPassword(user, password);

    return user;
  }

  /**
   * Changes the password for a user.
   *
   * @param  userDAO    the UserDAO object representing the user
   * @param  password   the new password for the user
   * @return            the updated UserDAO object with the new password
   */
  public UserDAO changePasswordForUser(UserDAO userDAO, String password) {
    String hashedPassword = bCryptPasswordEncoder.encode(password);
    userDAO.setHash(hashedPassword);

    userSaveService.saveUser(userDAO);
    return userDAO;
  }

  //TODO Move these magic numbers up
  /**
   * Creates a random offset point from the given latitude and longitude.
   *
   * @param  latitude  the latitude of the base point
   * @param  longitude the longitude of the base point
   * @return           the randomly offset point
   */
  public Point createRandomOffsetPoint(double latitude, double longitude) {
    double offsetLat = Math.max(
      MINIMUM_LATITUDE,
      Math.min(MAXIMUM_LATITUDE, latitude + (random.nextDouble() * 2 - 1) * OFFSET_RANGE)
    );
    double offsetLon = Math.max(
      MINIMUM_LONGITUDE,
      Math.min(MAXIMUM_LONGITUDE, longitude + (random.nextDouble() * 2 - 1) * OFFSET_RANGE)
    );

    return geometryFactory.createPoint(new Coordinate(offsetLat, offsetLon));
  }
}
