package no.delalt.back.service.validation;

import no.delalt.back.model.dao.CommunityDAO;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.repository.CommunityRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CommunityValidationService {
  private final CommunityRepository communityRepository;
  private static final int MAXIMUM_DISTANCE = 5000;

  public CommunityValidationService(CommunityRepository communityRepository) {
    this.communityRepository = communityRepository;
  }

  /**
   * Validates if the user is near the specified community.
   *
   * @param userDAO the user to validate
   * @param communityID the ID of the community
   * @throws ResponseStatusException if the user is too far from the community
   */
  public void validateUserIsNearCommunity(UserDAO userDAO, String communityID) {
    if (
      !communityRepository.isUserNearCommunity(
        userDAO.getCoordinates().getX(),
        userDAO.getCoordinates().getY(),
        MAXIMUM_DISTANCE,
        communityID
      )
    ) {
      throw new ResponseStatusException(
        HttpStatus.FORBIDDEN,
        "User too far from community."
      );
    }
  }

  /**
   * Validates if a community exists and returns the CommunityDAO object.
   *
   * @param communityID the ID of the community
   * @return the CommunityDAO object if the community exists
   * @throws ResponseStatusException if the community does not exist
   */
  public CommunityDAO validateCommunityExistsAndReturn(String communityID)
    throws ResponseStatusException {
    return findCommunityDAOByCommunityID(communityID);
  }

  /**
   * Validates if a community with the given community ID exists.
   *
   * @param communityID the ID of the community to validate
   * @throws ResponseStatusException if the community does not exist
   */
  public void validateCommunityExists(String communityID) {
    if (!communityRepository.existsById(communityID)) {
      throw new ResponseStatusException(
        HttpStatus.NOT_FOUND,
        "Community exists."
      );
    }
  }

  /**
   * Finds a CommunityDAO object by the given community ID.
   *
   * @param  communityID  The ID of the community.
   * @return              The CommunityDAO object with the matching community ID.
   * @throws ResponseStatusException If the community is not found.
   */
  public CommunityDAO findCommunityDAOByCommunityID(String communityID)
    throws ResponseStatusException {
    return communityRepository
      .findById(communityID)
      .orElseThrow(
        () ->
          new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Community not found"
          )
      );
  }
}
