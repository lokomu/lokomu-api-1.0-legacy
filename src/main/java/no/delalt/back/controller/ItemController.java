package no.delalt.back.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import no.delalt.back.configuration.validation.ValidID;
import no.delalt.back.model.dto.input.ItemCreationDTO;
import no.delalt.back.model.dto.input.ItemModifyDTO;
import no.delalt.back.model.dto.output.ItemDTO;
import no.delalt.back.model.dto.output.ItemOwnerDTO;
import no.delalt.back.response.dto.ItemOwnerResponse;
import no.delalt.back.response.id.ItemIDResponse;
import no.delalt.back.response.list.ItemListResponse;
import no.delalt.back.service.ItemService;
import no.delalt.back.service.deletion.ItemDeletionService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@Validated
@RequestMapping("/item")
public class ItemController {
  private final ItemService itemService;
  private final ItemDeletionService itemDeletionService;

  public ItemController(
    ItemService itemService,
    ItemDeletionService itemDeletionService
  ) {
    this.itemService = itemService;
    this.itemDeletionService = itemDeletionService;
  }

  /**
   * Creates a new item.
   *
   * @param  itemDTO  the itemDTO object containing the details of the item to be created
   * @return          the response entity containing the item ID of the newly created item
   * @throws ResponseStatusException if an error occurs while creating the item
   */
  @Operation(summary = "Creates a new item")
  @PostMapping(
    path = "/borrow",
    consumes = "application/json",
    produces = "application/json"
  )
  public ResponseEntity<ItemIDResponse> createBorrowItem(
    @RequestBody @Valid ItemCreationDTO itemDTO
  )
    throws ResponseStatusException {
    boolean isGiven = false;
    String itemID = itemService.createItem(itemDTO, isGiven);
    ItemIDResponse itemIDResponse = new ItemIDResponse(itemID);

    return ResponseEntity.ok(itemIDResponse);
  }

  /**
   * Creates a new given out item.
   *
   * @param  itemDTO  the itemDTO object containing the details of the item to be created
   * @return          the response entity containing the item ID of the newly created item
   * @throws ResponseStatusException if an error occurs while creating the item
   */
  @Operation(summary = "Creates a new item")
  @PostMapping(
    path = "/give",
    consumes = "application/json",
    produces = "application/json"
  )
  public ResponseEntity<ItemIDResponse> createGivenItem(
    @RequestBody @Valid ItemCreationDTO itemDTO
  )
    throws ResponseStatusException {
    boolean isGiven = true;
    String itemID = itemService.createItem(itemDTO, isGiven);
    ItemIDResponse itemIDResponse = new ItemIDResponse(itemID);

    return ResponseEntity.ok(itemIDResponse);
  }

  /**
   * Retrieves an item by its ID for the owner.
   *
   * @param  itemID  the ID of the item to retrieve
   * @return         the ResponseEntity containing the item owner response
   * @throws ResponseStatusException if there is an error retrieving the item
   */
  @Operation(summary = "Retrieves an item by its ID for the owner")
  @GetMapping(path = "/{itemID}", produces = "application/json")
  public ResponseEntity<ItemOwnerResponse> getItemDAOByIDForOwner(
    @PathVariable @ValidID @NotBlank String itemID
  )
    throws ResponseStatusException {
    ItemOwnerDTO itemDTO = itemService.getItemByIDForOwner(itemID);
    ItemOwnerResponse itemOwnerResponse = new ItemOwnerResponse(itemDTO);

    return ResponseEntity.ok(itemOwnerResponse);
  }

  /**
   * Retrieves all items for the active user.
   *
   * @return         the ResponseEntity containing the item list response
   * @throws ResponseStatusException if there is an error retrieving the items
   */
  @Operation(summary = "Retrieves all items for the active user")
  @GetMapping(path = "/user", produces = "application/json")
  public ResponseEntity<ItemListResponse> getAllUserItems()
    throws ResponseStatusException {
    List<ItemDTO> itemDTOs = itemService.getAllItemsForActiveUser();
    ItemListResponse itemListResponse = new ItemListResponse(itemDTOs);

    return ResponseEntity.ok(itemListResponse);
  }

  //TODO Fix this, should never delete all community item entities when updating the communities of the item
  /**
   * Change an item.
   *
   * @param  itemID   the ID of the item to be changed
   * @param  itemDTO  the modified item data
   * @return          a response entity indicating the success of the operation
   * @throws ResponseStatusException if there is an error updating the item
   */
  @Operation(summary = "Change an item")
  @PutMapping(path = "/{itemID}", consumes = "application/json")
  public ResponseEntity<Void> changeItem(
    @PathVariable @ValidID @NotBlank String itemID,
    @RequestBody @Valid ItemModifyDTO itemDTO
  )
    throws ResponseStatusException {
    itemService.updateItem(itemID, itemDTO);
    return ResponseEntity.ok().build();
  }

  /**
   * Delete an item.
   *
   * @param  itemID  the ID of the item to be deleted
   * @return         a response entity indicating the success of the operation
   * @throws ResponseStatusException if there is an error deleting the item
   */
  @Operation(summary = "Deletes an item")
  @DeleteMapping(path = "/{itemID}")
  public ResponseEntity<Void> setItemToDeleted(
    @PathVariable @ValidID @NotBlank String itemID
  )
    throws ResponseStatusException {
    itemDeletionService.softDeleteItem(itemID);
    return ResponseEntity.ok().build();
  }
}
