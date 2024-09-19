package no.delalt.back.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import no.delalt.back.model.dto.input.LoginDTO;
import no.delalt.back.model.dto.input.RegisterUserDTO;
import no.delalt.back.response.AuthResponse;
import no.delalt.back.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  //TODO Not a secure method of sending a token, better implement HTTP-only cookie
  /**
   * Logs in a user with the provided credentials.
   *
   * @param  loginDTO  the LoginDTO object containing the user's credentials
   * @return           the ResponseEntity containing the AuthResponse object
   * @throws ResponseStatusException if there is an error during the login process
   */
  @Operation(summary = "Logs in a user with the provided credentials")
  @PostMapping(
    path = "/login",
    consumes = "application/json",
    produces = "application/json"
  )
  public ResponseEntity<AuthResponse> login(
    @RequestBody @Valid LoginDTO loginDTO
  )
    throws ResponseStatusException {
    AuthResponse authResponse = authService.logInUser(loginDTO);

    return ResponseEntity.ok(authResponse);
  }

  /**
   * Registers a new user account.
   *
   * @param  regInfo  the user information to register
   * @return          an HTTP response indicating success or failure
   * @throws ResponseStatusException if there is an error registering the user
   */
  @Operation(summary = "Registers a new user account")
  @PostMapping(path = "/signup", consumes = "application/json")
  public ResponseEntity<Void> registerNewUserAccount(
    @RequestBody @Valid RegisterUserDTO regInfo
  )
    throws ResponseStatusException {
    authService.registerNewUser(regInfo);

    return ResponseEntity.ok().build();
  }
}
