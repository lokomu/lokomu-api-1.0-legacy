package no.delalt.back.service;

import no.delalt.back.model.dao.CommunityDAO;
import no.delalt.back.model.dao.GatheringDAO;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.model.dao.UserGatheringDAO;
import no.delalt.back.model.dto.input.GatheringCreationDTO;
import no.delalt.back.model.dto.output.GatheringDTO;
import no.delalt.back.model.id.UserGatheringID;
import no.delalt.back.repository.GatheringRepository;
import no.delalt.back.repository.UserGatheringRepository;
import no.delalt.back.service.validation.CommunityValidationService;
import no.delalt.back.service.validation.UserCommunityValidationService;
import no.delalt.back.service.validation.UserValidationService;
import no.delalt.back.util.NanoIdGenerator;
import no.delalt.back.util.SanitizationUtil;
import no.delalt.back.util.SecurityUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class GatheringService {
  private final GatheringRepository gatheringRepository;
  private final UserGatheringRepository userGatheringRepository;
  private final UserValidationService userValidationService;
  private final CommunityValidationService communityValidationService;
  private final UserCommunityValidationService userCommunityValidationService;
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
    "yyyy-MM-dd HH"
  );
  private static final int SERVER_HOUR_OFFSET = 12;
  private static final int MAXIMUM_TIME_IN_DAYS = 60;

  public GatheringService(
    GatheringRepository gatheringRepository,
    UserGatheringRepository userGatheringRepository,
    UserValidationService userValidationService,
    CommunityValidationService communityValidationService,
    UserCommunityValidationService userCommunityValidationService
  ) {
    this.gatheringRepository = gatheringRepository;
    this.userGatheringRepository = userGatheringRepository;
    this.userValidationService = userValidationService;
    this.communityValidationService = communityValidationService;
    this.userCommunityValidationService = userCommunityValidationService;
  }

  // -------------------- Controller Methods --------------------

  /**
   * Creates a new gathering in the specified community.
   *
   * @param  communityID            the ID of the community where the gathering is created
   * @param  gatheringCreationDTO   the DTO object containing the details of the gathering
   */
  @Transactional
  public void createGathering(
    String communityID,
    GatheringCreationDTO gatheringCreationDTO
  ) {
    UserDAO userDAO = userValidationService.validateUserExistsAndReturn(
      SecurityUtil.getAuthenticatedAccountID()
    );
    CommunityDAO communityDAO = communityValidationService.validateCommunityExistsAndReturn(
      communityID
    );

    userCommunityValidationService.validateCurrentUserIsMemberOfCommunity(
      communityID
    );

    GatheringDAO gatheringDAO = new GatheringDAO();

    String uniqueID = NanoIdGenerator.generateNanoID();
    gatheringDAO.setGatheringID(uniqueID);

    //TODO Add this exception to Global Handler, DateTimeParseException
    LocalDateTime localDateTime = LocalDateTime.parse(
      gatheringCreationDTO.dateAndTime(),
      formatter
    );

    validateGatheringDateAndTime(localDateTime);

    gatheringDAO.setDateAndTime(localDateTime);

    String safeTitle = SanitizationUtil.sanitize(gatheringCreationDTO.title());
    gatheringDAO.setTitle(safeTitle);

    String safeDescription = SanitizationUtil.sanitize(
      gatheringCreationDTO.description()
    );
    gatheringDAO.setDescription(safeDescription);

    gatheringDAO.setIsExpired(false);
    gatheringDAO.setUser(userDAO);
    gatheringDAO.setCommunity(communityDAO);

    gatheringRepository.save(gatheringDAO);

    UserGatheringDAO userGatheringDAO = new UserGatheringDAO(
      userDAO,
      gatheringDAO
    );
    userGatheringRepository.save(userGatheringDAO);
  }

  /**
   * Attends a gathering with the specified gathering ID.
   *
   * @param  gatheringID  the ID of the gathering to attend
   */
  @Transactional
  public void attendGathering(String gatheringID) {
    UserDAO userDAO = userValidationService.validateUserExistsAndReturn(
      SecurityUtil.getAuthenticatedAccountID()
    );
    GatheringDAO gatheringDAO = validateGatheringExistsAndReturn(gatheringID);
    userCommunityValidationService.validateCurrentUserIsMemberOfCommunity(
      gatheringDAO.getCommunity().getCommunityID()
    );

    validateUserNotAlreadyAttendingGathering(userDAO, gatheringDAO);

    UserGatheringDAO userGatheringDAO = new UserGatheringDAO(
      userDAO,
      gatheringDAO
    );
    userGatheringRepository.save(userGatheringDAO);
  }

  /**
   * Retrieves a list of upcoming gatherings for a given community.
   *
   * @param  communityID  the ID of the community
   * @return              a list of GatheringDTO objects representing the upcoming gatherings
   */
  @Transactional(readOnly = true)
  public List<GatheringDTO> retrieveUpcomingGatherings(String communityID) {
    CommunityDAO communityDAO = communityValidationService.validateCommunityExistsAndReturn(
      communityID
    );

    userCommunityValidationService.validateCurrentUserIsMemberOfCommunity(
      communityID
    );

    LocalDateTime localDateTime = LocalDateTime
      .now()
      .minusHours(SERVER_HOUR_OFFSET);

    return gatheringRepository.retrieveUpcomingGatherings(
      communityDAO,
      localDateTime
    );
  }

  /**
   * Retrieves the number of attendees for a given gathering.
   *
   * @param  gatheringID  the ID of the gathering
   * @return              the number of attendees
   */
  @Transactional(readOnly = true)
  public int getNumberOfAttendees(String gatheringID) {
    GatheringDAO gatheringDAO = validateGatheringExistsAndReturn(gatheringID);
    userCommunityValidationService.validateCurrentUserIsMemberOfCommunity(
      gatheringDAO.getCommunity().getCommunityID()
    );

    return userGatheringRepository.getNumberOfAttendees(gatheringDAO);
  }

  // -------------------- Helper Methods --------------------

  /**
   * Validates the existence of a gathering and returns the corresponding GatheringDAO object.
   *
   * @param  gatheringID  the ID of the gathering to validate
   * @return              the corresponding GatheringDAO object if found
   */
  private GatheringDAO validateGatheringExistsAndReturn(String gatheringID) {
    return gatheringRepository
      .findById(gatheringID)
      .orElseThrow(
        () ->
          new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Gathering not found"
          )
      );
  }

  /**
   * Validates whether the user is already attending a gathering.
   *
   * @param  userDAO       the UserDAO object representing the user
   * @param  gatheringDAO  the GatheringDAO object representing the gathering
   */
  private void validateUserNotAlreadyAttendingGathering(
    UserDAO userDAO,
    GatheringDAO gatheringDAO
  ) {
    if (
      userGatheringRepository.existsById(
        new UserGatheringID(userDAO.getUserID(), gatheringDAO.getGatheringID())
      )
    ) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "User is already attending the gathering"
      );
    }
  }

  /**
   * Validates the date and time of a new gathering.
   *
   * @param  localDateTime  the date and time of the gathering
   */
  private void validateGatheringDateAndTime(LocalDateTime localDateTime) {
    if (
      localDateTime.isBefore(LocalDateTime.now().minusHours(SERVER_HOUR_OFFSET))
    ) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Gathering date and time must be in the future"
      );
    }
    if (
      localDateTime.isAfter(LocalDateTime.now().plusDays(MAXIMUM_TIME_IN_DAYS))
    ) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Gathering date and time must be less than 60 days in the future"
      );
    }
  }
}
