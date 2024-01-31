package no.delalt.back.repository;

import no.delalt.back.model.dao.BorrowRequestDAO;
import no.delalt.back.model.dao.ItemDAO;
import no.delalt.back.model.dao.UserDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowRequestRepository
  extends JpaRepository<BorrowRequestDAO, String> {
  List<BorrowRequestDAO> findAllByOwnerAndIsProcessed(
    UserDAO owner,
    boolean isProcessed
  );
  List<BorrowRequestDAO> findAllByOwnerAndIsProcessedAndStatus(
    UserDAO owner,
    boolean isProcessed,
    int status
  );
  List<BorrowRequestDAO> findAllByRequesterAndIsProcessed(
    UserDAO requester,
    boolean isProcessed
  );
  List<BorrowRequestDAO> findAllByRequesterAndIsProcessedAndStatus(
    UserDAO requester,
    boolean isProcessed,
    int status
  );
  boolean existsByRequesterAndIsProcessedAndItem(
    UserDAO requester,
    boolean isProcessed,
    ItemDAO item
  );

  @Query(
    "SELECT br FROM BorrowRequestDAO br WHERE br.item = :item AND br.startDate <= :endDate AND br.endDate >= :startDate AND br.isProcessed = false"
  )
  List<BorrowRequestDAO> findPendingRequestsByItemInTimeframe(
    @Param("item") ItemDAO item,
    @Param("startDate") LocalDate startDate,
    @Param("endDate") LocalDate endDate
  );
}
