package no.delalt.back;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
  info = @Info(title = "Del - API"),
  security = @SecurityRequirement(name = "Account Token")
)
@SecurityScheme(
  name = "Account Token",
  scheme = "Bearer",
  type = SecuritySchemeType.HTTP,
  in = SecuritySchemeIn.HEADER
)
public class DelApplication {

  /**
   * Runs the main method of the Java application.
   *
   * @param  args  the command line arguments passed to the application
   */
  public static void main(String[] args) {
    SpringApplication.run(DelApplication.class, args);
  }
}
