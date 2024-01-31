package no.delalt.back.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import no.delalt.back.model.dto.input.PasswordDTO;
import no.delalt.back.model.dto.input.UserModifyDTO;
import no.delalt.back.model.dto.output.UserDTO;
import no.delalt.back.response.AuthResponse;
import no.delalt.back.response.dto.UserResponse;
import no.delalt.back.service.UserService;
import no.delalt.back.service.deletion.UserDeletionService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Validated
@RequestMapping("/user")
public class UserController {
  private final UserService userService;
  private final UserDeletionService userDeletionService;

  public UserController(
    UserService userService,
    UserDeletionService userDeletionService
  ) {
    this.userService = userService;
    this.userDeletionService = userDeletionService;
  }

  /**
   * Retrieves the current user.
   *
   * @return the ResponseEntity containing the UserResponse
   */
  @Operation(summary = "Retrieves a user from the given userID")
  @GetMapping(path = "/", produces = "application/json")
  public ResponseEntity<UserResponse> getAUser()
    throws ResponseStatusException {
    UserDTO userDTO = userService.retrieveCurrentUserDAO();
    UserResponse userResponse = new UserResponse(userDTO);

    return ResponseEntity.ok(userResponse);
  }

  /**
   * Changes the profile of a user.
   *
   * @param userDTO the DTO containing the new profile
   * @return the ResponseEntity
   */
  @Operation(summary = "Changes the profile of a user")
  @PutMapping(path = "/", consumes = "application/json")
  public ResponseEntity<Void> changeProfileOfUser(
    @RequestBody @Valid UserModifyDTO userDTO
  )
    throws ResponseStatusException {
    userService.updateUserProfile(userDTO);
    return ResponseEntity.ok().build();
  }

  /**
   * Change the password of a user.
   *
   * @param  passwordDTO  the PasswordDTO object containing the new password
   * @return              the ResponseEntity containing the AuthResponse object
   */
  @Operation(summary = "Change the password of a user")
  @PatchMapping(
    path = "/password",
    consumes = "application/json",
    produces = "application/json"
  )
  public ResponseEntity<AuthResponse> changePasswordOfUser(
    @RequestBody @Valid PasswordDTO passwordDTO
  )
    throws ResponseStatusException {
    AuthResponse authResponse = userService.changeUserPassword(passwordDTO);
    return ResponseEntity.ok(authResponse);
  }

  //TODO Delete all rating data, chat data and borrowal request data, including the user itself fully
  /**
   * Deletes the user and associated data.
   *
   * @return         an HTTP response indicating the success of the deletion
   * @throws ResponseStatusException  if there is an error during deletion
   */
  @Operation(summary = "Deletes the user and associated data")
  @DeleteMapping(path = "/")
  public ResponseEntity<Void> deleteAccount() throws ResponseStatusException {
    userDeletionService.deleteUserAndAssociatedData();
    return ResponseEntity.ok().build();
  }
}
