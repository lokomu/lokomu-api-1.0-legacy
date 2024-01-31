package no.delalt.back.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;
import no.delalt.back.configuration.validation.ValidID;
import no.delalt.back.model.dto.output.CommunityDTO;
import no.delalt.back.response.bool.UserInCommunityBooleanResponse;
import no.delalt.back.response.id.UserIDResponse;
import no.delalt.back.response.list.CommunityIDListResponse;
import no.delalt.back.response.list.CommunityListResponse;
import no.delalt.back.service.UserCommunityService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@Validated
@RequestMapping("/community/user")
public class UserCommunityController {
  private final UserCommunityService userCommunityService;

  public UserCommunityController(UserCommunityService userCommunityService) {
    this.userCommunityService = userCommunityService;
  }

  /**
   * Adds a user to a community.
   *
   * @param  communityID  the ID of the community
   * @return              a ResponseEntity object representing the response status
   * @throws ResponseStatusException if an error occurs while adding the user to the community
   */
  @Operation(summary = "Adds a user to a community")
  @PostMapping(path = "/{communityID}")
  public ResponseEntity<Void> addUserToCommunity(
    @PathVariable @ValidID @NotBlank String communityID
  )
    throws ResponseStatusException {
    userCommunityService.addUserToOpenCommunity(communityID);
    return ResponseEntity.ok().build();
  }

  /**
   * Retrieves a list of communities for the current user.
   *
   * @return         	Response entity containing a list of CommunityDTOs
   * @throws ResponseStatusException   If an error occurs while retrieving the communities
   */
  @Operation(summary = "Retrieves a list of communities for the current user")
  @GetMapping(path = "/communities", produces = "application/json")
  public ResponseEntity<CommunityListResponse> getCommunitiesForUser()
    throws ResponseStatusException {
    List<CommunityDTO> communityDTOs = userCommunityService.getCommunitiesForCurrentUser();
    CommunityListResponse communityListResponse = new CommunityListResponse(
      communityDTOs
    );

    return ResponseEntity.ok(communityListResponse);
  }

  /**
   * Retrieves a list of community IDs for the current user where they are an admin.
   *
   * @return         	Response entity containing a list of CommunityDTOs
   * @throws ResponseStatusException   If an error occurs while retrieving the communities
   */
  @Operation(
    summary = "Retrieves a list of community IDs for the current user where they are an admin"
  )
  @GetMapping(path = "/admin/communities", produces = "application/json")
  public ResponseEntity<CommunityIDListResponse> getAdminCommunities()
    throws ResponseStatusException {
    List<String> listOfCommunities = userCommunityService.getAdminCommunitiesForCurrentUser();
    CommunityIDListResponse communityIDListResponse = new CommunityIDListResponse(
      listOfCommunities
    );

    return ResponseEntity.ok(communityIDListResponse);
  }

  /**
   * Checks if the current user is in the specified community.
   *
   * @param  communityID  the ID of the community
   * @return              a ResponseEntity object indicating whether the user is in the community
   * @throws ResponseStatusException if there is an error checking if the user is in the community
   */
  @Operation(
    summary = "Checks if the current user is in the specified community"
  )
  @GetMapping(path = "/{communityID}/status", produces = "application/json")
  public ResponseEntity<UserInCommunityBooleanResponse> checkIfUserIsInCommunity(
    @PathVariable @ValidID @NotBlank String communityID
  )
    throws ResponseStatusException {
    boolean isUserInCommunity = userCommunityService.isCurrentUserInCommunity(
      communityID
    );
    UserInCommunityBooleanResponse userInCommunityBooleanResponse = new UserInCommunityBooleanResponse(
      isUserInCommunity
    );

    return ResponseEntity.ok(userInCommunityBooleanResponse);
  }

  /**
   * Retrieves the admin user ID of the specified community.
   *
   * @param  communityID  the ID of the community
   * @return              a ResponseEntity object containing the admin user ID
   * @throws ResponseStatusException if there is an error retrieving the admin user ID
   */
  @Operation(summary = "Retrieves the admin user ID of the specified community")
  @GetMapping(path = "/{communityID}/admin", produces = "application/json")
  public ResponseEntity<UserIDResponse> getUserAdminOfCommunity(
    @PathVariable @ValidID @NotBlank String communityID
  )
    throws ResponseStatusException {
    String userID = userCommunityService.getAdminUserIDOfCommunity(communityID);
    UserIDResponse userIDResponse = new UserIDResponse(userID);

    return ResponseEntity.ok(userIDResponse);
  }

  /**
   * Removes a user from a community.
   *
   * @param  communityID  the ID of the community
   * @param  userID       the ID of the user
   * @return              a ResponseEntity object indicating the success of the operation
   * @throws ResponseStatusException if there is an error removing the user from the community
   */
  @Operation(summary = "Removes a user from a community")
  @PatchMapping(path = "/{communityID}/kick/{userID}")
  public ResponseEntity<Void> kickUserFromCommunity(
    @PathVariable @ValidID @NotBlank String communityID,
    @PathVariable @ValidID @NotBlank String userID
  )
    throws ResponseStatusException {
    userCommunityService.kickUserFromCommunity(communityID, userID);
    return ResponseEntity.ok().build();
  }

  /**
   * Leaves a community.
   *
   * @param  communityID  the ID of the community
   * @return              a ResponseEntity object indicating the success of the operation
   * @throws ResponseStatusException if there is an error leaving the community
   */
  @Operation(summary = "Leaves a community")
  @PatchMapping(path = "/{communityID}/leave")
  public ResponseEntity<Void> leaveCommunity(
    @PathVariable @ValidID @NotBlank String communityID
  )
    throws ResponseStatusException {
    userCommunityService.leaveCommunity(communityID);
    return ResponseEntity.ok().build();
  }
}
