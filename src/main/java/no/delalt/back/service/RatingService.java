package no.delalt.back.service;

import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.model.dto.output.RatingsDTO;
import no.delalt.back.repository.RatingRepository;
import no.delalt.back.service.validation.UserValidationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RatingService {
  private final RatingRepository ratingRepository;
  private final UserValidationService userValidationService;

  public RatingService(
    RatingRepository ratingRepository,
    UserValidationService userValidationService
  ) {
    this.ratingRepository = ratingRepository;
    this.userValidationService = userValidationService;
  }

  //TODO Validate the community and isRequest
  /**
   * Retrieves the ratings summary for a user.
   *
   * @param  userID  the ID of the user
   * @return         the ratings summary for the user
   */
  @Transactional(readOnly = true)
  public RatingsDTO retrieveRatingsForUser(String userID) {
    userValidationService.validateAuthenticatedUserIsDifferent(userID);
    UserDAO userDAO = userValidationService.validateUserExistsAndReturn(userID);

    return ratingRepository.getRatingsSummaryForBorrower(userDAO);
  }
}
