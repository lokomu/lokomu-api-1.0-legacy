package no.delalt.back;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.annotation.PostConstruct;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.model.dto.output.UserDTO;
import no.delalt.back.response.AuthResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class Authentication {
  //TODO Use @ConfigurationProperties instead
  @Value("${not.secret.key}")
  private String secretKey;

  private Algorithm algorithm;

  @PostConstruct
  public void init() {
    algorithm = Algorithm.HMAC256(secretKey.getBytes(StandardCharsets.UTF_8));
  }

  public AuthResponse authenticate(UserDAO user) {
    String authToken = JWT
      .create()
      .withClaim("accountID", user.getUserID())
      .withExpiresAt(
        new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 14)
      )
      .sign(algorithm);
    return new AuthResponse(authToken, new UserDTO(user));
  }
}
