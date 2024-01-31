package no.delalt.back.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import no.delalt.back.configuration.validation.ValidID;
import no.delalt.back.model.dto.input.CommunityRequestCreationDTO;
import no.delalt.back.model.dto.output.CommunityRequestDTO;
import no.delalt.back.model.dto.output.UserDTO;
import no.delalt.back.response.dto.CommunityRequestResponse;
import no.delalt.back.response.list.UserListResponse;
import no.delalt.back.service.CommunityRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@Validated
@RequestMapping("/community/request")
public class CommunityRequestController {
  private final CommunityRequestService communityRequestService;

  public CommunityRequestController(
    CommunityRequestService communityRequestService
  ) {
    this.communityRequestService = communityRequestService;
  }

  /**
   * Joins a private community.
   *
   * @param  communityID      the ID of the community to join
   * @param  creationDTO      the DTO containing the community request creation data
   * @throws ResponseStatusException if an error occurs while joining the community
   * @return                   the response entity with a void body
   */
  @Operation(summary = "Joins a private community")
  @PostMapping(path = "/{communityID}", consumes = "application/json")
  public ResponseEntity<Void> joinPrivateCommunity(
    @PathVariable @ValidID @NotBlank String communityID,
    @RequestBody @Valid CommunityRequestCreationDTO creationDTO
  )
    throws ResponseStatusException {
    communityRequestService.requestToJoinPrivateCommunity(
      communityID,
      creationDTO
    );
    return ResponseEntity.ok().build();
  }

  /**
   * Accepts a community join request for a given community ID and user ID.
   *
   * @param  communityID  the ID of the community
   * @param  userID       the ID of the user
   * @return              a ResponseEntity with no body
   * @throws ResponseStatusException  if an error occurs while processing the request
   */
  @Operation(
    summary = "Accepts a community join request for a given community ID and user ID"
  )
  @PostMapping(path = "/{communityID}/{userID}/accept")
  public ResponseEntity<Void> acceptCommunityRequest(
    @PathVariable @ValidID @NotBlank String communityID,
    @PathVariable @ValidID @NotBlank String userID
  )
    throws ResponseStatusException {
    communityRequestService.processCommunityJoinRequest(communityID, userID);
    return ResponseEntity.ok().build();
  }

  /**
   * Rejects a community join request for a given community ID and user ID.
   *
   * @param  communityID  the ID of the community
   * @param  userID       the ID of the user
   * @return              a ResponseEntity with no body
   * @throws ResponseStatusException  if an error occurs while processing the request
   */
  @Operation(
    summary = "Rejects a community join request for a given community ID and user ID"
  )
  @DeleteMapping(path = "/{communityID}/{userID}")
  public ResponseEntity<Void> rejectCommunityRequest(
    @PathVariable @ValidID @NotBlank String communityID,
    @PathVariable @ValidID @NotBlank String userID
  )
    throws ResponseStatusException {
    communityRequestService.rejectRequestByUserAndCommunityID(
      userID,
      communityID
    );
    return ResponseEntity.ok().build();
  }

  /**
   * Retrieves the list of requests for a specific community.
   *
   * @param  communityID  the ID of the community
   * @return              the response entity containing the user list response
   * @throws ResponseStatusException if there is an error retrieving the requests
   */
  @Operation(
    summary = "Retrieves the list of requests for a specific community"
  )
  @GetMapping(path = "/{communityID}", produces = "application/json")
  public ResponseEntity<UserListResponse> getRequests(
    @PathVariable @ValidID @NotBlank String communityID
  )
    throws ResponseStatusException {
    List<UserDTO> userDTOs = communityRequestService.getRequestsByCommunityID(
      communityID
    );
    UserListResponse userListResponse = new UserListResponse(userDTOs);

    return ResponseEntity.ok(userListResponse);
  }

  /**
   * Retrieves the request for a specific community and user.
   *
   * @param  communityID  the ID of the community
   * @param  userID       the ID of the user
   * @return              the response entity containing the community request response
   * @throws ResponseStatusException if there is an error retrieving the request
   */
  @Operation(
    summary = "Retrieves the request for a specific community and user"
  )
  @GetMapping(path = "/{communityID}/{userID}", produces = "application/json")
  public ResponseEntity<CommunityRequestResponse> findRequestByIDs(
    @PathVariable @ValidID @NotBlank String communityID,
    @PathVariable @ValidID @NotBlank String userID
  )
    throws ResponseStatusException {
    CommunityRequestDTO communityRequestDTO = communityRequestService.findRequestByUserAndCommunityID(
      userID,
      communityID
    );
    CommunityRequestResponse communityRequestResponse = new CommunityRequestResponse(
      communityRequestDTO
    );

    return ResponseEntity.ok(communityRequestResponse);
  }

  /**
   * Deletes the user's own community request for the given community ID.
   *
   * @param  communityID  the ID of the community
   * @return              a ResponseEntity object representing the HTTP response
   * @throws ResponseStatusException if an error occurs while removing the community request
   */
  @Operation(
    summary = "Deletes the user's own community request for the given community ID"
  )
  @DeleteMapping(path = "/{communityID}/self")
  public ResponseEntity<Void> removeCommunityRequest(
    @PathVariable @ValidID @NotBlank String communityID
  )
    throws ResponseStatusException {
    communityRequestService.removeOwnRequest(communityID);
    return ResponseEntity.ok().build();
  }
}
