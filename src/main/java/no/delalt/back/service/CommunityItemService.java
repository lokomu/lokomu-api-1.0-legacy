package no.delalt.back.service;

import no.delalt.back.model.dao.CommunityDAO;
import no.delalt.back.model.dao.ItemDAO;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.model.dto.output.ItemDTO;
import no.delalt.back.model.enums.CommunityVisibilityEnum;
import no.delalt.back.repository.CommunityItemRepository;
import no.delalt.back.service.retrieval.CommunityItemRetrievalService;
import no.delalt.back.service.retrieval.UserCommunityRetrievalService;
import no.delalt.back.service.validation.CommunityValidationService;
import no.delalt.back.service.validation.UserCommunityValidationService;
import no.delalt.back.service.validation.UserValidationService;
import no.delalt.back.util.SecurityUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CommunityItemService {
  private final CommunityItemRepository communityItemRepository;
  private final UserCommunityRetrievalService userCommunityRetrievalService;
  private final UserValidationService userValidationService;
  private final CommunityValidationService communityValidationService;
  private final CommunityItemRetrievalService communityItemRetrievalService;
  private final UserCommunityValidationService userCommunityValidationService;

  public CommunityItemService(
    CommunityItemRepository communityItemRepository,
    UserCommunityRetrievalService userCommunityRetrievalService,
    UserValidationService userValidationService,
    CommunityValidationService communityValidationService,
    CommunityItemRetrievalService communityItemRetrievalService,
    UserCommunityValidationService userCommunityValidationService
  ) {
    this.communityItemRepository = communityItemRepository;
    this.userCommunityRetrievalService = userCommunityRetrievalService;
    this.userValidationService = userValidationService;
    this.communityValidationService = communityValidationService;
    this.communityItemRetrievalService = communityItemRetrievalService;
    this.userCommunityValidationService = userCommunityValidationService;
  }

  // -------------------- Controller Methods --------------------

  /**
   * Retrieves a list of ItemDTO objects representing items from communities that
   * other users are a part of, for the authenticated user.
   *
   * @return  a list of ItemDTO objects
   */
  @Transactional(readOnly = true)
  public List<ItemDTO> getOtherUserItemsFromUserCommunities() {
    UserDAO user = userValidationService.validateUserExistsAndReturn(
      SecurityUtil.getAuthenticatedAccountID()
    );

    List<CommunityDAO> userCommunities = userCommunityRetrievalService.getCommunitiesForUser(
      user
    );

    List<ItemDAO> itemDAOs = communityItemRepository.findOtherUserItemsFromCommunities(
      userCommunities,
      user
    );

    return itemDAOs.stream().map(ItemDTO::new).toList();
  }

  /**
   * Handles items in a community.
   *
   * @param  communityID  the ID of the community to retrieve items from
   * @return              a list of ItemDTO objects representing the items in the community
   */
  @Transactional(readOnly = true)
  public List<ItemDTO> handleItemsInCommunity(String communityID)
    throws ResponseStatusException {
    CommunityDAO communityDAO = communityValidationService.validateCommunityExistsAndReturn(
      communityID
    );

    if (
      (
        communityDAO.getVisibility() ==
        CommunityVisibilityEnum.OPEN.getValue() ||
        communityDAO.getVisibility() ==
        CommunityVisibilityEnum.CLOSED.getValue()
      ) &&
      !userCommunityValidationService.checkUserInCommunity(
        SecurityUtil.getAuthenticatedAccountID(),
        communityID
      )
    ) {
      throw new ResponseStatusException(
        HttpStatus.FORBIDDEN,
        "You do not have permission"
      );
    }

    List<ItemDAO> itemDAOS = communityItemRetrievalService.getAllItemsInCommunity(
      communityDAO
    );

    return itemDAOS.stream().map(ItemDTO::new).toList();
  }
  // -------------------- Helper Methods --------------------
}
