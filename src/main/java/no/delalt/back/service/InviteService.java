package no.delalt.back.service;

import no.delalt.back.model.dao.CommunityDAO;
import no.delalt.back.model.dao.InviteDAO;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.model.dto.output.InviteDTO;
import no.delalt.back.repository.InviteRepository;
import no.delalt.back.service.save.UserCommunitySaveService;
import no.delalt.back.service.validation.CommunityValidationService;
import no.delalt.back.service.validation.UserCommunityValidationService;
import no.delalt.back.service.validation.UserValidationService;
import no.delalt.back.util.NanoIdGenerator;
import no.delalt.back.util.SecurityUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class InviteService {
  private final UserCommunitySaveService userCommunitySaveService;
  private final UserCommunityValidationService userCommunityValidationService;

  private final InviteRepository inviteRepository;
  private final UserValidationService userValidationService;
  private final CommunityValidationService communityValidationService;

  public InviteService(
    UserCommunitySaveService userCommunitySaveService,
    UserCommunityValidationService userCommunityValidationService,
    InviteRepository inviteRepository,
    UserValidationService userValidationService,
    CommunityValidationService communityValidationService
  ) {
    this.userCommunitySaveService = userCommunitySaveService;
    this.userCommunityValidationService = userCommunityValidationService;
    this.inviteRepository = inviteRepository;
    this.userValidationService = userValidationService;
    this.communityValidationService = communityValidationService;
  }

  private static final int EXPIRATION_IN_HOURS = 24;

  // -------------------- Controller Methods --------------------

  /**
   * Handles the creation of an invitation for a given community.
   *
   * @param  communityID  the ID of the community
   * @return              the created invite as a DTO
   */
  @Transactional
  public InviteDTO handleInviteCreation(String communityID) {
    CommunityDAO communityDAO = communityValidationService.validateCommunityExistsAndReturn(
      communityID
    );
    userCommunityValidationService.validateCurrentUserIsMemberOfCommunity(
      communityID
    );

    InviteDAO inviteDAO = createInvite(communityDAO);
    saveInvite(inviteDAO);

    return new InviteDTO(inviteDAO);
  }

  /**
   * Handles the acceptance of an invitation.
   *
   * @param  inviteID  the ID of the invitation
   * @return           the ID of the accepted community
   */
  @Transactional
  public String handleInviteAcceptance(String inviteID) {
    InviteDAO invite = getInviteByInviteID(inviteID);

    if (invite.getIsExpired() || isInviteExpired(invite)) {
      invite.setIsExpired(true);
      saveInvite(invite);
      throw new ResponseStatusException(HttpStatus.GONE, "Invite expired");
    }

    CommunityDAO communityDAO = communityValidationService.validateCommunityExistsAndReturn(
      invite.getCommunity().getCommunityID()
    );
    userCommunityValidationService.validateCurrentUserIsNotMemberOfCommunity(
      communityDAO
    );

    UserDAO user = userValidationService.findUserByUserID(
      SecurityUtil.getAuthenticatedAccountID()
    );

    userCommunitySaveService.saveUserToCommunity(user, communityDAO);

    return communityDAO.getCommunityID();
  }

  // -------------------- Helper Methods --------------------

  /**
   * Saves the given invite object to the invite repository.
   *
   * @param  inviteDAO  the InviteDAO object to be saved
   */
  private void saveInvite(InviteDAO inviteDAO) {
    inviteRepository.save(inviteDAO);
  }

  /**
   * Creates and returns an InviteDAO object.
   *
   * @param  communityDAO  the CommunityDAO object
   * @return              the created InviteDAO object
   */
  private InviteDAO createInvite(CommunityDAO communityDAO) {
    InviteDAO invite = new InviteDAO();
    invite.setCreatedAt(System.currentTimeMillis());

    String uniqueID = NanoIdGenerator.generateNanoID();
    invite.setInviteID(uniqueID);

    UserDAO userDAO = userValidationService.validateUserExistsAndReturn(
      SecurityUtil.getAuthenticatedAccountID()
    );
    invite.setUser(userDAO);

    invite.setCommunity(communityDAO);
    invite.setIsExpired(false);

    return invite;
  }

  /**
   * Returns the InviteDAO object for the given inviteID.
   *
   * @param  inviteID  the ID of the invite
   * @return           the InviteDAO object
   */
  private InviteDAO getInviteByInviteID(String inviteID)
    throws ResponseStatusException {
    return inviteRepository
      .findById(inviteID)
      .orElseThrow(
        () ->
          new ResponseStatusException(HttpStatus.NOT_FOUND, "Invite not found")
      );
  }

  /**
   * Checks if the given invite is expired.
   *
   * @param  inviteDAO  the InviteDAO object
   * @return           true if the invite is expired, false otherwise
   */
  private static boolean isInviteExpired(InviteDAO inviteDAO) {
    Instant createdAtInstant = Instant.ofEpochMilli(inviteDAO.getCreatedAt());
    Instant now = Instant.now();

    long hoursElapsed = ChronoUnit.HOURS.between(createdAtInstant, now);
    return hoursElapsed >= EXPIRATION_IN_HOURS;
  }
}
