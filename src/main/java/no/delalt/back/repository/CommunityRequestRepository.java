package no.delalt.back.repository;

import no.delalt.back.model.dao.CommunityDAO;
import no.delalt.back.model.dao.CommunityRequestDAO;
import no.delalt.back.model.dao.UserDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityRequestRepository
  extends JpaRepository<CommunityRequestDAO, String> {
  CommunityRequestDAO findByCommunityAndUser(
    CommunityDAO community,
    UserDAO user
  );
  List<CommunityRequestDAO> findAllByCommunity(CommunityDAO community);
  List<CommunityRequestDAO> findAllByUser(UserDAO user);
}
