package no.delalt.back.service.save;

import no.delalt.back.model.dao.BorrowAgreementDAO;
import no.delalt.back.repository.BorrowAgreementRepository;
import org.springframework.stereotype.Service;

@Service
public class BorrowAgreementSaveService {
  private final BorrowAgreementRepository borrowAgreementRepository;

  public BorrowAgreementSaveService(
    BorrowAgreementRepository borrowAgreementRepository
  ) {
    this.borrowAgreementRepository = borrowAgreementRepository;
  }

  /**
   * Saves the given `BorrowAgreementDAO` object to the database.
   *
   * @param  borrowAgreementDAO  the `BorrowAgreementDAO` object to be saved
   */
  public void saveAgreement(BorrowAgreementDAO borrowAgreementDAO) {
    borrowAgreementRepository.save(borrowAgreementDAO);
  }
}
