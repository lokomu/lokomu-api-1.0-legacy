package no.delalt.back.service.deletion;

import no.delalt.back.model.dao.BorrowRequestDAO;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.model.enums.BorrowRequestStatusEnum;
import no.delalt.back.repository.BorrowRequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BorrowRequestDeletionService {
  private final BorrowRequestRepository borrowRequestRepository;

  public BorrowRequestDeletionService(
    BorrowRequestRepository borrowRequestRepository
  ) {
    this.borrowRequestRepository = borrowRequestRepository;
  }

  public void deleteUnprocessedRequestsForUser(UserDAO userDAO) {
    deleteUnprocessedRequestsForRequester(userDAO);
    deleteUnprocessedRequestsForOwner(userDAO);
  }

  public void deleteProcessedButNotAcceptedRequestsForUser(UserDAO userDAO) {
    deleteProcessedButNotAcceptedRequestsForRequester(userDAO);
    deleteProcessedButNotAcceptedRequestsForOwner(userDAO);
  }

  public void deleteProcessedAcceptedForOwner(UserDAO userDAO) {
    List<BorrowRequestDAO> borrowRequestDAOList = borrowRequestRepository.findAllByOwnerAndIsProcessedAndStatus(
      userDAO,
      true,
      BorrowRequestStatusEnum.ACCEPTED.getValue()
    );
    borrowRequestRepository.deleteAll(borrowRequestDAOList);
  }

  // -------------------- Helper Methods --------------------

  private void deleteUnprocessedRequestsForRequester(UserDAO userDAO) {
    List<BorrowRequestDAO> borrowRequestDAOList = borrowRequestRepository.findAllByRequesterAndIsProcessed(
      userDAO,
      false
    );
    borrowRequestRepository.deleteAll(borrowRequestDAOList);
  }

  private void deleteUnprocessedRequestsForOwner(UserDAO userDAO) {
    List<BorrowRequestDAO> borrowRequestDAOList = borrowRequestRepository.findAllByOwnerAndIsProcessed(
      userDAO,
      false
    );
    borrowRequestRepository.deleteAll(borrowRequestDAOList);
  }

  private void deleteProcessedButNotAcceptedRequestsForOwner(UserDAO userDAO) {
    List<BorrowRequestDAO> rejectedBorrowRequestDAOList = borrowRequestRepository.findAllByOwnerAndIsProcessedAndStatus(
      userDAO,
      true,
      BorrowRequestStatusEnum.REJECTED.getValue()
    );
    List<BorrowRequestDAO> canceledBorrowRequestDAOList = borrowRequestRepository.findAllByOwnerAndIsProcessedAndStatus(
      userDAO,
      true,
      BorrowRequestStatusEnum.CANCELED.getValue()
    );
    borrowRequestRepository.deleteAll(rejectedBorrowRequestDAOList);
    borrowRequestRepository.deleteAll(canceledBorrowRequestDAOList);
  }

  private void deleteProcessedButNotAcceptedRequestsForRequester(
    UserDAO userDAO
  ) {
    List<BorrowRequestDAO> rejectedBorrowRequestDAOList = borrowRequestRepository.findAllByRequesterAndIsProcessedAndStatus(
      userDAO,
      true,
      BorrowRequestStatusEnum.REJECTED.getValue()
    );
    List<BorrowRequestDAO> canceledBorrowRequestDAOList = borrowRequestRepository.findAllByRequesterAndIsProcessedAndStatus(
      userDAO,
      true,
      BorrowRequestStatusEnum.CANCELED.getValue()
    );
    borrowRequestRepository.deleteAll(rejectedBorrowRequestDAOList);
    borrowRequestRepository.deleteAll(canceledBorrowRequestDAOList);
  }
}
