package no.delalt.back.service.validation;

import no.delalt.back.model.dao.ItemDAO;
import no.delalt.back.repository.ItemRepository;
import no.delalt.back.util.SecurityUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
public class ItemValidationService {
  private final ItemRepository itemRepository;

  public ItemValidationService(ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
  }

  /**
   * Validates whether the current user owns the item.
   *
   * @param  itemDAO  the ItemDAO object representing the item
   * @throws ResponseStatusException  if the user does not own the item
   */
  public void validateUserOwnsItem(ItemDAO itemDAO)
    throws ResponseStatusException {
    if (
      !Objects.equals(
        SecurityUtil.getAuthenticatedAccountID(),
        itemDAO.getUser().getUserID()
      )
    ) {
      throw new ResponseStatusException(
        HttpStatus.FORBIDDEN,
        "You do not have permission"
      );
    }
  }

  /**
   * Validates if an item with the given itemID exists and returns it.
   *
   * @param  itemID  the ID of the item to validate
   * @return         the ItemDAO object representing the validated item
   * @throws ResponseStatusException if the item is not found
   */
  public ItemDAO validateItemExistsAndReturn(String itemID)
    throws ResponseStatusException {
    return itemRepository
      .findById(itemID)
      .orElseThrow(
        () ->
          new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found")
      );
  }
}
