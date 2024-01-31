package no.delalt.back.service.save;

import no.delalt.back.model.dao.ItemDAO;
import no.delalt.back.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemSaveService {
  private final ItemRepository itemRepository;

  public ItemSaveService(ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
  }

  /**
   * Saves the given `ItemDAO` object to the database.
   *
   * @param  itemDAO  the `ItemDAO` object to be saved
   */
  public void saveItem(ItemDAO itemDAO) {
    itemRepository.save(itemDAO);
  }

  /**
   * Saves a list of `ItemDAO` objects to the database.
   *
   * @param  list  the list of `ItemDAO` objects to be saved
   */
  public void saveAll(List<ItemDAO> list) {
    itemRepository.saveAll(list);
  }
}
