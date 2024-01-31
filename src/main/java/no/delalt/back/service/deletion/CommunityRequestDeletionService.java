package no.delalt.back.service.deletion;

import no.delalt.back.model.dao.CommunityDAO;
import no.delalt.back.model.dao.CommunityRequestDAO;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.repository.CommunityRequestRepository;
import no.delalt.back.service.retrieval.CommunityRequestRetrievalService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunityRequestDeletionService {
  private final CommunityRequestRepository communityRequestRepository;
  private final CommunityRequestRetrievalService communityRequestRetrievalService;

  public CommunityRequestDeletionService(
    CommunityRequestRepository communityRequestRepository,
    CommunityRequestRetrievalService communityRequestRetrievalService
  ) {
    this.communityRequestRepository = communityRequestRepository;
    this.communityRequestRetrievalService = communityRequestRetrievalService;
  }

  /**
   * Deletes all the requests associated with a particular user.
   *
   * @param  userDAO  the user DAO object representing the user
   */
  public void deleteRequestsForUser(UserDAO userDAO) {
    List<CommunityRequestDAO> requests = communityRequestRepository.findAllByUser(
      userDAO
    );
    communityRequestRepository.deleteAll(requests);
  }

  /**
   * Removes a request from the database.
   *
   * @param  userDAO  the user DAO object representing the user
   * @param  communityDAO  the community DAO object representing the community
   */
  public void removeRequest(UserDAO userDAO, CommunityDAO communityDAO) {
    CommunityRequestDAO communityRequestDAO = communityRequestRetrievalService.findRequest(
      userDAO,
      communityDAO
    );
    communityRequestRepository.delete(communityRequestDAO);
  }
}
