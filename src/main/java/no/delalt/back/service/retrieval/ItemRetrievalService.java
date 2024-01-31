package no.delalt.back.service.retrieval;

import no.delalt.back.model.dao.ItemDAO;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemRetrievalService {
  private final ItemRepository itemRepository;

  public ItemRetrievalService(ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
  }

  /**
   * Retrieves all items belonging to a specific user.
   *
   * @param  userDAO  the user for which to retrieve the items
   * @return          a list of ItemDAO objects representing the items belonging to the user
   */
  public List<ItemDAO> getAllOfUsersItems(UserDAO userDAO) {
    return itemRepository.findAllByUserAndIsDeletedIsFalse(userDAO);
  }
}
