package no.delalt.back.service.deletion;

import no.delalt.back.model.dao.ItemDAO;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.service.retrieval.ItemRetrievalService;
import no.delalt.back.service.save.ItemSaveService;
import no.delalt.back.service.validation.ItemValidationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ItemDeletionService {
  private final ItemValidationService itemValidationService;
  private final ItemSaveService itemSaveService;
  private final ItemRetrievalService itemRetrievalService;
  private final CommunityItemDeletionService communityItemDeletionService;

  public ItemDeletionService(
    ItemValidationService itemValidationService,
    ItemSaveService itemSaveService,
    ItemRetrievalService itemRetrievalService,
    CommunityItemDeletionService communityItemDeletionService
  ) {
    this.itemValidationService = itemValidationService;
    this.itemSaveService = itemSaveService;
    this.itemRetrievalService = itemRetrievalService;
    this.communityItemDeletionService = communityItemDeletionService;
  }

  //TODO Decide what has to be done with borrows and requests when an item is deleted
  /**
   * Deletes an item by marking it as deleted in the database.
   *
   * @param  itemID  the ID of the item to be deleted
   */
  @Transactional
  public void softDeleteItem(String itemID) {
    ItemDAO item = itemValidationService.validateItemExistsAndReturn(itemID);

    itemValidationService.validateUserOwnsItem(item);

    communityItemDeletionService.deleteAllWithItem(item);

    item.setIsDeleted(true);

    itemSaveService.saveItem(item);
  }

  /**
   * Deletes all items for a given user.
   *
   * @param  userDAO  the UserDAO object representing the user
   */
  public void deleteItemsForUser(UserDAO userDAO) {
    List<ItemDAO> items = itemRetrievalService.getAllOfUsersItems(userDAO);
    for (ItemDAO item : items) {
      communityItemDeletionService.deleteAllWithItem(item);

      item.setImage(null);
      item.setIsDeleted(true);
    }
    itemSaveService.saveAll(items);
  }
}
