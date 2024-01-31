package no.delalt.back.repository;

import no.delalt.back.model.dao.CommunityDAO;
import no.delalt.back.model.dao.CommunityItemDAO;
import no.delalt.back.model.dao.ItemDAO;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.model.id.CommunityItemID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityItemRepository
  extends JpaRepository<CommunityItemDAO, CommunityItemID> {
  List<CommunityItemDAO> findAllByItem(ItemDAO item);
  List<CommunityItemDAO> findAllByCommunity(CommunityDAO community);
  List<CommunityItemDAO> findAllByCommunityAndItem_User(
    CommunityDAO community,
    UserDAO user
  );

  //TODO Make this method retrieve the user's communities itself
  @Query(
    "SELECT DISTINCT i " +
    "FROM ItemDAO i " +
    "JOIN CommunityItemDAO ci ON i = ci.item " +
    "WHERE ci.community IN :communities AND i.user <> :user"
  )
  List<ItemDAO> findOtherUserItemsFromCommunities(
    @Param("communities") List<CommunityDAO> communities,
    @Param("user") UserDAO user
  );
}
