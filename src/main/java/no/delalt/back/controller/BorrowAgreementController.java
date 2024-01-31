package no.delalt.back.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import no.delalt.back.configuration.validation.ValidID;
import no.delalt.back.model.dto.input.RatingStatusDTO;
import no.delalt.back.model.dto.output.BorrowAgreementDTO;
import no.delalt.back.model.dto.output.BorrowDatesDTO;
import no.delalt.back.response.list.BorrowAgreementDatesListResponse;
import no.delalt.back.response.list.BorrowAgreementListResponse;
import no.delalt.back.service.BorrowAgreementService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@Validated
@RequestMapping("/borrow/agreement")
public class BorrowAgreementController {
  private final BorrowAgreementService borrowService;

  public BorrowAgreementController(BorrowAgreementService borrowService) {
    this.borrowService = borrowService;
  }

  /**
   * Modify the borrow status to ongoing for a specific agreement.
   *
   * @param  agreementID  the ID of the agreement
   * @return              a ResponseEntity with no body
   * @throws ResponseStatusException if an error occurs during the process
   */
  @Operation(
    summary = "Modify the borrow status to ongoing for a specific agreement"
  )
  @PutMapping(path = "/{agreementID}/status/ongoing")
  public ResponseEntity<Void> modifyBorrowStatusToOngoing(
    @PathVariable @ValidID @NotBlank String agreementID
  )
    throws ResponseStatusException {
    borrowService.setBorrowStatusToOngoing(agreementID);
    return ResponseEntity.ok().build();
  }

  /**
   * Modify the borrow status to returned for a specific agreement.
   *
   * @param  agreementID  the ID of the agreement
   * @return              a ResponseEntity with no body
   * @throws ResponseStatusException if an error occurs during the process
   */
  @Operation(
    summary = "Modify the borrow status to returned for a specific agreement"
  )
  @PutMapping(path = "/{agreementID}/status/returned")
  public ResponseEntity<Void> modifyBorrowStatusToReturned(
    @PathVariable @ValidID @NotBlank String agreementID
  )
    throws ResponseStatusException {
    borrowService.setBorrowStatusToReturned(agreementID);
    return ResponseEntity.ok().build();
  }

  /**
   * Modify the borrow status to completed for a specific agreement.
   *
   * @param  agreementID  the ID of the agreement
   * @return              a ResponseEntity with no body
   * @throws ResponseStatusException if an error occurs during the process
   */
  @Operation(
    summary = "Modify the borrow status to completed for a specific agreement"
  )
  @PutMapping(
    path = "/{agreementID}/status/completed",
    consumes = "application/json"
  )
  public ResponseEntity<Void> modifyBorrowStatusToCompleted(
    @PathVariable @ValidID @NotBlank String agreementID,
    @RequestBody @Valid RatingStatusDTO ratingStatusDTO
  )
    throws ResponseStatusException {
    borrowService.setBorrowStatusToCompleted(agreementID, ratingStatusDTO);
    return ResponseEntity.ok().build();
  }

  /**
   * Retrieve a list of all incomplete borrow agreements for owner.
   *
   * @return              a ResponseEntity with the list of borrow agreements
   * @throws ResponseStatusException if an error occurs during the process
   */
  @Operation(
    summary = "Retrieve a list of all incomplete borrow agreements for owner"
  )
  @GetMapping(path = "/incomplete/owner", produces = "application/json")
  public ResponseEntity<BorrowAgreementListResponse> getIncompleteBorrowsForOwner()
    throws ResponseStatusException {
    List<BorrowAgreementDTO> borrowDTOs = borrowService.retrieveIncompleteBorrowsForOwner();
    BorrowAgreementListResponse borrowAgreementListResponse = new BorrowAgreementListResponse(
      borrowDTOs
    );
    return ResponseEntity.ok(borrowAgreementListResponse);
  }

  /**
   * Retrieve a list of all incomplete borrow agreements for borrower.
   *
   * @return              a ResponseEntity with the list of borrow agreements
   * @throws ResponseStatusException if an error occurs during the process
   */
  @Operation(
    summary = "Retrieve a list of all incomplete borrow agreements for borrower"
  )
  @GetMapping(path = "/incomplete/borrower", produces = "application/json")
  public ResponseEntity<BorrowAgreementListResponse> getIncompleteBorrowsForBorrower()
    throws ResponseStatusException {
    List<BorrowAgreementDTO> borrowDTOs = borrowService.retrieveIncompleteBorrowsForBorrower();
    BorrowAgreementListResponse borrowAgreementListResponse = new BorrowAgreementListResponse(
      borrowDTOs
    );
    return ResponseEntity.ok(borrowAgreementListResponse);
  }

  /**
   * Retrieves the list of completed borrow agreements for the owner.
   *
   * @return         	Response entity containing the list of completed borrow agreements
   * @throws ResponseStatusException	if there is an error retrieving the borrow agreements
   */
  @Operation(
    summary = "Retrieves the list of completed borrow agreements for the owner"
  )
  @GetMapping(path = "/completed/owner", produces = "application/json")
  public ResponseEntity<BorrowAgreementListResponse> getCompletedBorrowsForOwner()
    throws ResponseStatusException {
    List<BorrowAgreementDTO> borrowDTOs = borrowService.retrieveCompletedBorrowsForOwner();
    BorrowAgreementListResponse borrowAgreementListResponse = new BorrowAgreementListResponse(
      borrowDTOs
    );
    return ResponseEntity.ok(borrowAgreementListResponse);
  }

  /**
   * Retrieves the list of completed borrow agreements for the borrower.
   *
   * @return         	Response entity containing the list of completed borrow agreements
   * @throws ResponseStatusException	if there is an error retrieving the borrow agreements
   */
  @Operation(
    summary = "Retrieves the list of completed borrow agreements for the borrower"
  )
  @GetMapping(path = "/completed/borrower", produces = "application/json")
  public ResponseEntity<BorrowAgreementListResponse> getCompletedBorrowsForBorrower()
    throws ResponseStatusException {
    List<BorrowAgreementDTO> borrowDTOs = borrowService.retrieveCompletedBorrowsForBorrower();
    BorrowAgreementListResponse borrowAgreementListResponse = new BorrowAgreementListResponse(
      borrowDTOs
    );
    return ResponseEntity.ok(borrowAgreementListResponse);
  }

  /**
   * Retrieves the list of unavailable times for an item.
   *
   * @return Response entity containing the list of unavailable times
   * @throws ResponseStatusException if there is an error retrieving the unavailable times
   */
  @Operation(summary = "Retrieves the list of unavailable times for an item")
  @GetMapping(path = "/times/item/{itemID}", produces = "application/json")
  public ResponseEntity<BorrowAgreementDatesListResponse> getBorrowTimesForItem(
    @PathVariable @ValidID @NotBlank String itemID
  ) {
    List<BorrowDatesDTO> unavailableTimesDTOs = borrowService.retrieveBorrowDatesForItem(
      itemID
    );
    BorrowAgreementDatesListResponse borrowAgreementDatesListResponse = new BorrowAgreementDatesListResponse(
      unavailableTimesDTOs
    );
    return ResponseEntity.ok(borrowAgreementDatesListResponse);
  }
}
