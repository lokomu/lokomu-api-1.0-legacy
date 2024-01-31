package no.delalt.back.repository;

import no.delalt.back.model.dao.GatheringDAO;
import no.delalt.back.model.dao.UserGatheringDAO;
import no.delalt.back.model.id.UserGatheringID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGatheringRepository
  extends JpaRepository<UserGatheringDAO, UserGatheringID> {
  @Query(
    "SELECT COUNT(ug) FROM UserGatheringDAO ug WHERE ug.gathering = :gathering"
  )
  int getNumberOfAttendees(@Param("gathering") GatheringDAO gathering);
}
