package no.delalt.back.service;

import no.delalt.back.model.dao.CommunityDAO;
import no.delalt.back.model.dao.CommunityRequestDAO;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.model.dto.input.CommunityRequestCreationDTO;
import no.delalt.back.model.dto.output.CommunityRequestDTO;
import no.delalt.back.model.dto.output.UserDTO;
import no.delalt.back.repository.CommunityRequestRepository;
import no.delalt.back.service.deletion.CommunityRequestDeletionService;
import no.delalt.back.service.retrieval.CommunityRequestRetrievalService;
import no.delalt.back.service.save.CommunityRequestSaveService;
import no.delalt.back.service.save.UserCommunitySaveService;
import no.delalt.back.service.validation.CommunityValidationService;
import no.delalt.back.service.validation.UserCommunityValidationService;
import no.delalt.back.service.validation.UserValidationService;
import no.delalt.back.util.SanitizationUtil;
import no.delalt.back.util.SecurityUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CommunityRequestService {
  private final CommunityRequestRepository communityRequestRepository;
  private final CommunityRequestRetrievalService communityRequestRetrievalService;
  private final CommunityRequestDeletionService communityRequestDeletionService;
  private final CommunityRequestSaveService communityRequestSaveService;

  private final UserCommunityValidationService userCommunityValidationService;
  private final UserCommunitySaveService userCommunitySaveService;

  private final CommunityValidationService communityValidationService;
  private final UserValidationService userValidationService;

  public CommunityRequestService(
    CommunityRequestRepository communityRequestRepository,
    CommunityRequestRetrievalService communityRequestRetrievalService,
    CommunityRequestDeletionService communityRequestDeletionService,
    CommunityRequestSaveService communityRequestSaveService,
    UserCommunityValidationService userCommunityValidationService,
    UserCommunitySaveService userCommunitySaveService,
    CommunityValidationService communityValidationService,
    UserValidationService userValidationService
  ) {
    this.communityRequestRepository = communityRequestRepository;
    this.communityRequestRetrievalService = communityRequestRetrievalService;
    this.communityRequestDeletionService = communityRequestDeletionService;
    this.communityRequestSaveService = communityRequestSaveService;
    this.userCommunityValidationService = userCommunityValidationService;
    this.userCommunitySaveService = userCommunitySaveService;
    this.communityValidationService = communityValidationService;
    this.userValidationService = userValidationService;
  }

  // -------------------- Controller Methods --------------------

  /**
   * Request to join a private community.
   *
   * @param  communityID     the ID of the community to join
   * @param  creationDTO     the DTO containing the request creation details
   */
  @Transactional
  public void requestToJoinPrivateCommunity(
    String communityID,
    CommunityRequestCreationDTO creationDTO
  ) {
    CommunityDAO communityDAO = communityValidationService.validateCommunityExistsAndReturn(
      communityID
    );

    if (communityDAO.getVisibility() == 2) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "This community is open"
      );
    }

    userCommunityValidationService.validateCurrentUserIsNotMemberOfCommunity(
      communityDAO
    );

    if (
      communityRequestRetrievalService.findRequest(
        userValidationService.findUserByUserID(
          SecurityUtil.getAuthenticatedAccountID()
        ),
        communityDAO
      ) !=
      null
    ) {
      throw new ResponseStatusException(
        HttpStatus.CONFLICT,
        "Request already exists"
      );
    }

    String safeMessage = SanitizationUtil.sanitize(creationDTO.message());

    communityRequestSaveService.addNewRequest(
      communityDAO,
      userValidationService.validateUserExistsAndReturn(
        SecurityUtil.getAuthenticatedAccountID()
      ),
      safeMessage
    );
  }

  //TODO Change the findRequest getting
  /**
   * Process the join request of a user to a community.
   *
   * @param  communityID  the ID of the community
   * @param  userID       the ID of the user
   */
  @Transactional
  public void processCommunityJoinRequest(String communityID, String userID) {
    UserDAO userDAO = userValidationService.validateUserExistsAndReturn(userID);
    CommunityDAO communityDAO = communityValidationService.validateCommunityExistsAndReturn(
      communityID
    );
    userCommunityValidationService.validateUserIsNotMemberOfCommunity(
      communityDAO,
      userID
    );
    userCommunityValidationService.validateUserIsAdminInCommunity(communityID);

    communityRequestRetrievalService.findRequest(userDAO, communityDAO);
    userCommunitySaveService.saveUserToCommunity(userDAO, communityDAO);
    communityRequestDeletionService.removeRequest(userDAO, communityDAO);
  }

  /**
   * Retrieves a list of UserDTO objects for the requests associated with a given community ID.
   *
   * @param  communityID  the ID of the community
   * @return              a list of UserDTO objects representing the requests for the community
   */
  @Transactional(readOnly = true)
  public List<UserDTO> getRequestsByCommunityID(String communityID) {
    CommunityDAO communityDAO = communityValidationService.validateCommunityExistsAndReturn(
      communityID
    );
    userCommunityValidationService.validateUserIsAdminInCommunity(communityID);

    return getRequestsForCommunity(communityDAO);
  }

  //TODO Insecure method, should validate the person finding the request
  /**
   * Retrieves a CommunityRequestDTO object for a given user ID and community ID.
   *
   * @param  userID       the ID of the user
   * @param  communityID  the ID of the community
   * @return              a CommunityRequestDTO object representing the request for the user and community
   */
  @Transactional(readOnly = true)
  public CommunityRequestDTO findRequestByUserAndCommunityID(
    String userID,
    String communityID
  ) {
    UserDAO userDAO = userValidationService.validateUserExistsAndReturn(userID);
    CommunityDAO communityDAO = communityValidationService.validateCommunityExistsAndReturn(
      communityID
    );

    CommunityRequestDAO communityRequestDAO = communityRequestRetrievalService.findRequest(
      userDAO,
      communityDAO
    );

    if (communityRequestDAO == null) {
      throw new ResponseStatusException(
        HttpStatus.NOT_FOUND,
        "Request not found"
      );
    }

    return new CommunityRequestDTO(communityRequestDAO);
  }

  /**
   * Rejects a request for a given user ID and community ID.
   *
   * @param  userID       the ID of the user
   * @param  communityID  the ID of the community
   */
  @Transactional
  public void rejectRequestByUserAndCommunityID(
    String userID,
    String communityID
  ) {
    UserDAO userDAO = userValidationService.validateUserExistsAndReturn(userID);
    CommunityDAO communityDAO = communityValidationService.validateCommunityExistsAndReturn(
      communityID
    );
    userCommunityValidationService.validateUserIsAdminInCommunity(communityID);

    communityRequestDeletionService.removeRequest(userDAO, communityDAO);
  }

  /**
   * Removes an own request for a given community ID.
   *
   * @param  communityID  the ID of the community
   */
  @Transactional
  public void removeOwnRequest(String communityID) {
    UserDAO userDAO = userValidationService.validateUserExistsAndReturn(
      SecurityUtil.getAuthenticatedAccountID()
    );
    CommunityDAO communityDAO = communityValidationService.validateCommunityExistsAndReturn(
      communityID
    );

    communityRequestDeletionService.removeRequest(userDAO, communityDAO);
  }

  // -------------------- Helper Methods --------------------

  /**
   * Retrieves a list of UserDTO objects for the requests associated with a given community.
   *
   * @param  communityDAO  the DAO of the community
   * @return               a list of UserDTO objects representing the requests for the community
   */
  private List<UserDTO> getRequestsForCommunity(CommunityDAO communityDAO) {
    return communityRequestRepository
      .findAllByCommunity(communityDAO)
      .stream()
      .map(requestDAO -> new UserDTO(requestDAO.getUser()))
      .toList();
  }
}
