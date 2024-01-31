package no.delalt.back.service.deletion;

import no.delalt.back.model.dao.BorrowAgreementDAO;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.repository.BorrowAgreementRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BorrowAgreementDeletionService {
  private final BorrowAgreementRepository borrowAgreementRepository;

  public BorrowAgreementDeletionService(
    BorrowAgreementRepository borrowAgreementRepository
  ) {
    this.borrowAgreementRepository = borrowAgreementRepository;
  }

  public void deleteBorrowAgreementsForOwner(UserDAO userDAO) {
    List<BorrowAgreementDAO> borrowAgreementDAOList = borrowAgreementRepository.findAllByOwner(
      userDAO
    );
    borrowAgreementRepository.deleteAll(borrowAgreementDAOList);
  }
}
