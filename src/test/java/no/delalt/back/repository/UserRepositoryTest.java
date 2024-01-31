package no.delalt.back.repository;

import no.delalt.back.AbstractContainerBaseTest;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.util.NanoIdGenerator;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
public class UserRepositoryTest extends AbstractContainerBaseTest {
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private GeometryFactory geometryFactory;

  @DynamicPropertySource
  static void databaseProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgisContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgisContainer::getUsername);
    registry.add("spring.datasource.password", postgisContainer::getPassword);
  }

  @Test
  public void whenCreateUser_thenUserIsFound() {
    // Arrange
    UserDAO newUser = new UserDAO();
    newUser.setUserID(NanoIdGenerator.generateNanoID());
    newUser.setEmail("test@test.com");
    newUser.setFirstName("Test");
    newUser.setLastName("Lest");
    newUser.setHash("123==");
    Point point = geometryFactory.createPoint(new Coordinate(90, 90));
    newUser.setCoordinates(point);
    newUser.setImage(null);
    LocalDate localDate = LocalDate.of(2023, 11, 1);
    newUser.setLastLocationUpdate(localDate);

    userRepository.save(newUser);

    UserDAO foundUser = userRepository
      .findById(newUser.getUserID())
      .orElse(null);

    assertNotNull(foundUser);
    assertEquals(newUser.getFirstName(), foundUser.getFirstName());
    assertEquals(newUser.getEmail(), foundUser.getEmail());
  }
}
