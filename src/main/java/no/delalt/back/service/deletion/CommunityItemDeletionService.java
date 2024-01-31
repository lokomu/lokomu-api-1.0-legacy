package no.delalt.back.service.deletion;

import no.delalt.back.model.dao.CommunityItemDAO;
import no.delalt.back.model.dao.ItemDAO;
import no.delalt.back.model.dao.UserCommunityDAO;
import no.delalt.back.repository.CommunityItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunityItemDeletionService {
  private final CommunityItemRepository communityItemRepository;

  public CommunityItemDeletionService(
    CommunityItemRepository communityItemRepository
  ) {
    this.communityItemRepository = communityItemRepository;
  }

  /**
   * Removes all items of a user from a community.
   *
   * @param  ucd  the UserCommunityDAO object representing the user and the community
   */
  public void removeUserItemsFromCommunity(UserCommunityDAO ucd) {
    List<CommunityItemDAO> allItemsOfUserInCommunity = communityItemRepository.findAllByCommunityAndItem_User(
      ucd.getCommunity(),
      ucd.getUser()
    );
    communityItemRepository.deleteAll(allItemsOfUserInCommunity);
  }

  /**
   * Deletes all community items associated with a specific item.
   *
   * @param  item  the item to delete community items for
   */
  public void deleteAllWithItem(ItemDAO item) {
    List<CommunityItemDAO> communityItems = communityItemRepository.findAllByItem(
      item
    );
    communityItemRepository.deleteAll(communityItems);
  }

  /**
   * Deletes a community item from the database.
   *
   * @param  communityItemDAO  the community item to be deleted
   */
  public void deleteCommunityItem(CommunityItemDAO communityItemDAO) {
    communityItemRepository.delete(communityItemDAO);
  }
}
