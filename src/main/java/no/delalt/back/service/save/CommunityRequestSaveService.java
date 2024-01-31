package no.delalt.back.service.save;

import no.delalt.back.model.dao.CommunityDAO;
import no.delalt.back.model.dao.CommunityRequestDAO;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.repository.CommunityRequestRepository;
import no.delalt.back.util.NanoIdGenerator;
import org.springframework.stereotype.Service;

@Service
public class CommunityRequestSaveService {
  private final CommunityRequestRepository communityRequestRepository;

  public CommunityRequestSaveService(
    CommunityRequestRepository communityRequestRepository
  ) {
    this.communityRequestRepository = communityRequestRepository;
  }

  /**
   * Saves the given `CommunityRequestDAO` object to the database.
   *
   * @param  communityDAO  the `CommunityDAO` object to be saved
   * @param  userDAO       the `UserDAO` object to be saved
   * @param  message       the message of the request
   */
  public void addNewRequest(
    CommunityDAO communityDAO,
    UserDAO userDAO,
    String message
  ) {
    CommunityRequestDAO communityRequestDAO = new CommunityRequestDAO();
    communityRequestDAO.setCommunityRequestID(NanoIdGenerator.generateNanoID());
    communityRequestDAO.setCommunity(communityDAO);
    communityRequestDAO.setUser(userDAO);
    communityRequestDAO.setText(message);

    communityRequestRepository.save(communityRequestDAO);
  }
}
