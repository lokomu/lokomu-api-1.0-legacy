package no.delalt.back.service.save;

import no.delalt.back.model.dao.CommunityDAO;
import no.delalt.back.repository.CommunityRepository;
import org.springframework.stereotype.Service;

@Service
public class CommunitySaveService {
  private final CommunityRepository communityRepository;

  public CommunitySaveService(CommunityRepository communityRepository) {
    this.communityRepository = communityRepository;
  }

  /**
   * Saves the given `CommunityDAO` object to the database.
   *
   * @param  communityDAO  the `CommunityDAO` object to be saved
   */
  public void saveCommunity(CommunityDAO communityDAO) {
    communityRepository.save(communityDAO);
  }
}
