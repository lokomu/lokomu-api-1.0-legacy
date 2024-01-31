package no.delalt.back.service.save;

import no.delalt.back.model.dao.CommunityDAO;
import no.delalt.back.model.dao.CommunityItemDAO;
import no.delalt.back.model.dao.ItemDAO;
import no.delalt.back.repository.CommunityItemRepository;
import org.springframework.stereotype.Service;

@Service
public class CommunityItemSaveService {
  private final CommunityItemRepository communityItemRepository;

  public CommunityItemSaveService(
    CommunityItemRepository communityItemRepository
  ) {
    this.communityItemRepository = communityItemRepository;
  }

  /**
   * Saves the given `CommunityItemDAO` object to the database.
   *
   * @param  communityDAO  the `CommunityDAO` object to be saved
   * @param  itemDAO       the `ItemDAO` object to be saved
   */
  public void saveCommunityItem(CommunityDAO communityDAO, ItemDAO itemDAO) {
    CommunityItemDAO communityItemDAO = new CommunityItemDAO(
      communityDAO,
      itemDAO
    );
    communityItemRepository.save(communityItemDAO);
  }
}
