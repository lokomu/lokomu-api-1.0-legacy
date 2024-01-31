package no.delalt.back.repository;

import no.delalt.back.model.dao.CommunityDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityRepository
  extends JpaRepository<CommunityDAO, String> {
  @Query(
    value = "SELECT c.* FROM public.community c WHERE c.coordinates IS NOT NULL AND " +
    "ST_DWithin(ST_GeographyFromText('SRID=4326;POINT(' || :x || ' ' || :y || ')'), c.coordinates, :distanceInMeters) AND " +
    "NOT EXISTS (SELECT 1 FROM user_community uc WHERE uc.community_id = c.community_id AND uc.user_id = :userID)",
    nativeQuery = true
  )
  List<CommunityDAO> findNearbyCommunities(
    @Param("x") double x,
    @Param("y") double y,
    @Param("distanceInMeters") double distanceInMeters,
    @Param("userID") String userID
  );

  @Query(
    value = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END FROM community c WHERE c.community_id = :communityID AND ST_DWithin(ST_GeographyFromText('SRID=4326;POINT(' || :x || ' ' || :y || ')'), c.coordinates, :distanceInMeters)",
    nativeQuery = true
  )
  boolean isUserNearCommunity(
    @Param("x") double x,
    @Param("y") double y,
    @Param("distanceInMeters") double distanceInMeters,
    @Param("communityID") String communityID
  );
}
