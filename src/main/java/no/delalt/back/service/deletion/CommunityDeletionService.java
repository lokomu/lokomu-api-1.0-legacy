package no.delalt.back.service.deletion;

import no.delalt.back.model.dao.CommunityDAO;
import no.delalt.back.model.dao.CommunityItemDAO;
import no.delalt.back.model.dao.UserCommunityDAO;
import no.delalt.back.repository.CommunityRepository;
import no.delalt.back.service.retrieval.CommunityItemRetrievalService;
import no.delalt.back.service.retrieval.UserCommunityRetrievalService;
import no.delalt.back.service.validation.CommunityValidationService;
import no.delalt.back.service.validation.UserCommunityValidationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CommunityDeletionService {
  private final CommunityRepository communityRepository;
  private final CommunityValidationService communityValidationService;

  private final CommunityItemRetrievalService communityItemRetrievalService;
  private final CommunityItemDeletionService communityItemDeletionService;

  private final UserCommunityValidationService userCommunityValidationService;
  private final UserCommunityRetrievalService userCommunityRetrievalService;
  private final UserCommunityDeletionService userCommunityDeletionService;

  private final ImageDeletionService imageDeletionService;

  public CommunityDeletionService(
    CommunityRepository communityRepository,
    UserCommunityValidationService userCommunityValidationService,
    CommunityValidationService communityValidationService,
    ImageDeletionService imageDeletionService,
    UserCommunityRetrievalService userCommunityRetrievalService,
    UserCommunityDeletionService userCommunityDeletionService,
    CommunityItemRetrievalService communityItemRetrievalService,
    CommunityItemDeletionService communityItemDeletionService
  ) {
    this.communityRepository = communityRepository;
    this.userCommunityValidationService = userCommunityValidationService;
    this.communityValidationService = communityValidationService;
    this.imageDeletionService = imageDeletionService;
    this.userCommunityRetrievalService = userCommunityRetrievalService;
    this.userCommunityDeletionService = userCommunityDeletionService;
    this.communityItemRetrievalService = communityItemRetrievalService;
    this.communityItemDeletionService = communityItemDeletionService;
  }

  /**
   * Deletes a community.
   *
   * @param  communityDAO  the community to be deleted
   */
  private void deleteCommunity(CommunityDAO communityDAO) {
    communityRepository.delete(communityDAO);
  }

  /**
   * Handles the deletion of a community.
   *
   * @param  communityID  the ID of the community to be deleted
   */
  @Transactional
  public void handleRemoveCommunity(String communityID) {
    userCommunityValidationService.validateUserIsAdminInCommunity(communityID);
    removeCommunity(communityID);
  }

  /**
   * Removes a community.
   *
   * @param  communityID  the ID of the community to remove
   * @throws ResponseStatusException  if the community does not exist
   */
  public void removeCommunity(String communityID)
    throws ResponseStatusException {
    CommunityDAO communityDAO = communityValidationService.validateCommunityExistsAndReturn(
      communityID
    );

    removeAllUsersFromCommunity(communityDAO);
    deleteAllCommunityItems(communityDAO);
    if (communityDAO.getImage() != null) {
      imageDeletionService.deleteImage(communityDAO.getImage());
    }
    deleteCommunity(communityDAO);
  }

  /**
   * Removes all users from a community.
   *
   * @param  communityDAO  the community to remove users from
   */
  private void removeAllUsersFromCommunity(CommunityDAO communityDAO) {
    List<UserCommunityDAO> users = userCommunityRetrievalService.findAllMembersInACommunityByCommunity(
      communityDAO
    );
    for (UserCommunityDAO user : users) {
      userCommunityDeletionService.deleteUserFromCommunity(user);
    }
  }

  /**
   * Removes all community items from a community.
   *
   * @param  communityDAO  the community to remove community items from
   */
  private void deleteAllCommunityItems(CommunityDAO communityDAO) {
    List<CommunityItemDAO> items = communityItemRetrievalService.getAllCommunityItemForCommunity(
      communityDAO
    );
    for (CommunityItemDAO item : items) {
      communityItemDeletionService.deleteCommunityItem(item);
    }
  }
}
