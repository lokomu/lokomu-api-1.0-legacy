package no.delalt.back.service;

import java.util.List;
import no.delalt.back.model.dao.CommunityDAO;
import no.delalt.back.model.dao.UserCommunityDAO;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.model.dto.output.CommunityDTO;
import no.delalt.back.model.enums.CommunityVisibilityEnum;
import no.delalt.back.model.id.UserCommunityID;
import no.delalt.back.repository.UserCommunityRepository;
import no.delalt.back.service.deletion.CommunityDeletionService;
import no.delalt.back.service.deletion.UserCommunityDeletionService;
import no.delalt.back.service.save.UserCommunitySaveService;
import no.delalt.back.service.validation.CommunityValidationService;
import no.delalt.back.service.validation.UserCommunityValidationService;
import no.delalt.back.service.validation.UserValidationService;
import no.delalt.back.util.SecurityUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserCommunityService {
  private final UserCommunityRepository userCommunityRepository;
  private final UserCommunityValidationService userCommunityValidationService;
  private final UserCommunityDeletionService userCommunityDeletionService;
  private final UserCommunitySaveService userCommunitySaveService;

  private final CommunityValidationService communityValidationService;
  private final CommunityDeletionService communityDeletionService;

  private final UserValidationService userValidationService;

  public UserCommunityService(
    UserCommunityRepository userCommunityRepository,
    UserCommunityValidationService userCommunityValidationService,
    UserCommunityDeletionService userCommunityDeletionService,
    UserCommunitySaveService userCommunitySaveService,
    CommunityValidationService communityValidationService,
    CommunityDeletionService communityDeletionService,
    UserValidationService userValidationService
  ) {
    this.userCommunityRepository = userCommunityRepository;
    this.userCommunityValidationService = userCommunityValidationService;
    this.userCommunityDeletionService = userCommunityDeletionService;
    this.userCommunitySaveService = userCommunitySaveService;
    this.communityValidationService = communityValidationService;
    this.communityDeletionService = communityDeletionService;
    this.userValidationService = userValidationService;
  }

  // -------------------- Controller Methods --------------------

  /**
   * Adds a user to an open community.
   *
   * @param  communityID  the ID of the community
   */
  @Transactional
  public void addUserToOpenCommunity(String communityID) {
    CommunityDAO communityDAO = communityValidationService.validateCommunityExistsAndReturn(
      communityID
    );

    if (
      communityDAO.getVisibility() == CommunityVisibilityEnum.OPEN.getValue() ||
      communityDAO.getVisibility() == CommunityVisibilityEnum.CLOSED.getValue()
    ) {
      throw new ResponseStatusException(
        HttpStatus.FORBIDDEN,
        "This community is private"
      );
    }

    userCommunityValidationService.validateCurrentUserIsNotMemberOfCommunity(
      communityDAO
    );

    UserDAO user = userValidationService.validateUserExistsAndReturn(
      SecurityUtil.getAuthenticatedAccountID()
    );

    communityValidationService.validateUserIsNearCommunity(user, communityID);

    userCommunitySaveService.saveUserToCommunity(user, communityDAO);
  }

  /**
   * Retrieves a list of CommunityDTO objects associated with the current user.
   *
   * @return         	A list of CommunityDTO objects.
   */
  @Transactional(readOnly = true)
  public List<CommunityDTO> getCommunitiesForCurrentUser() {
    UserDAO user = userValidationService.validateUserExistsAndReturn(
      SecurityUtil.getAuthenticatedAccountID()
    );

    return userCommunityRepository
      .findAllByUser(user)
      .stream()
      .map(UserCommunityDAO::getCommunity)
      .map(CommunityDTO::new)
      .toList();
  }

  /**
   * Retrieves a list of CommunityDTO objects associated with the current user.
   *
   * @return         	A list of CommunityDTO objects.
   */
  @Transactional(readOnly = true)
  public List<String> getAdminCommunitiesForCurrentUser() {
    UserDAO user = userValidationService.validateUserExistsAndReturn(
      SecurityUtil.getAuthenticatedAccountID()
    );

    List<UserCommunityDAO> userCommunityDAOs = userCommunityRepository.findByUserAndIsAdministratorTrue(
      user
    );

    return userCommunityDAOs
      .stream()
      .map(userCommunityDAO -> userCommunityDAO.getCommunity().getCommunityID())
      .toList();
  }

  /**
   * Removes a user from a community.
   *
   * @param  communityID  the ID of the community
   */
  @Transactional
  public void leaveCommunity(String communityID) {
    CommunityDAO communityDAO = communityValidationService.validateCommunityExistsAndReturn(
      communityID
    );
    UserCommunityDAO userCommunityDAO = validateCurrentUserIsMemberOfCommunityAndReturn(
      communityID
    );

    int membersCount = getCommunityMemberCount(communityDAO);

    if (userCommunityDAO.getIsAdministrator()) {
      int adminsCount = getAdminsSize(communityDAO);
      if (adminsCount <= 1 && membersCount > 1) {
        throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "Can not leave community, needs a new admin"
        );
      }
    }

    userCommunityDeletionService.deleteUserFromCommunity(userCommunityDAO);

    if (membersCount == 1) {
      communityDeletionService.removeCommunity(communityID);
    }
  }

  /**
   * Removes a user from a community.
   *
   * @param  communityID  the ID of the community
   */
  @Transactional
  public void kickUserFromCommunity(String communityID, String userID) {
    communityValidationService.validateCommunityExists(communityID);
    userCommunityValidationService.validateUserIsAdminInCommunity(communityID);
    userValidationService.validateUserExists(userID);

    UserCommunityDAO userToBeKicked = validateUserInCommunityAndReturn(
      userID,
      communityID
    );

    if (userToBeKicked.getIsAdministrator()) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Can not kick admin"
      );
    }

    userCommunityDeletionService.deleteUserFromCommunity(userToBeKicked);
  }

  /**
   * Retrieves the admin user ID of a community.
   *
   * @param  communityID the ID of the community
   * @return             the user ID of the admin
   */
  @Transactional(readOnly = true)
  public String getAdminUserIDOfCommunity(String communityID) {
    CommunityDAO communityDAO = communityValidationService.validateCommunityExistsAndReturn(
      communityID
    );
    UserCommunityDAO userCommunityDAO = userCommunityRepository.findByCommunityAndIsAdministratorTrue(
      communityDAO
    );
    if (userCommunityDAO == null) throw new ResponseStatusException(
      HttpStatus.NOT_FOUND,
      "No admin found"
    );
    return userCommunityDAO.getUser().getUserID();
  }

  /**
   * Determines if the current user is part of a specific community.
   *
   * @param  communityID  the ID of the community to check membership for
   * @return              true if the current user is a member of the community, false otherwise
   */
  @Transactional(readOnly = true)
  public boolean isCurrentUserInCommunity(String communityID) {
    communityValidationService.validateCommunityExists(communityID);
    return userCommunityValidationService.checkUserInCommunity(
      SecurityUtil.getAuthenticatedAccountID(),
      communityID
    );
  }

  // -------------------- Helper Methods --------------------

  /**
   * Returns the size of the admins in the community.
   *
   * @param  communityDAO  the CommunityDAO object representing the community
   * @return               the number of administrators in the community
   */
  private int getAdminsSize(CommunityDAO communityDAO) {
    return userCommunityRepository.countByCommunityAndIsAdministratorTrue(
      communityDAO
    );
  }

  /**
   * Returns the number of members in the community.
   *
   * @param  communityDAO  the CommunityDAO object representing the community
   * @return               the number of members in the community
   */
  private int getCommunityMemberCount(CommunityDAO communityDAO) {
    return userCommunityRepository.countByCommunity(communityDAO);
  }

  /**
   * Validates if a user is in a community and returns the UserCommunityDAO object.
   *
   * @param  userID        the ID of the user
   * @param  communityID   the ID of the community
   * @return               the UserCommunityDAO object representing the user and the community
   */
  private UserCommunityDAO validateUserInCommunityAndReturn(
    String userID,
    String communityID
  ) {
    return userCommunityRepository
      .findById(new UserCommunityID(userID, communityID))
      .orElseThrow(
        () ->
          new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "User not in community."
          )
      );
  }

  /**
   * Validates if the current user is a member of the given community and returns the UserCommunityDAO object.
   *
   * @param  communityID  the ID of the community to validate membership for
   * @return              the UserCommunityDAO object if the user is a member of the community
   * @throws ResponseStatusException if the user is not a member of the community
   */
  private UserCommunityDAO validateCurrentUserIsMemberOfCommunityAndReturn(
    String communityID
  )
    throws ResponseStatusException {
    return validateUserInCommunityAndReturn(
      SecurityUtil.getAuthenticatedAccountID(),
      communityID
    );
  }
}
