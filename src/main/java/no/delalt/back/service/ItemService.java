package no.delalt.back.service;

import no.delalt.back.model.dao.CommunityDAO;
import no.delalt.back.model.dao.CommunityItemDAO;
import no.delalt.back.model.dao.ImageDAO;
import no.delalt.back.model.dao.ItemDAO;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.model.dto.input.ItemCreationDTO;
import no.delalt.back.model.dto.input.ItemModifyDTO;
import no.delalt.back.model.dto.output.ItemDTO;
import no.delalt.back.model.dto.output.ItemOwnerDTO;
import no.delalt.back.repository.ItemRepository;
import no.delalt.back.service.deletion.CommunityItemDeletionService;
import no.delalt.back.service.deletion.ImageDeletionService;
import no.delalt.back.service.retrieval.CommunityItemRetrievalService;
import no.delalt.back.service.retrieval.ImageRetrievalService;
import no.delalt.back.service.save.CommunityItemSaveService;
import no.delalt.back.service.save.ItemSaveService;
import no.delalt.back.service.validation.CommunityValidationService;
import no.delalt.back.service.validation.ItemValidationService;
import no.delalt.back.service.validation.UserCommunityValidationService;
import no.delalt.back.service.validation.UserValidationService;
import no.delalt.back.util.NanoIdGenerator;
import no.delalt.back.util.SanitizationUtil;
import no.delalt.back.util.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ItemService {
  private final ItemRepository itemRepository;
  private final ItemValidationService itemValidationService;
  private final ItemSaveService itemSaveService;

  private final CommunityItemDeletionService communityItemDeletionService;
  private final CommunityItemSaveService communityItemSaveService;
  private final CommunityItemRetrievalService communityItemRetrievalService;

  private final ImageRetrievalService imageRetrievalService;
  private final ImageDeletionService imageDeletionService;

  private final CommunityValidationService communityValidationService;
  private final UserCommunityValidationService userCommunityValidationService;
  private final UserValidationService userValidationService;

  public ItemService(
    ItemRepository itemRepository,
    ItemValidationService itemValidationService,
    ItemSaveService itemSaveService,
    CommunityItemDeletionService communityItemDeletionService,
    CommunityItemSaveService communityItemSaveService,
    CommunityItemRetrievalService communityItemRetrievalService,
    ImageRetrievalService imageRetrievalService,
    ImageDeletionService imageDeletionService,
    CommunityValidationService communityValidationService,
    UserCommunityValidationService userCommunityValidationService,
    UserValidationService userValidationService
  ) {
    this.itemRepository = itemRepository;
    this.itemValidationService = itemValidationService;
    this.itemSaveService = itemSaveService;
    this.communityItemDeletionService = communityItemDeletionService;
    this.communityItemSaveService = communityItemSaveService;
    this.communityItemRetrievalService = communityItemRetrievalService;
    this.imageRetrievalService = imageRetrievalService;
    this.imageDeletionService = imageDeletionService;
    this.communityValidationService = communityValidationService;
    this.userCommunityValidationService = userCommunityValidationService;
    this.userValidationService = userValidationService;
  }

  // -------------------- Controller Methods --------------------

  /**
   * Creates an item based on the given ItemCreationDTO.
   *
   * @param  itemDTO  the ItemCreationDTO containing the item details
   * @return          the unique ID of the created item
   */
  @Transactional
  public String createItem(ItemCreationDTO itemDTO, boolean isGiven) {
    UserDAO currentUserDAO = userValidationService.validateUserExistsAndReturn(
      SecurityUtil.getAuthenticatedAccountID()
    );

    ItemDAO item = new ItemDAO();

    if (itemDTO.image() != null) {
      String imageID = itemDTO.image();
      ImageDAO imageDAO = imageRetrievalService.getImage(imageID);
      userValidationService.validateAuthenticatedUserIsSame(
        imageDAO.getUser().getUserID()
      );
      item.setImage(imageID);
    }

    String uniqueID = NanoIdGenerator.generateNanoID();
    String safeTitle = SanitizationUtil.sanitize(itemDTO.title());
    String safeDescription = SanitizationUtil.sanitize(itemDTO.description());

    item.setItemID(uniqueID);
    item.setIsDeleted(false);
    item.setTitle(safeTitle);
    item.setDescription(safeDescription);
    item.setUser(currentUserDAO);
    item.setIsForGiving(isGiven);

    //TODO Dont let the user create more than 100 items

    itemSaveService.saveItem(item);

    for (String communityID : itemDTO.communityIDs()) {
      CommunityDAO communityDAO = communityValidationService.validateCommunityExistsAndReturn(
        communityID
      );
      userCommunityValidationService.validateCurrentUserIsMemberOfCommunity(
        communityDAO.getCommunityID()
      );
      communityItemSaveService.saveCommunityItem(communityDAO, item);
    }

    return uniqueID;
  }

  /**
   * Retrieves an ItemOwnerDTO for a given item ID.
   *
   * @param  itemID  the ID of the item
   * @return         the ItemOwnerDTO object representing the item and its owner
   */
  @Transactional(readOnly = true)
  public ItemOwnerDTO getItemByIDForOwner(String itemID) {
    ItemDAO itemDAO = itemValidationService.validateItemExistsAndReturn(itemID);
    userValidationService.validateAuthenticatedUserIsSame(
      itemDAO.getUser().getUserID()
    );

    List<String> communityIDs = communityItemRetrievalService.getCommunityItemIDsByItemID(
      itemDAO
    );

    return new ItemOwnerDTO(itemDAO, communityIDs.toArray(new String[0]));
  }

  /**
   * Retrieves all items for the active user.
   *
   * @return a list of ItemDTO objects representing the items
   *         associated with the active user
   */
  @Transactional(readOnly = true)
  public List<ItemDTO> getAllItemsForActiveUser() {
    UserDAO user = userValidationService.validateUserExistsAndReturn(
      SecurityUtil.getAuthenticatedAccountID()
    );
    List<ItemDAO> itemDAOs = itemRepository.findAllByUserAndIsDeletedIsFalse(
      user
    );
    return itemDAOs.stream().map(ItemDTO::new).toList();
  }

  /**
   * Updates an item based on the given ItemModifyDTO.
   *
   * @param  itemID   the ID of the item to update
   * @param  itemDTO  the ItemModifyDTO containing the new item details
   */
  @Transactional
  public void updateItem(String itemID, ItemModifyDTO itemDTO) {
    ItemDAO item = itemValidationService.validateItemExistsAndReturn(itemID);

    itemValidationService.validateUserOwnsItem(item);

    String safeTitle = SanitizationUtil.sanitize(itemDTO.title());
    String safeDescription = SanitizationUtil.sanitize(itemDTO.description());

    item.setTitle(safeTitle);
    item.setDescription(safeDescription);

    if (!Objects.equals(itemDTO.image(), item.getImage())) {
      if (itemDTO.image() == null) {
        imageDeletionService.deleteImage(item.getImage());
      } else if (item.getImage() == null) {
        userValidationService.validateAuthenticatedUserIsSame(
          imageRetrievalService.getImage(itemDTO.image()).getUser().getUserID()
        );
      }
      item.setImage(itemDTO.image());
    }

    itemSaveService.saveItem(item);
    //TODO Shouldnt delete all with item
    //TODO Check that the communities are not empty
    communityItemDeletionService.deleteAllWithItem(item);

    for (String communityID : itemDTO.communityIDs()) {
      CommunityDAO communityDAO = communityValidationService.validateCommunityExistsAndReturn(
        communityID
      );
      userCommunityValidationService.validateCurrentUserIsMemberOfCommunity(
        communityDAO.getCommunityID()
      );
      communityItemSaveService.saveCommunityItem(communityDAO, item);
    }

    updateItemCommunities(item, itemDTO);
  }

  // -------------------- Helper Methods --------------------

  /**
   * Updates the communities associated with an item.
   *
   * @param  item           the item to be updated
   * @param  itemModifyDTO  the DTO containing the modified item data
   */
  private void updateItemCommunities(
    ItemDAO item,
    ItemModifyDTO itemModifyDTO
  ) {
    List<CommunityItemDAO> communityItemDAOs = communityItemRetrievalService.getAllCommunityItemForItem(
      item
    );
    Set<String> currentCommunityIds = extractCommunityIds(communityItemDAOs);
    Set<String> newCommunityIds = new HashSet<>(
      Arrays.asList(itemModifyDTO.communityIDs())
    );

    Set<String> toAdd = new HashSet<>(newCommunityIds);
    toAdd.removeAll(currentCommunityIds);

    Set<String> toRemove = new HashSet<>(currentCommunityIds);
    toRemove.removeAll(newCommunityIds);

    Map<String, CommunityItemDAO> communityItemMap = communityItemDAOs
      .stream()
      .collect(
        Collectors.toMap(
          communityItem -> communityItem.getCommunity().getCommunityID(),
          Function.identity()
        )
      );

    processRemovals(toRemove, communityItemMap);
    processAdditions(toAdd, item);
  }

  /**
   * Extracts the community IDs from a list of CommunityItemDAO objects.
   *
   * @param  communityItemDAOs  the list of CommunityItemDAO objects
   * @return                    the set of community IDs
   */
  private Set<String> extractCommunityIds(
    List<CommunityItemDAO> communityItemDAOs
  ) {
    return communityItemDAOs
      .stream()
      .map(communityItem -> communityItem.getCommunity().getCommunityID())
      .collect(Collectors.toSet());
  }

  /**
   * Processes the removals of communities from an item.
   *
   * @param  toRemove  the set of community IDs to remove
   * @param  communityItemMap  the map of community IDs to CommunityItemDAO objects
   */
  private void processRemovals(
    Set<String> toRemove,
    Map<String, CommunityItemDAO> communityItemMap
  ) {
    toRemove.forEach(
      communityIdToRemove -> {
        CommunityItemDAO communityItemDAO = communityItemMap.get(
          communityIdToRemove
        );
        if (communityItemDAO != null) {
          communityItemDeletionService.deleteCommunityItem(communityItemDAO);
        }
      }
    );
  }

  /**
   * Processes the additions of communities to an item.
   *
   * @param  toAdd  the set of community IDs to add
   * @param  item   the item to add the communities to
   */
  private void processAdditions(Set<String> toAdd, ItemDAO item) {
    toAdd.forEach(
      communityIdToAdd -> {
        CommunityDAO communityDAO = communityValidationService.validateCommunityExistsAndReturn(
          communityIdToAdd
        );
        userCommunityValidationService.validateCurrentUserIsMemberOfCommunity(
          communityDAO.getCommunityID()
        );
        communityItemSaveService.saveCommunityItem(communityDAO, item);
      }
    );
  }
}
