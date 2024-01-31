package no.delalt.back.service.retrieval;

import no.delalt.back.model.dao.CommunityDAO;
import no.delalt.back.model.dao.UserCommunityDAO;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.repository.UserCommunityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserCommunityRetrievalService {
  private final UserCommunityRepository userCommunityRepository;

  public UserCommunityRetrievalService(
    UserCommunityRepository userCommunityRepository
  ) {
    this.userCommunityRepository = userCommunityRepository;
  }

  /**
   * Retrieves a list of UserCommunityDAO objects representing all the members in a community.
   *
   * @param  community  the CommunityDAO object representing the community
   * @return            a list of UserCommunityDAO objects representing all the members in the community
   */
  public List<UserCommunityDAO> findAllMembersInACommunityByCommunity(
    CommunityDAO community
  ) {
    return userCommunityRepository.findAllByCommunity(community);
  }

  /**
   * Retrieves a list of communities associated with a user.
   *
   * @param  user  the user for whom to retrieve the communities
   * @return       a list of CommunityDAO objects representing the communities associated with the user
   */
  public List<CommunityDAO> getCommunitiesForUser(UserDAO user) {
    return userCommunityRepository
      .findAllByUser(user)
      .stream()
      .map(UserCommunityDAO::getCommunity)
      .toList();
  }
}
