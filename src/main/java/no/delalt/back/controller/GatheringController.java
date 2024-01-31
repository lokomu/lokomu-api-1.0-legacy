package no.delalt.back.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import no.delalt.back.configuration.validation.ValidID;
import no.delalt.back.model.dto.input.GatheringCreationDTO;
import no.delalt.back.model.dto.output.GatheringDTO;
import no.delalt.back.response.integer.GatheringAttendeesResponse;
import no.delalt.back.response.list.GatheringListResponse;
import no.delalt.back.service.GatheringService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@Validated
@RequestMapping("/gathering")
public class GatheringController {
  private final GatheringService gatheringService;

  public GatheringController(GatheringService gatheringService) {
    this.gatheringService = gatheringService;
  }

  /**
   * Retrieve a list of upcoming gatherings for a community.
   *
   * @param  communityID            The ID of the community
   * @param  gatheringCreationDTO   The DTO for creating a gathering
   * @return                        A ResponseEntity representing the response status
   * @throws ResponseStatusException If there is an error in the response status
   */
  @Operation(summary = "Retrieve a list of upcoming gatherings for a community")
  @PostMapping(path = "/community/{communityID}", produces = "application/json")
  public ResponseEntity<Void> createGathering(
    @PathVariable @ValidID @NotBlank String communityID,
    @RequestBody @Valid GatheringCreationDTO gatheringCreationDTO
  )
    throws ResponseStatusException {
    gatheringService.createGathering(communityID, gatheringCreationDTO);
    return ResponseEntity.ok().build();
  }

  /**
   * Mark as attending the community gathering.
   *
   * @param  gatheringID            The ID of the gathering
   * @return                        A ResponseEntity representing the response status
   * @throws ResponseStatusException If there is an error in the response status
   */
  @Operation(summary = "Mark as attending the community gathering")
  @PostMapping(path = "/{gatheringID}")
  public ResponseEntity<Void> attendGathering(
    @PathVariable @NotBlank @ValidID String gatheringID
  ) {
    gatheringService.attendGathering(gatheringID);
    return ResponseEntity.ok().build();
  }

  /**
   * Retrieve a list of upcoming gatherings for a community.
   *
   * @param  communityID            The ID of the community
   * @return                        A ResponseEntity representing the response status
   * @throws ResponseStatusException If there is an error in the response status
   */
  @Operation(summary = "Retrieve a list of upcoming gatherings for a community")
  @GetMapping(
    path = "/upcoming/community/{communityID}",
    produces = "application/json"
  )
  public ResponseEntity<GatheringListResponse> getUpcomingGatherings(
    @PathVariable @ValidID @NotBlank String communityID
  )
    throws ResponseStatusException {
    List<GatheringDTO> gatheringDTOs = gatheringService.retrieveUpcomingGatherings(
      communityID
    );
    GatheringListResponse gatheringListResponse = new GatheringListResponse(
      gatheringDTOs
    );
    return ResponseEntity.ok(gatheringListResponse);
  }

  /**
   * Retrieve the amount of attendees for a gathering.
   *
   * @param  gatheringID            The ID of the gathering
   * @return                        A ResponseEntity representing the response status
   * @throws ResponseStatusException If there is an error in the response status
   */
  @Operation(summary = "Retrieve the amount of attendees for a gathering")
  @GetMapping(path = "/{gatheringID}/attendees", produces = "application/json")
  public ResponseEntity<GatheringAttendeesResponse> getNumberOfAttendees(
    @PathVariable @ValidID @NotBlank String gatheringID
  )
    throws ResponseStatusException {
    int numberOfAttendees = gatheringService.getNumberOfAttendees(gatheringID);

    GatheringAttendeesResponse gatheringAttendeesResponse = new GatheringAttendeesResponse(
      numberOfAttendees
    );
    return ResponseEntity.ok(gatheringAttendeesResponse);
  }
}
