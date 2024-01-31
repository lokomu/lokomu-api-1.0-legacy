package no.delalt.back.service.retrieval;

import no.delalt.back.model.dao.CommunityDAO;
import no.delalt.back.model.dao.CommunityRequestDAO;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.repository.CommunityRequestRepository;
import org.springframework.stereotype.Service;

@Service
public class CommunityRequestRetrievalService {
  private final CommunityRequestRepository communityRequestRepository;

  public CommunityRequestRetrievalService(
    CommunityRequestRepository communityRequestRepository
  ) {
    this.communityRequestRepository = communityRequestRepository;
  }

  /**
   * Finds a CommunityRequestDAO object by searching for the given UserDAO and
   * CommunityDAO objects.
   *
   * @param  userDAO       the UserDAO object to search for
   * @param  communityDAO  the CommunityDAO object to search for
   * @return               the found CommunityRequestDAO object, or null if not found
   */
  public CommunityRequestDAO findRequest(
    UserDAO userDAO,
    CommunityDAO communityDAO
  ) {
    return communityRequestRepository.findByCommunityAndUser(
      communityDAO,
      userDAO
    );
  }
}
