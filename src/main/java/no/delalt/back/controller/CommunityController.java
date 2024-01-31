package no.delalt.back.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import no.delalt.back.configuration.validation.ValidID;
import no.delalt.back.model.dto.input.CommunityCreationDTO;
import no.delalt.back.model.dto.output.CommunityDTO;
import no.delalt.back.model.dto.output.UserDTO;
import no.delalt.back.response.dto.CommunityResponse;
import no.delalt.back.response.id.CommunityIDResponse;
import no.delalt.back.response.list.CommunityListResponse;
import no.delalt.back.response.list.UserListResponse;
import no.delalt.back.service.CommunityService;
import no.delalt.back.service.deletion.CommunityDeletionService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@Validated
@RequestMapping("/community")
public class CommunityController {
  private final CommunityService communityService;
  private final CommunityDeletionService communityDeletionService;

  public CommunityController(
    CommunityService communityService,
    CommunityDeletionService communityDeletionService
  ) {
    this.communityService = communityService;
    this.communityDeletionService = communityDeletionService;
  }

  /**
   * Creates a new community.
   *
   * @param  communityDTO  the DTO containing the community details
   * @return               the response entity containing the created community ID
   * @throws ResponseStatusException  if an error occurs while creating the community
   */
  @Operation(summary = "Creates a new community")
  @PostMapping(
    path = "/",
    consumes = "application/json",
    produces = "application/json"
  )
  public ResponseEntity<CommunityIDResponse> addCommunity(
    @RequestBody @Valid CommunityCreationDTO communityDTO
  )
    throws ResponseStatusException {
    String communityID = communityService.handleAddCommunity(communityDTO);
    CommunityIDResponse communityIDResponse = new CommunityIDResponse(
      communityID
    );

    return ResponseEntity.ok(communityIDResponse);
  }

  /**
   * Retrieves the details of a community.
   *
   * @param  communityID  the ID of the community
   * @return              the ResponseEntity containing the community details
   * @throws ResponseStatusException  if an error occurs while retrieving the community
   */
  @Operation(summary = "Retrieves the details of a community")
  @GetMapping(path = "/{communityID}", produces = "application/json")
  public ResponseEntity<CommunityResponse> getCommunity(
    @PathVariable @ValidID @NotBlank String communityID
  )
    throws ResponseStatusException {
    CommunityDTO communityDTO = communityService.retrieveCommunityForMember(
      communityID
    );
    CommunityResponse communityResponse = new CommunityResponse(communityDTO);

    return ResponseEntity.ok(communityResponse);
  }

  /**
   * Retrieves a list of nearby communities based on the user's location.
   *
   * @param  distance  the maximum distance in meters from the user's location (default: 3000)
   * @return           the ResponseEntity containing the list of nearby communities
   * @throws ResponseStatusException  if there is an error handling the request
   */
  @Operation(
    summary = "Retrieves a list of nearby communities based on the user's location"
  )
  @GetMapping(path = "/near", produces = "application/json")
  public ResponseEntity<CommunityListResponse> showAllNearbyCommunitiesByUser(
    @RequestParam(value = "distance", defaultValue = "3000") @Min(300) @Max(
      5000
    ) int distance
  )
    throws ResponseStatusException {
    List<CommunityDTO> communityDTOs = communityService.handleNearbyCommunitiesForUser(
      distance
    );
    CommunityListResponse communityListResponse = new CommunityListResponse(
      communityDTOs
    );

    return ResponseEntity.ok(communityListResponse);
  }

  //TODO Move to user community
  /**
   * Retrieves the list of members in a community.
   *
   * @param  communityID  the ID of the community
   * @return              the response entity containing the list of users
   * @throws ResponseStatusException if an error occurs while retrieving the members
   */
  @Operation(summary = "Retrieves the list of members in a community")
  @GetMapping(path = "/{communityID}/members", produces = "application/json")
  public ResponseEntity<UserListResponse> getMembersInCommunity(
    @PathVariable @ValidID @NotBlank String communityID
  )
    throws ResponseStatusException {
    List<UserDTO> membersList = communityService.getMembersForCommunity(
      communityID
    );
    //TODO Doesnt check if the user is a member
    UserListResponse userListResponse = new UserListResponse(membersList);

    return ResponseEntity.ok(userListResponse);
  }

  /**
   * Updates a community with the provided community ID.
   *
   * @param  communityID   the ID of the community to be updated
   * @param  communityDTO  the new details of the community
   * @return               the response entity containing the updated community ID
   * @throws ResponseStatusException if the community ID is not valid or if an error occurs during the update process
   */
  @Operation(summary = "Updates a community with the provided community ID")
  @PutMapping(
    path = "/{communityID}",
    consumes = "application/json",
    produces = "application/json"
  )
  public ResponseEntity<CommunityIDResponse> changeCommunity(
    @PathVariable @ValidID @NotBlank String communityID,
    @RequestBody @Valid CommunityCreationDTO communityDTO
  )
    throws ResponseStatusException {
    String sameCommunityID = communityService.updateCommunity(
      communityID,
      communityDTO
    );
    CommunityIDResponse communityIDResponse = new CommunityIDResponse(
      sameCommunityID
    );

    return ResponseEntity.ok(communityIDResponse);
  }

  /**
   * Removes a community.
   *
   * @param  communityID  the ID of the community to be removed
   * @return              a ResponseEntity representing the response status
   */
  @Operation(summary = "Removes a community")
  @DeleteMapping(path = "/{communityID}")
  public ResponseEntity<Void> removeCommunity(
    @PathVariable @ValidID @NotBlank String communityID
  )
    throws ResponseStatusException {
    communityDeletionService.handleRemoveCommunity(communityID);
    return ResponseEntity.ok().build();
  }
}
