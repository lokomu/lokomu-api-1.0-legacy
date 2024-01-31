package no.delalt.back.service.validation;

import no.delalt.back.model.id.CommunityItemID;
import no.delalt.back.repository.CommunityItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CommunityItemValidationService {
  private final CommunityItemRepository communityItemRepository;

  public CommunityItemValidationService(
    CommunityItemRepository communityItemRepository
  ) {
    this.communityItemRepository = communityItemRepository;
  }

  /**
   * Validates if an item exists in a community.
   *
   * @param  communityID  the ID of the community
   * @param  itemID       the ID of the item
   * @throws ResponseStatusException  if the item does not exist in the community
   */
  public void validateItemExistsInCommunity(String communityID, String itemID)
    throws ResponseStatusException {
    if (
      !communityItemRepository.existsById(
        new CommunityItemID(communityID, itemID)
      )
    ) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Item not in community"
      );
    }
  }
}
