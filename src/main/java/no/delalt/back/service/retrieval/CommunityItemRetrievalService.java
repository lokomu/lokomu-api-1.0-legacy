package no.delalt.back.service.retrieval;

import no.delalt.back.model.dao.CommunityDAO;
import no.delalt.back.model.dao.CommunityItemDAO;
import no.delalt.back.model.dao.ItemDAO;
import no.delalt.back.repository.CommunityItemRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommunityItemRetrievalService {
  private final CommunityItemRepository communityItemRepository;

  public CommunityItemRetrievalService(
    CommunityItemRepository communityItemRepository
  ) {
    this.communityItemRepository = communityItemRepository;
  }

  /**
   * Retrieves all items in a community.
   *
   * @param  communityDAO  the community to retrieve items from
   * @return               a list of item data access objects representing the items in the community
   */
  public List<ItemDAO> getAllItemsInCommunity(CommunityDAO communityDAO) {
    List<CommunityItemDAO> communityItems = getAllCommunityItemForCommunity(
      communityDAO
    );

    if (communityItems == null) {
      return new ArrayList<>();
    }

    return communityItems.stream().map(CommunityItemDAO::getItem).toList();
  }

  /**
   * Retrieves a list of CommunityItemDAO objects for a given CommunityDAO.
   *
   * @param  communityDAO  the CommunityDAO object for which to retrieve CommunityItemDAO objects
   * @return               a list of CommunityItemDAO objects associated with the given CommunityDAO
   */
  public List<CommunityItemDAO> getAllCommunityItemForCommunity(
    CommunityDAO communityDAO
  ) {
    return communityItemRepository.findAllByCommunity(communityDAO);
  }

  /**
   * Retrieves a list of community item IDs based on the provided item ID.
   *
   * @param  item  the item for which to retrieve community item IDs
   * @return       a list of community item IDs associated with the item
   */
  public List<String> getCommunityItemIDsByItemID(ItemDAO item) {
    return communityItemRepository
      .findAllByItem(item)
      .stream()
      .map(communityItem -> communityItem.getCommunity().getCommunityID())
      .toList();
  }

  /**
   * Retrieves a list of CommunityItemDAO objects for a given ItemDAO.
   *
   * @param  itemDAO  the ItemDAO object for which to retrieve CommunityItemDAO objects
   * @return          a list of CommunityItemDAO objects associated with the given ItemDAO
   */
  public List<CommunityItemDAO> getAllCommunityItemForItem(ItemDAO itemDAO) {
    return communityItemRepository.findAllByItem(itemDAO);
  }
}
