package no.delalt.back.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;
import no.delalt.back.configuration.validation.ValidID;
import no.delalt.back.model.dto.output.InviteDTO;
import no.delalt.back.response.dto.InviteResponse;
import no.delalt.back.response.id.CommunityIDResponse;
import no.delalt.back.service.InviteService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Validated
@RequestMapping("/invite")
public class InviteController {
  private final InviteService inviteService;

  public InviteController(InviteService inviteService) {
    this.inviteService = inviteService;
  }

  /**
   * Creates an invitation for a given community ID.
   *
   * @param  communityID  the ID of the community
   * @return              the response entity containing the invite response
   * @throws ResponseStatusException if an error occurs while creating the invite
   */
  @Operation(summary = "Creates an invitation for a given community ID")
  @PostMapping(path = "/{communityID}", produces = "application/json")
  public ResponseEntity<InviteResponse> createInvite(
    @PathVariable @ValidID @NotBlank String communityID
  )
    throws ResponseStatusException {
    InviteDTO inviteDTO = inviteService.handleInviteCreation(communityID);
    InviteResponse inviteResponse = new InviteResponse(inviteDTO);

    return ResponseEntity.ok(inviteResponse);
  }

  /**
   * Accepts an invitation for a given invite ID.
   *
   * @param  inviteID  the ID of the invite
   * @return           the response entity containing the community ID response
   * @throws ResponseStatusException if an error occurs while accepting the invite
   */
  @Operation(summary = "Accepts an invitation for a given invite ID")
  @PostMapping(path = "/accept/{inviteID}", produces = "application/json")
  public ResponseEntity<CommunityIDResponse> acceptInvite(
    @PathVariable @ValidID @NotBlank String inviteID
  )
    throws ResponseStatusException {
    String communityID = inviteService.handleInviteAcceptance(inviteID);
    CommunityIDResponse communityIDResponse = new CommunityIDResponse(
      communityID
    );

    return ResponseEntity.ok(communityIDResponse);
  }
}
