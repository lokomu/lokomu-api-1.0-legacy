package no.delalt.back.service.validation;

import no.delalt.back.model.dao.CommunityDAO;
import no.delalt.back.model.dao.CommunityRequestDAO;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.repository.CommunityRequestRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CommunityRequestValidatonService {
  private final CommunityRequestRepository communityRequestRepository;
  private final CommunityValidationService communityValidationService;

  public CommunityRequestValidatonService(
    CommunityRequestRepository communityRequestRepository,
    CommunityValidationService communityValidationService
  ) {
    this.communityRequestRepository = communityRequestRepository;
    this.communityValidationService = communityValidationService;
  }

  /**
   * Validates if a user request exists for a given user and community ID.
   *
   * @param  user          the UserDAO object representing the user
   * @param  communityID   the String representing the community ID
   * @throws ResponseStatusException if the user has no request in the community
   */
  public void validateUserRequestExists(UserDAO user, String communityID) {
    CommunityDAO communityDAO = communityValidationService.validateCommunityExistsAndReturn(
      communityID
    );
    CommunityRequestDAO communityRequestDAO = communityRequestRepository.findByCommunityAndUser(
      communityDAO,
      user
    );
    if (communityRequestDAO == null) {
      throw new ResponseStatusException(
        HttpStatus.FORBIDDEN,
        "User has no request in the community."
      );
    }
  }
}
