package no.delalt.back.service.save;

import no.delalt.back.model.dao.BorrowAgreementDAO;
import no.delalt.back.model.dao.RatingDAO;
import no.delalt.back.model.dto.input.RatingStatusDTO;
import no.delalt.back.repository.RatingRepository;
import no.delalt.back.util.NanoIdGenerator;
import org.springframework.stereotype.Service;

@Service
public class RatingSaveService {
  private final RatingRepository ratingRepository;

  public RatingSaveService(RatingRepository ratingRepository) {
    this.ratingRepository = ratingRepository;
  }

  /**
   * Saves the given `BorrowAgreementDAO` object to the database.
   *
   * @param  borrowAgreementDAO  the `BorrowAgreementDAO` object to be saved
   */
  public void saveRating(
    BorrowAgreementDAO borrowAgreementDAO,
    RatingStatusDTO ratingStatusDTO
  ) {
    RatingDAO ratingDAO = new RatingDAO();

    ratingDAO.setRatingID(NanoIdGenerator.generateNanoID());
    ratingDAO.setBorrowAgreement(borrowAgreementDAO);
    ratingDAO.setBorrower(borrowAgreementDAO.getBorrower());
    ratingDAO.setOwner(borrowAgreementDAO.getOwner());
    ratingDAO.setStatus(ratingStatusDTO.status());

    ratingRepository.save(ratingDAO);
  }
}
