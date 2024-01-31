package no.delalt.back.repository;

import no.delalt.back.model.dao.BorrowAgreementDAO;
import no.delalt.back.model.dao.ItemDAO;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.model.dto.output.BorrowDatesDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowAgreementRepository
  extends JpaRepository<BorrowAgreementDAO, String> {
  List<BorrowAgreementDAO> findAllByOwnerAndIsCompleted(
    UserDAO owner,
    boolean isCompleted
  );
  List<BorrowAgreementDAO> findAllByBorrowerAndIsCompleted(
    UserDAO borrower,
    boolean isCompleted
  );
  boolean existsByItemAndIsCompletedAndStatus(
    ItemDAO item,
    boolean isCompleted,
    int status
  );

  @Query(
    "SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM BorrowAgreementDAO b WHERE b.item = :item AND b.endDate >= :startDate AND b.startDate <= :endDate"
  )
  boolean existsByItemAndTimeframe(
    @Param("item") ItemDAO item,
    @Param("startDate") LocalDate startDate,
    @Param("endDate") LocalDate endDate
  );

  @Query(
    "SELECT new no.delalt.back.model.dto.output.BorrowDatesDTO(b.startDate, b.endDate) FROM BorrowAgreementDAO b WHERE b.item = :item AND b.endDate >= :currentDate"
  )
  List<BorrowDatesDTO> findAllBorrowDatesForItem(
    @Param("item") ItemDAO item,
    @Param("currentDate") LocalDate currentDate
  );

  List<BorrowAgreementDAO> findAllByOwner(UserDAO userDAO);
}
