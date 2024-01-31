package no.delalt.back.service.save;

import no.delalt.back.model.dao.CommunityDAO;
import no.delalt.back.model.dao.UserCommunityDAO;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.repository.UserCommunityRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserCommunitySaveService {
  private final UserCommunityRepository userCommunityRepository;

  public UserCommunitySaveService(
    UserCommunityRepository userCommunityRepository
  ) {
    this.userCommunityRepository = userCommunityRepository;
  }

  /**
   * Saves a user to a community in the database.
   *
   * @param  user         the UserDAO object representing the user to be saved
   * @param  communityDAO the CommunityDAO object representing the community to which the user is being saved
   * @throws ResponseStatusException if an error occurs while saving the user to the community
   */
  public void saveUserToCommunity(UserDAO user, CommunityDAO communityDAO)
    throws ResponseStatusException {
    try {
      userCommunityRepository.save(
        new UserCommunityDAO(communityDAO, user, false)
      );
    } catch (Exception e) {
      throw new ResponseStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Something went wrong."
      );
    }
  }

  /**
   * Saves a user as an admin to a community in the database.
   *
   * @param  user         the UserDAO object representing the user to be saved
   * @param  communityDAO the CommunityDAO object representing the community to which the user is being saved
   * @throws ResponseStatusException if an error occurs while saving the user to the community
   */
  public void saveUserAsAdminToCommunity(
    UserDAO user,
    CommunityDAO communityDAO
  )
    throws ResponseStatusException {
    userCommunityRepository.save(
      new UserCommunityDAO(communityDAO, user, true)
    );
  }
}
