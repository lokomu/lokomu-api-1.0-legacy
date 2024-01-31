package no.delalt.back.repository;

import no.delalt.back.model.dao.CommunityDAO;
import no.delalt.back.model.dao.UserCommunityDAO;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.model.id.UserCommunityID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCommunityRepository
  extends JpaRepository<UserCommunityDAO, UserCommunityID> {
  int countByCommunityAndIsAdministratorTrue(CommunityDAO community);
  int countByCommunity(CommunityDAO community);
  boolean existsByUser_UserIDAndCommunity_CommunityIDAndIsAdministratorTrue(
    String userID,
    String communityID
  );
  List<UserCommunityDAO> findAllByUser(UserDAO user);
  List<UserCommunityDAO> findAllByCommunity(CommunityDAO community);
  UserCommunityDAO findByCommunityAndIsAdministratorTrue(
    CommunityDAO community
  );
  List<UserCommunityDAO> findByUserAndIsAdministratorTrue(UserDAO user);
}
