package no.delalt.back.service;

import no.delalt.back.model.dao.CommunityDAO;
import no.delalt.back.model.dao.UserCommunityDAO;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.model.dto.input.CommunityCreationDTO;
import no.delalt.back.model.dto.output.CommunityDTO;
import no.delalt.back.model.dto.output.UserDTO;
import no.delalt.back.model.enums.CommunityVisibilityEnum;
import no.delalt.back.repository.CommunityRepository;
import no.delalt.back.service.deletion.ImageDeletionService;
import no.delalt.back.service.retrieval.UserCommunityRetrievalService;
import no.delalt.back.service.save.CommunitySaveService;
import no.delalt.back.service.save.UserCommunitySaveService;
import no.delalt.back.service.validation.CommunityValidationService;
import no.delalt.back.service.validation.UserCommunityValidationService;
import no.delalt.back.service.validation.UserValidationService;
import no.delalt.back.util.NanoIdGenerator;
import no.delalt.back.util.SanitizationUtil;
import no.delalt.back.util.SecurityUtil;
import org.locationtech.jts.geom.Point;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Service
public class CommunityService {
  private final CommunityRepository communityRepository;
  private final CommunityValidationService communityValidationService;
  private final CommunitySaveService communitySaveService;

  private final UserCommunityRetrievalService userCommunityRetrievalService;
  private final UserCommunityValidationService userCommunityValidationService;
  private final UserCommunitySaveService userCommunitySaveService;

  private final UserValidationService userValidationService;
  private final ImageDeletionService imageDeletionService;

  private final static int MINIMUM_DISTANCE = 500; //In meters
  private final static int MAXIMUM_DISTANCE = 3000; //In meters

  public CommunityService(
    CommunityRepository communityRepository,
    CommunityValidationService communityValidationService,
    CommunitySaveService communitySaveService,
    UserCommunityRetrievalService userCommunityRetrievalService,
    UserCommunityValidationService userCommunityValidationService,
    UserCommunitySaveService userCommunitySaveService,
    UserValidationService userValidationService,
    ImageDeletionService imageDeletionService
  ) {
    this.communityRepository = communityRepository;
    this.communityValidationService = communityValidationService;
    this.communitySaveService = communitySaveService;
    this.userCommunityRetrievalService = userCommunityRetrievalService;
    this.userCommunityValidationService = userCommunityValidationService;
    this.userCommunitySaveService = userCommunitySaveService;
    this.userValidationService = userValidationService;
    this.imageDeletionService = imageDeletionService;
  }

  // -------------------- Controller Methods --------------------

  /**
   * Retrieves the community DTO for a given member.
   *
   * @param  communityID  the ID of the community to retrieve
   * @return              the community DTO for the member
   */
  @Transactional(readOnly = true)
  public CommunityDTO retrieveCommunityForMember(String communityID) {
    userCommunityValidationService.validateCurrentUserIsMemberOfCommunity(
      communityID
    );

    return new CommunityDTO(
      communityValidationService.validateCommunityExistsAndReturn(communityID)
    );
  }

  /**
   * Handles the creation of a new community.
   *
   * @param  communityDTO  the community DTO to create
   * @return              the ID of the created community
   */
  @Transactional
  public String handleAddCommunity(CommunityCreationDTO communityDTO) {
    UserDAO userDAO = userValidationService.validateUserExistsAndReturn(
      SecurityUtil.getAuthenticatedAccountID()
    );

    Point coordinates = userDAO.getCoordinates();

    CommunityDAO communityDAO = communityCreationToDAO(
      communityDTO,
      coordinates
    );
    //TODO Dont let the user be a part of more than 30 communities

    communitySaveService.saveCommunity(communityDAO);

    userCommunitySaveService.saveUserAsAdminToCommunity(userDAO, communityDAO);

    return communityDAO.getCommunityID();
  }

