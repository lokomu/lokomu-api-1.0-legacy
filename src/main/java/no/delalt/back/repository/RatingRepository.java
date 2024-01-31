package no.delalt.back.repository;

import no.delalt.back.model.dao.RatingDAO;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.model.dto.output.RatingsDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<RatingDAO, String> {
  @Query(
    "SELECT new no.delalt.back.model.dto.output.RatingsDTO(" +
    "COUNT(DISTINCT r.owner.userID) AS userAmount, " +
    "COALESCE(SUM(CASE WHEN r.status = 0 THEN 1 ELSE 0 END), 0) AS sameConditionAmount, " +
    "COALESCE(SUM(CASE WHEN r.status = 1 THEN 1 ELSE 0 END), 0) AS betterConditionAmount, " +
    "COALESCE(SUM(CASE WHEN r.status = 2 THEN 1 ELSE 0 END), 0) AS worseConditionAmount, " +
    "COALESCE(SUM(CASE WHEN r.status = 3 THEN 1 ELSE 0 END), 0) AS lateReturnAmount, " +
    "COALESCE(SUM(CASE WHEN r.status = 4 THEN 1 ELSE 0 END), 0) AS notReturnedAmount ) " +
    "FROM RatingDAO r WHERE r.borrower = :user"
  )
  RatingsDTO getRatingsSummaryForBorrower(@Param("user") UserDAO user);

  List<RatingDAO> findAllByOwner(UserDAO userDAO);
}
