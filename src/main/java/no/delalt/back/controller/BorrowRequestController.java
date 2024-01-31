package no.delalt.back.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import no.delalt.back.configuration.validation.ValidID;
import no.delalt.back.model.dto.input.BorrowRequestCreationDTO;
import no.delalt.back.model.dto.output.BorrowRequestDTO;
import no.delalt.back.response.list.BorrowRequestListResponse;
import no.delalt.back.service.BorrowRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@Validated
@RequestMapping("/borrow/request")
public class BorrowRequestController {
  private final BorrowRequestService borrowRequestService;

  public BorrowRequestController(BorrowRequestService borrowRequestService) {
    this.borrowRequestService = borrowRequestService;
  }

  /**
   * Creates a new borrow request.
   *
   * @param  requestCreationDTO  the data transfer object containing the information needed to create the borrow request
   * @return                     a ResponseEntity with a void body indicating the success of the operation
   * @throws ResponseStatusException if an error occurs while creating the borrow request
   */
  @Operation(summary = "Creates a new borrow request")
  @PostMapping(path = "/", consumes = "application/json")
  public ResponseEntity<Void> createBorrowRequest(
    @RequestBody @Valid BorrowRequestCreationDTO requestCreationDTO
  )
    throws ResponseStatusException {
    borrowRequestService.createBorrowRequest(requestCreationDTO);

    return ResponseEntity.ok().build();
  }

  /**
   * Retrieves the pending borrow requests for the owner.
   *
   * @return         	A ResponseEntity containing the list of pending borrow requests
   * @throws ResponseStatusException	if there is an exception while retrieving the requests
   */
  @Operation(summary = "Retrieves the pending borrow requests for the owner")
  @GetMapping(path = "/pending/owner", produces = "application/json")
  public ResponseEntity<BorrowRequestListResponse> getPendingRequestsForOwner()
    throws ResponseStatusException {
    List<BorrowRequestDTO> borrowRequestDTOList = borrowRequestService.retrievePendingRequestsForOwner();
    BorrowRequestListResponse borrowRequestListResponse = new BorrowRequestListResponse(
      borrowRequestDTOList
    );

    return ResponseEntity.ok(borrowRequestListResponse);
  }

  /**
   * Retrieves the pending borrow requests for the requester.
   *
   * @return         	A ResponseEntity containing the list of pending borrow requests
   * @throws ResponseStatusException	if there is an exception while retrieving the requests
   */
  @Operation(
    summary = "Retrieves the pending borrow requests for the requester"
  )
  @GetMapping(path = "/pending/requester", produces = "application/json")
  public ResponseEntity<BorrowRequestListResponse> getPendingRequestsForRequester()
    throws ResponseStatusException {
    List<BorrowRequestDTO> borrowRequestDTOList = borrowRequestService.retrievePendingRequestsForRequester();
    BorrowRequestListResponse borrowRequestListResponse = new BorrowRequestListResponse(
      borrowRequestDTOList
    );

    return ResponseEntity.ok(borrowRequestListResponse);
  }

  /**
   * Retrieves the list of canceled borrow requests for the owner.
   *
   * @return         	Response entity containing the list of canceled borrow requests
   * @throws ResponseStatusException  if there is an error retrieving the list
   */
  @Operation(
    summary = "Retrieves the list of canceled borrow requests for the owner"
  )
  @GetMapping(path = "/canceled/owner", produces = "application/json")
  public ResponseEntity<BorrowRequestListResponse> getCanceledRequestsForOwner()
    throws ResponseStatusException {
    List<BorrowRequestDTO> borrowRequestDTOList = borrowRequestService.retrieveCanceledRequestsForOwner();
    BorrowRequestListResponse borrowRequestListResponse = new BorrowRequestListResponse(
      borrowRequestDTOList
    );

    return ResponseEntity.ok(borrowRequestListResponse);
  }

  /**
   * Retrieves the list of canceled borrow requests for the requester.
   *
   * @return         	Response entity containing the list of canceled borrow requests
   * @throws ResponseStatusException  if there is an error retrieving the list
   */
  @Operation(
    summary = "Retrieves the list of canceled borrow requests for the requester"
  )
  @GetMapping(path = "/canceled/requester", produces = "application/json")
  public ResponseEntity<BorrowRequestListResponse> getCanceledRequestsForRequester()
    throws ResponseStatusException {
    List<BorrowRequestDTO> borrowRequestDTOList = borrowRequestService.retrieveCanceledRequestsForRequester();
    BorrowRequestListResponse borrowRequestListResponse = new BorrowRequestListResponse(
      borrowRequestDTOList
    );

    return ResponseEntity.ok(borrowRequestListResponse);
  }

  /**
   * Retrieves the list of declined borrow requests for the owner.
   *
   * @return         	Response entity containing the list of declined borrow requests
   * @throws ResponseStatusException  if there is an error retrieving the list
   */
  @Operation(
    summary = "Retrieves the list of declined borrow requests for the owner"
  )
  @GetMapping(path = "/declined/owner", produces = "application/json")
  public ResponseEntity<BorrowRequestListResponse> getDeclinedRequestsForOwner()
    throws ResponseStatusException {
    List<BorrowRequestDTO> borrowRequestDTOList = borrowRequestService.retrieveDeclinedRequestsForOwner();
    BorrowRequestListResponse borrowRequestListResponse = new BorrowRequestListResponse(
      borrowRequestDTOList
    );

    return ResponseEntity.ok(borrowRequestListResponse);
  }

  /**
   * Retrieves the list of declined borrow requests for the requester.
   *
   * @return         	Response entity containing the list of declined borrow requests
   * @throws ResponseStatusException  if there is an error retrieving the list
   */
  @Operation(
    summary = "Retrieves the list of declined borrow requests for the requester"
  )
  @GetMapping(path = "/declined/requester", produces = "application/json")
  public ResponseEntity<BorrowRequestListResponse> getDeclinedRequestsForRequester()
    throws ResponseStatusException {
    List<BorrowRequestDTO> borrowRequestDTOList = borrowRequestService.retrieveDeclinedRequestsForRequester();
    BorrowRequestListResponse borrowRequestListResponse = new BorrowRequestListResponse(
      borrowRequestDTOList
    );

    return ResponseEntity.ok(borrowRequestListResponse);
  }

  /**
   * Accepts a request with the given request ID.
   *
   * @param  requestID  the ID of the request to accept
   * @return            a ResponseEntity with a void return type
   * @throws ResponseStatusException if an error occurs while accepting the request
   */
  @Operation(summary = "Accepts a request with the given request ID")
  @PostMapping(path = "/{requestID}/accept")
  public ResponseEntity<Void> acceptRequest(
    @PathVariable @ValidID @NotBlank String requestID
  )
    throws ResponseStatusException {
    borrowRequestService.acceptBorrowRequest(requestID);

    return ResponseEntity.ok().build();
  }

  /**
   * Declines a request with the given request ID.
   *
   * @param  requestID  the ID of the request to decline
   * @return            a ResponseEntity with a void return type
   * @throws ResponseStatusException if an error occurs while declining the request
   */
  @Operation(summary = "Declines a request with the given request ID")
  @PutMapping(path = "/{requestID}/decline")
  public ResponseEntity<Void> declineRequest(
    @PathVariable @ValidID @NotBlank String requestID
  )
    throws ResponseStatusException {
    borrowRequestService.declineBorrowRequest(requestID);

    return ResponseEntity.ok().build();
  }
}