  /**
   * Handles nearby communities for a user.
   *
   * @param  distance  the distance parameter for nearby communities
   * @return           a list of CommunityDTO objects representing nearby communities
   * @throws ResponseStatusException if the distance parameter is invalid
   */
  @Transactional(readOnly = true)
  public List<CommunityDTO> handleNearbyCommunitiesForUser(double distance)
    throws ResponseStatusException {
    if (distance < MINIMUM_DISTANCE || distance > MAXIMUM_DISTANCE) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Invalid distance parameter"
      );
    }

    UserDAO user = userValidationService.validateUserExistsAndReturn(
      SecurityUtil.getAuthenticatedAccountID()
    );
    List<CommunityDAO> communityDAOs = getNearbyCommunities(
      user.getCoordinates().getX(),
      user.getCoordinates().getY(),
      distance,
      user.getUserID()
    );
    return communityDAOs.stream().map(CommunityDTO::new).toList();
  }

  /**
   * Retrieves the members of a community.
   *
   * @param  communityID  the ID of the community to retrieve members from
   * @return              a list of UserDTO objects representing the members of the community
   */
  @Transactional(readOnly = true)
  public List<UserDTO> getMembersForCommunity(String communityID)
    throws ResponseStatusException {
    CommunityDAO communityDAO = communityValidationService.validateCommunityExistsAndReturn(
      communityID
    );

    List<UserCommunityDAO> userCommunityDAOs = userCommunityRetrievalService.findAllMembersInACommunityByCommunity(
      communityDAO
    );
    if (userCommunityDAOs == null) {
      throw new RuntimeException("Error retrieving community members");
    }

    return userCommunityDAOs
      .stream()
      .map(userCommunityDAO -> new UserDTO(userCommunityDAO.getUser()))
      .toList();
  }

  /**
   * Updates a community.
   *
   * @param  communityID  the ID of the community to update
   * @param  communityDTO  the updated community DTO
   * @return              the ID of the updated community
   */
  @Transactional
  public String updateCommunity(
    String communityID,
    CommunityCreationDTO communityDTO
  )
    throws ResponseStatusException {
    CommunityDAO community = communityValidationService.validateCommunityExistsAndReturn(
      communityID
    );
    userCommunityValidationService.validateUserIsAdminInCommunity(communityID);

    if (
      communityDTO.visibility() != community.getVisibility() &&
      community.getVisibility() == CommunityVisibilityEnum.OPEN.getValue()
    ) {
      community.setCoordinates(
        userValidationService
          .validateUserExistsAndReturn(SecurityUtil.getAuthenticatedAccountID())
          .getCoordinates()
      );
    }

    community.setVisibility(communityDTO.visibility());

    if (
      community.getImage() != null &&
      !Objects.equals(community.getImage(), communityDTO.image())
    ) {
      imageDeletionService.deleteImage(community.getImage());
    }

    String safeName = SanitizationUtil.sanitize(communityDTO.name());
    String safeDescription = SanitizationUtil.sanitize(
      communityDTO.description()
    );
    String safeLocation = SanitizationUtil.sanitize(communityDTO.location());

    //TODO Validate that image belongs to user
    community.setImage(communityDTO.image());
    community.setName(safeName);
    community.setDescription(safeDescription);
    community.setLocation(safeLocation);

    communitySaveService.saveCommunity(community);

    return community.getCommunityID();
  }

  // -------------------- Helper Methods --------------------

  /**
   * Converts a CommunityCreationDTO object into a CommunityDAO object.
   *
   * @param  communityDTO  the CommunityCreationDTO object to be converted
   * @param  coordinates   the coordinates of the community
   * @return               the converted CommunityDAO object
   */
  private static CommunityDAO communityCreationToDAO(
    CommunityCreationDTO communityDTO,
    Point coordinates
  ) {
    String uniqueID = NanoIdGenerator.generateNanoID();
    String safeDescription = SanitizationUtil.sanitize(
      communityDTO.description()
    );
    String safeName = SanitizationUtil.sanitize(communityDTO.name());
    String safeLocation = SanitizationUtil.sanitize(communityDTO.location());

    CommunityDAO community = new CommunityDAO();

    community.setCommunityID(uniqueID);
    community.setDescription(safeDescription);
    community.setLocation(safeLocation);
    community.setName(safeName);
    //TODO Validate that image belongs to you
    community.setImage(communityDTO.image());
    community.setVisibility(communityDTO.visibility());
    community.setCoordinates(coordinates);
    return community;
  }

  /**
   * Retrieves a list of nearby communities based on the provided coordinates and distance.
   *
   * @param  x                  the x-coordinate of the location
   * @param  y                  the y-coordinate of the location
   * @param  distanceInMeters   the maximum distance from the location in meters
   * @param  userID             the ID of the user
   * @return                    a list of CommunityDAO objects representing the nearby communities
   */
  private List<CommunityDAO> getNearbyCommunities(
    double x,
    double y,
    double distanceInMeters,
    String userID
  ) {
    return communityRepository.findNearbyCommunities(
      x,
      y,
      distanceInMeters,
      userID
    );
  }
}
