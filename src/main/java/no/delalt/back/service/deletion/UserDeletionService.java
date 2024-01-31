package no.delalt.back.service.deletion;

import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.repository.UserRepository;
import no.delalt.back.service.validation.UserValidationService;
import no.delalt.back.service.worker.AuthWorkerSevice;
import no.delalt.back.util.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class UserDeletionService {
  private final UserRepository userRepository;
  private final UserValidationService userValidationService;
  private final UserCommunityDeletionService userCommunityDeletionService;
  private final CommunityRequestDeletionService communityRequestDeletionService;
  private final ItemDeletionService itemDeletionService;
  private final AuthWorkerSevice authWorkerSevice;
  private final InviteDeletionService inviteDeletionService;
  private final ImageDeletionService imageDeletionService;
  private final BorrowRequestDeletionService borrowRequestDeletionService;
  private final RatingDeletionService ratingDeletionService;
  private final BorrowAgreementDeletionService borrowAgreementDeletionService;

  private final static int RESET_YEAR = 2000;

  public UserDeletionService(
    UserRepository userRepository,
    UserValidationService userValidationService,
    UserCommunityDeletionService userCommunityDeletionService,
    CommunityRequestDeletionService communityRequestDeletionService,
    ItemDeletionService itemDeletionService,
    AuthWorkerSevice authWorkerSevice,
    InviteDeletionService inviteDeletionService,
    ImageDeletionService imageDeletionService,
    BorrowRequestDeletionService borrowRequestDeletionService,
    RatingDeletionService ratingDeletionService,
    BorrowAgreementDeletionService borrowAgreementDeletionService
  ) {
    this.userRepository = userRepository;
    this.userValidationService = userValidationService;
    this.userCommunityDeletionService = userCommunityDeletionService;
    this.communityRequestDeletionService = communityRequestDeletionService;
    this.itemDeletionService = itemDeletionService;
    this.authWorkerSevice = authWorkerSevice;
    this.inviteDeletionService = inviteDeletionService;
    this.imageDeletionService = imageDeletionService;
    this.borrowRequestDeletionService = borrowRequestDeletionService;
    this.ratingDeletionService = ratingDeletionService;
    this.borrowAgreementDeletionService = borrowAgreementDeletionService;
  }

  /**
   * Deletes a user and all associated data.
   *
   */
  @Transactional
  public void deleteUserAndAssociatedData() {
    String userID = SecurityUtil.getAuthenticatedAccountID();

    UserDAO userDAO = userValidationService.validateUserExistsAndReturn(userID);

    //Deletes all items and removes them from all communities
    itemDeletionService.deleteItemsForUser(userDAO);

    //Deletes user from all communities
    userCommunityDeletionService.deleteUserFromAllGroups(userDAO);

    //Deletes community requests
    communityRequestDeletionService.deleteRequestsForUser(userDAO);

    //Deletes invites
    inviteDeletionService.deleteInvitesByUser(userDAO);

    //Deletes images
    //TODO Check that none of the images are used in a community, if so set to null
    imageDeletionService.deleteImagesForUser(userDAO);

    //Delete borrow requests
    borrowRequestDeletionService.deleteUnprocessedRequestsForUser(userDAO);
    borrowRequestDeletionService.deleteProcessedButNotAcceptedRequestsForUser(
      userDAO
    );
    borrowRequestDeletionService.deleteProcessedAcceptedForOwner(userDAO);

    //Delete ratings
    ratingDeletionService.deleteRatingsForOwner(userDAO);

    //Delete borrow agreements
    borrowAgreementDeletionService.deleteBorrowAgreementsForOwner(userDAO);

    clearUserInfo(userDAO);
  }

  /**
   * Clears the user information by setting the first name to "Deleted",
   * the last name to "User: " followed by the user ID, the image to null,
   * the hash to an empty string, and saves the changes
   * to the userDAO object.
   *
   * @param  userDAO  the UserDAO object representing the user to clear
   */
  private void clearUserInfo(UserDAO userDAO) {
    userDAO.setFirstName("Deleted");
    userDAO.setLastName("User: " + userDAO.getUserID());
    userDAO.setImage(null);
    userDAO.setHash("");
    userDAO.setCoordinates(authWorkerSevice.createRandomOffsetPoint(0, 0));
    userDAO.setLastLocationUpdate(LocalDate.of(RESET_YEAR, 1, 1));
    userDAO.setDeletedAt(LocalDate.now());

    userRepository.save(userDAO);
  }
}
