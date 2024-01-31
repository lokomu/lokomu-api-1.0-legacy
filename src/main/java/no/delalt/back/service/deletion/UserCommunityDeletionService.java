package no.delalt.back.service.deletion;

import no.delalt.back.model.dao.UserCommunityDAO;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.repository.UserCommunityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserCommunityDeletionService {
  private final UserCommunityRepository userCommunityRepository;
  private final CommunityItemDeletionService communityItemDeletionService;

  public UserCommunityDeletionService(
    UserCommunityRepository userCommunityRepository,
    CommunityItemDeletionService communityItemDeletionService
  ) {
    this.userCommunityRepository = userCommunityRepository;
    this.communityItemDeletionService = communityItemDeletionService;
  }

  /**
   * Deletes a user from the community.
   *
   * @param  ucd  the UserCommunityDAO representing the user to be deleted
   */
  public void deleteUserFromCommunity(UserCommunityDAO ucd) {
    communityItemDeletionService.removeUserItemsFromCommunity(ucd);

    userCommunityRepository.delete(ucd);
  }

  /**
   * Removes all items in communities and deletes a user from all groups.
   *
   * @param  user  the UserDAO representing the user to be deleted
   */
  public void deleteUserFromAllGroups(UserDAO user) {
    List<UserCommunityDAO> communities = userCommunityRepository.findAllByUser(
      user
    );

    for (UserCommunityDAO ucd : communities) {
      communityItemDeletionService.removeUserItemsFromCommunity(ucd);
    }

    //TODO Check if they are the only member or if they are the only admin

    userCommunityRepository.deleteAll(communities);
  }
}
