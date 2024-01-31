package no.delalt.back;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class AbstractContainerBaseTest {
  static DockerImageName myImage = DockerImageName
    .parse("postgis/postgis:16-3.4")
    .asCompatibleSubstituteFor("postgres");
  public static final PostgreSQLContainer<?> postgisContainer = new PostgreSQLContainer<>(
    myImage
  );

  static {
    postgisContainer.start();
  }
}
