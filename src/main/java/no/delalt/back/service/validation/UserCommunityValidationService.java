package no.delalt.back.service.validation;

import no.delalt.back.model.dao.CommunityDAO;
import no.delalt.back.model.id.UserCommunityID;
import no.delalt.back.repository.UserCommunityRepository;
import no.delalt.back.util.SecurityUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserCommunityValidationService {
  private final UserCommunityRepository userCommunityRepository;

  public UserCommunityValidationService(
    UserCommunityRepository userCommunityRepository
  ) {
    this.userCommunityRepository = userCommunityRepository;
  }

  //TODO Change to validateCurrentUser
  /**
   * Validates if the current user is an admin of the specified community.
   *
   * @param  communityID   the ID of the community to check admin status for
   * @throws ResponseStatusException   if the user is not an admin of the community
   */
  public void validateUserIsAdminInCommunity(String communityID)
    throws ResponseStatusException {
    if (!userIsAdmin(communityID, SecurityUtil.getAuthenticatedAccountID())) {
      throw new ResponseStatusException(
        HttpStatus.FORBIDDEN,
        "You do not have permission"
      );
    }
  }

  /**
   * Validates if the current user is a member of the specified community.
   *
   * @param  communityID   the ID of the community to check membership for
   * @throws ResponseStatusException   if the user is not a member of the community
   */
  public void validateCurrentUserIsMemberOfCommunity(String communityID)
    throws ResponseStatusException {
    if (
      !checkUserInCommunity(
        SecurityUtil.getAuthenticatedAccountID(),
        communityID
      )
    ) {
      throw new ResponseStatusException(
        HttpStatus.FORBIDDEN,
        "User not in community"
      );
    }
  }

  /**
   * Validates if the current user is not a member of the specified community.
   *
   * @param  communityDAO  the community to check membership for
   * @throws ResponseStatusException   if the user is a member of the community
   */
  public void validateCurrentUserIsNotMemberOfCommunity(
    CommunityDAO communityDAO
  )
    throws ResponseStatusException {
    if (
      checkUserInCommunity(
        SecurityUtil.getAuthenticatedAccountID(),
        communityDAO.getCommunityID()
      )
    ) {
      throw new ResponseStatusException(
        HttpStatus.CONFLICT,
        "User is already in this community"
      );
    }
  }

  /**
   * Validates if the specified user is not a member of the specified community.
   *
   * @param  communityDAO  the community to check membership for
   * @throws ResponseStatusException   if the user is a member of the community
   */
  public void validateUserIsNotMemberOfCommunity(
    CommunityDAO communityDAO,
    String userID
  )
    throws ResponseStatusException {
    if (checkUserInCommunity(userID, communityDAO.getCommunityID())) {
      throw new ResponseStatusException(
        HttpStatus.CONFLICT,
        "User is already in this community"
      );
    }
  }

  /**
   * Returns true if the user with the given userID is a member of the community with the given
   * communityID.
   *
   * @param userID the ID of the user
   * @param communityID the ID of the community
   * @return true if the user is a member of the community
   */
  public boolean checkUserInCommunity(String userID, String communityID) {
    return userCommunityRepository.existsById(
      new UserCommunityID(userID, communityID)
    );
  }

  /**
   * Returns true if the user with the given userID is an administrator of the community with the
   * given communityID.
   *
   * @param userID the ID of the user
   * @param communityID the ID of the community
   * @return true if the user is an administrator of the community
   */
  private boolean userIsAdmin(String communityID, String userID) {
    return userCommunityRepository.existsByUser_UserIDAndCommunity_CommunityIDAndIsAdministratorTrue(
      userID,
      communityID
    );
  }

  /**
   * Validates if the user with the given userID is a member of the community with the given
   * communityID.
   *
   * @param userID the ID of the user
   * @param communityID the ID of the community
   * @throws ResponseStatusException if the user is not a member of the community
   */
  public void validateUserInCommunity(String userID, String communityID) {
    if (!checkUserInCommunity(userID, communityID)) {
      throw new ResponseStatusException(
        HttpStatus.FORBIDDEN,
        "User not in community"
      );
    }
  }
}
