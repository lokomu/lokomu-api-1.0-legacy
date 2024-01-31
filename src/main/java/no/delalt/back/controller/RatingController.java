package no.delalt.back.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;
import no.delalt.back.configuration.validation.ValidID;
import no.delalt.back.model.dto.output.RatingsDTO;
import no.delalt.back.response.dto.RatingsResponse;
import no.delalt.back.service.RatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/rating")
public class RatingController {
  private final RatingService ratingService;

  public RatingController(RatingService ratingService) {
    this.ratingService = ratingService;
  }

  /**
   * Retrieves the ratings for a specific user.
   *
   * @param  userID  the ID of the user
   * @return         the response entity containing the ratings for the user
   */
  @Operation(summary = "Retrieves the ratings for a specific user")
  @GetMapping(path = "/ratings/user/{userID}", produces = "application/json")
  public ResponseEntity<RatingsResponse> getRatingsForUser(
    @PathVariable @NotBlank @ValidID String userID
  ) {
    RatingsDTO ratingsDTO = ratingService.retrieveRatingsForUser(userID);
    RatingsResponse ratingsResponse = new RatingsResponse(ratingsDTO);

    return ResponseEntity.ok(ratingsResponse);
  }
}
