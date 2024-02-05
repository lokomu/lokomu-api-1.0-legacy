package no.delalt.back.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;
import no.delalt.back.configuration.validation.ValidID;
import no.delalt.back.model.dto.output.ItemDTO;
import no.delalt.back.response.list.ItemListResponse;
import no.delalt.back.service.CommunityItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@Validated
@RequestMapping("/community/item")
public class CommunityItemController {
  private final CommunityItemService communityItemService;

  public CommunityItemController(CommunityItemService communityItemService) {
    this.communityItemService = communityItemService;
  }

  /**
   * Retrieves a list of other user community items.
   *
   * @return         	Response entity containing a list of item DTOs
   * @throws ResponseStatusException	if an error occurs while retrieving the items
   */
  @Operation(summary = "Retrieves a list of other user community items")
  @GetMapping(path = "/others", produces = "application/json")
  public ResponseEntity<ItemListResponse> getOtherUserCommunityItems()
    throws ResponseStatusException {
    List<ItemDTO> otherUserCommunityItems = communityItemService.getOtherUserItemsFromUserCommunities();
    ItemListResponse itemListResponse = new ItemListResponse(
      otherUserCommunityItems
    );

    return ResponseEntity.ok(itemListResponse);
  }

  //TODO Fix the retrieval of items from non-members
  /**
   * Retrieves all items in a community.
   *
   * @param  communityID  the ID of the community
   * @return              the response entity containing the item list response
   * @throws ResponseStatusException  if an error occurs while retrieving the items
   */
  @Operation(summary = "Retrieves all items in a community")
  @GetMapping(path = "/{communityID}", produces = "application/json")
  public ResponseEntity<ItemListResponse> getAllItemsInACommunity(
    @PathVariable @ValidID @NotBlank String communityID
  )
    throws ResponseStatusException {
    List<ItemDTO> items = communityItemService.handleItemsInCommunity(
      communityID
    );
    ItemListResponse itemListResponse = new ItemListResponse(items);

    return ResponseEntity.ok(itemListResponse);
  }
}
