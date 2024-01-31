package no.delalt.back.service.validation;

import java.time.LocalDate;
import no.delalt.back.model.dao.ItemDAO;
import no.delalt.back.repository.BorrowAgreementRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BorrowAgreementValidationService {
  private final BorrowAgreementRepository borrowAgreementRepository;

  public BorrowAgreementValidationService(
    BorrowAgreementRepository borrowAgreementRepository
  ) {
    this.borrowAgreementRepository = borrowAgreementRepository;
  }

  /**
   * Validates whether there are no existing borrows for a specific item within a given timeframe.
   *
   * @param  item       the item to validate
   * @param  startDate  the start date of the timeframe
   * @param  endDate    the end date of the timeframe
   * @throws ResponseStatusException if there are existing borrows in the timeframe
   */
  public void validateNoBorrowsInTimeframe(
    ItemDAO item,
    LocalDate startDate,
    LocalDate endDate
  ) {
    if (
      borrowAgreementRepository.existsByItemAndTimeframe(
        item,
        startDate,
        endDate
      )
    ) {
      throw new ResponseStatusException(
        HttpStatus.CONFLICT,
        "A borrow already exists for this item in the timeframe."
      );
    }
  }
}
