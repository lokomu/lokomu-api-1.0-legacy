package no.delalt.back.service.deletion;

import no.delalt.back.model.dao.RatingDAO;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.repository.RatingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingDeletionService {
  private final RatingRepository ratingRepository;

  public RatingDeletionService(RatingRepository ratingRepository) {
    this.ratingRepository = ratingRepository;
  }

  public void deleteRatingsForOwner(UserDAO userDAO) {
    List<RatingDAO> ratingDAOList = ratingRepository.findAllByOwner(userDAO);
    ratingRepository.deleteAll(ratingDAOList);
  }
}
