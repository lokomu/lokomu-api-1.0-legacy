package no.delalt.back.repository;

import no.delalt.back.model.dao.CommunityDAO;
import no.delalt.back.model.dao.GatheringDAO;
import no.delalt.back.model.dto.output.GatheringDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GatheringRepository
  extends JpaRepository<GatheringDAO, String> {
  @Query(
    "SELECT new no.delalt.back.model.dto.output.GatheringDTO(g.gatheringID, g.title, g.description, g.user, g.dateAndTime) " +
    "FROM GatheringDAO g WHERE g.community = :community AND g.isExpired = false AND g.dateAndTime > :dateTime"
  )
  List<GatheringDTO> retrieveUpcomingGatherings(
    @Param("community") CommunityDAO community,
    @Param("dateTime") LocalDateTime dateTime
  );
}
