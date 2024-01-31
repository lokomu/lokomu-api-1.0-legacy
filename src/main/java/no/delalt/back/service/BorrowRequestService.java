package no.delalt.back.service;

import no.delalt.back.model.dao.BorrowAgreementDAO;
import no.delalt.back.model.dao.BorrowRequestDAO;
import no.delalt.back.model.dao.ItemDAO;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.model.dto.input.BorrowRequestCreationDTO;
import no.delalt.back.model.dto.output.BorrowRequestDTO;
import no.delalt.back.model.enums.BorrowAgreementStatusEnum;
import no.delalt.back.model.enums.BorrowRequestStatusEnum;
import no.delalt.back.model.object.BorrowDates;
import no.delalt.back.repository.BorrowRequestRepository;
import no.delalt.back.service.save.BorrowAgreementSaveService;
import no.delalt.back.service.validation.BorrowAgreementValidationService;
import no.delalt.back.service.validation.CommunityItemValidationService;
import no.delalt.back.service.validation.ItemValidationService;
import no.delalt.back.service.validation.UserCommunityValidationService;
import no.delalt.back.service.validation.UserValidationService;
import no.delalt.back.util.NanoIdGenerator;
import no.delalt.back.util.SanitizationUtil;
import no.delalt.back.util.SecurityUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BorrowRequestService {
  private final BorrowAgreementValidationService borrowAgreementValidationService;
  private final BorrowAgreementSaveService borrowAgreementSaveService;

  private final BorrowRequestRepository borrowRequestRepository;
  private final ItemValidationService itemValidationService;
  private final UserValidationService userValidationService;
  private final UserCommunityValidationService userCommunityValidationService;
  private final CommunityItemValidationService communityItemValidationService;

  private static final short MINIMUM_TIME = 1; //1 DAY
  private static final short MAXIMUM_TIME = 366; //366 Days, 1 YEAR
  private static final short FOUR_MONTH_LIMIT = 122; //122 Days, 4 MONTHS
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
    "yyyy-MM-dd"
  );

  public BorrowRequestService(
    ItemValidationService itemValidationService,
    UserValidationService userValidationService,
    UserCommunityValidationService userCommunityValidationService,
    BorrowRequestRepository borrowRequestRepository,
    BorrowAgreementValidationService borrowAgreementValidationService,
    BorrowAgreementSaveService borrowAgreementSaveService,
    CommunityItemValidationService communityItemValidationService
  ) {
    this.itemValidationService = itemValidationService;
    this.userValidationService = userValidationService;
    this.userCommunityValidationService = userCommunityValidationService;
    this.borrowRequestRepository = borrowRequestRepository;
    this.borrowAgreementValidationService = borrowAgreementValidationService;
    this.borrowAgreementSaveService = borrowAgreementSaveService;
    this.communityItemValidationService = communityItemValidationService;
  }

  // -------------------- Controller Methods --------------------

  /**
   * Creates a borrow request based on the provided BorrowRequestCreationDTO.
   *
   * @param  requestCreationDTO  the BorrowRequestCreationDTO containing the details of the borrow request
   */
  @Transactional
  public void createBorrowRequest(BorrowRequestCreationDTO requestCreationDTO) {
    BorrowDates requestDates = convertStringToLocalDate(
      requestCreationDTO.startDate(),
      requestCreationDTO.endDate()
    );

    checkTimeframeOfRequest(requestDates.startDate(), requestDates.endDate());

    ItemDAO itemDAO = itemValidationService.validateItemExistsAndReturn(
      requestCreationDTO.itemID()
    );

    validateItemBorrowable(itemDAO);

    userValidationService.validateAuthenticatedUserIsDifferent(
      itemDAO.getUser().getUserID()
    );

    communityItemValidationService.validateItemExistsInCommunity(
      requestCreationDTO.communityID(),
      itemDAO.getItemID()
    );

    userCommunityValidationService.validateCurrentUserIsMemberOfCommunity(
      requestCreationDTO.communityID()
    );

    borrowAgreementValidationService.validateNoBorrowsInTimeframe(
      itemDAO,
      requestDates.startDate(),
      requestDates.endDate()
    );

    UserDAO userDAO = userValidationService.validateUserExistsAndReturn(
      SecurityUtil.getAuthenticatedAccountID()
    );

    validateNoRequestsForItemFromUser(itemDAO, userDAO);

    BorrowRequestDAO borrowRequestDAO = new BorrowRequestDAO();

    borrowRequestDAO.setBorrowRequestID(NanoIdGenerator.generateNanoID());
    borrowRequestDAO.setRequester(userDAO);
    borrowRequestDAO.setCreatedAt(System.currentTimeMillis());
    borrowRequestDAO.setStartDate(requestDates.startDate());
    borrowRequestDAO.setEndDate(requestDates.endDate());
    String safeMessage = SanitizationUtil.sanitize(
      requestCreationDTO.message()
    );
    borrowRequestDAO.setMessage(safeMessage);
    borrowRequestDAO.setItem(itemDAO);
    borrowRequestDAO.setOwner(itemDAO.getUser());
    borrowRequestDAO.setStatus(BorrowRequestStatusEnum.PENDING.getValue());
    borrowRequestDAO.setIsProcessed(false);

    borrowRequestRepository.save(borrowRequestDAO);
  }

  /**
   * Retrieves a list of pending borrow requests for the owner.
   *
   * @return         	A list of BorrowRequestDTO objects representing the pending borrow requests.
   */
  @Transactional(readOnly = true)
  public List<BorrowRequestDTO> retrievePendingRequestsForOwner() {
    UserDAO currentUser = userValidationService.validateUserExistsAndReturn(
      SecurityUtil.getAuthenticatedAccountID()
    );
    List<BorrowRequestDAO> borrowRequestDAOs = borrowRequestRepository.findAllByOwnerAndIsProcessed(
      currentUser,
      false
    );
    return toBorrowRequestDTOs(borrowRequestDAOs);
  }

  /**
   * Retrieves a list of pending borrow requests for the requester.
   *
   * @return         	A list of BorrowRequestDTO objects representing the pending borrow requests.
   */
  @Transactional(readOnly = true)
  public List<BorrowRequestDTO> retrievePendingRequestsForRequester() {
    UserDAO currentUser = userValidationService.validateUserExistsAndReturn(
      SecurityUtil.getAuthenticatedAccountID()
    );
    List<BorrowRequestDAO> borrowRequestDAOs = borrowRequestRepository.findAllByRequesterAndIsProcessed(
      currentUser,
      false
    );

    return toBorrowRequestDTOs(borrowRequestDAOs);
  }

  /**
   * Retrieves a list of canceled borrow requests for the owner.
   *
   * @return         	A list of BorrowRequestDTO objects representing the canceled borrow requests.
   */
  @Transactional(readOnly = true)
  public List<BorrowRequestDTO> retrieveCanceledRequestsForOwner() {
    UserDAO currentUser = userValidationService.validateUserExistsAndReturn(
      SecurityUtil.getAuthenticatedAccountID()
    );
    List<BorrowRequestDAO> borrowRequestDAOs = borrowRequestRepository.findAllByOwnerAndIsProcessedAndStatus(
      currentUser,
      true,
      BorrowRequestStatusEnum.CANCELED.getValue()
    );
    return toBorrowRequestDTOs(borrowRequestDAOs);
  }

  /**
   * Retrieves a list of canceled borrow requests for the requester.
   *
   * @return         	A list of BorrowRequestDTO objects representing the canceled borrow requests.
   */
  @Transactional(readOnly = true)
  public List<BorrowRequestDTO> retrieveCanceledRequestsForRequester() {
    UserDAO currentUser = userValidationService.validateUserExistsAndReturn(
      SecurityUtil.getAuthenticatedAccountID()
    );
    List<BorrowRequestDAO> borrowRequestDAOs = borrowRequestRepository.findAllByRequesterAndIsProcessedAndStatus(
      currentUser,
      true,
      BorrowRequestStatusEnum.CANCELED.getValue()
    );
    return toBorrowRequestDTOs(borrowRequestDAOs);
  }

  /**
   * Retrieves a list of declined borrow requests for the owner.
   *
   * @return         	A list of BorrowRequestDTO objects representing the declined borrow requests.
   */
  @Transactional(readOnly = true)
  public List<BorrowRequestDTO> retrieveDeclinedRequestsForOwner() {
    UserDAO currentUser = userValidationService.validateUserExistsAndReturn(
      SecurityUtil.getAuthenticatedAccountID()
    );
    List<BorrowRequestDAO> borrowRequestDAOs = borrowRequestRepository.findAllByOwnerAndIsProcessedAndStatus(
      currentUser,
      true,
      BorrowRequestStatusEnum.REJECTED.getValue()
    );
    return toBorrowRequestDTOs(borrowRequestDAOs);
  }

  /**
   * Retrieves a list of declined borrow requests for the requester.
   *
   * @return         	A list of BorrowRequestDTO objects representing the declined borrow requests.
   */
  @Transactional(readOnly = true)
  public List<BorrowRequestDTO> retrieveDeclinedRequestsForRequester() {
    UserDAO currentUser = userValidationService.validateUserExistsAndReturn(
      SecurityUtil.getAuthenticatedAccountID()
    );
    List<BorrowRequestDAO> borrowRequestDAOs = borrowRequestRepository.findAllByRequesterAndIsProcessedAndStatus(
      currentUser,
      true,
      BorrowRequestStatusEnum.REJECTED.getValue()
    );
    return toBorrowRequestDTOs(borrowRequestDAOs);
  }

  /**
   * Accepts a borrow request.
   *
   * @param requestID   The ID of the request to accept.
   */
  @Transactional
  public void acceptBorrowRequest(String requestID) {
    BorrowRequestDAO borrowRequestDAO = validateRequestAndReturn(requestID);

    userValidationService.validateAuthenticatedUserIsSame(
      borrowRequestDAO.getOwner().getUserID()
    );

    if (
      borrowRequestDAO.getStatus() != BorrowRequestStatusEnum.PENDING.getValue()
    ) {
      throw new ResponseStatusException(
        HttpStatus.CONFLICT,
        "Request cannot be accepted, because it isn't pending."
      );
    }

    LocalDate currentDate = LocalDate.now();

    if (borrowRequestDAO.getEndDate().isBefore(currentDate)) {
      borrowRequestDAO.setIsProcessed(true);
      borrowRequestDAO.setStatus(BorrowRequestStatusEnum.CANCELED.getValue());
      borrowRequestRepository.save(borrowRequestDAO);
      throw new ResponseStatusException(
        HttpStatus.UNPROCESSABLE_ENTITY,
        "The request end time is in the past."
      );
    }

    BorrowAgreementDAO borrowDAO = new BorrowAgreementDAO();

    if (borrowRequestDAO.getStartDate().isBefore(currentDate)) {
      if (
        ChronoUnit.DAYS.between(borrowRequestDAO.getEndDate(), currentDate) <
        MINIMUM_TIME
      ) {
        borrowRequestDAO.setIsProcessed(true);
        borrowRequestDAO.setStatus(BorrowRequestStatusEnum.CANCELED.getValue());
        borrowRequestRepository.save(borrowRequestDAO);
        throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY,
          "The borrow agreement cannot be less than a day."
        );
      }
      borrowDAO.setStartDate(currentDate);
    } else {
      borrowDAO.setStartDate(borrowRequestDAO.getStartDate());
    }

    borrowDAO.setBorrowRequest(borrowRequestDAO);
    borrowDAO.setBorrowID(NanoIdGenerator.generateNanoID());
    borrowDAO.setCreatedAt(System.currentTimeMillis());
    borrowDAO.setItem(borrowRequestDAO.getItem());
    borrowDAO.setBorrower(borrowRequestDAO.getRequester());
    borrowDAO.setOwner(borrowRequestDAO.getOwner());
    borrowDAO.setEndDate(borrowRequestDAO.getEndDate());
    borrowDAO.setStatus(BorrowAgreementStatusEnum.INITIATED.getValue());
    borrowDAO.setReturnTime(null);
    borrowDAO.setIsCompleted(false);

    borrowAgreementSaveService.saveAgreement(borrowDAO);

    borrowRequestDAO.setStatus(BorrowRequestStatusEnum.ACCEPTED.getValue());
    borrowRequestDAO.setIsProcessed(true);
    borrowRequestRepository.save(borrowRequestDAO);

    List<BorrowRequestDAO> borrowRequestDAOs = borrowRequestRepository.findPendingRequestsByItemInTimeframe(
      borrowRequestDAO.getItem(),
      borrowRequestDAO.getStartDate(),
      borrowRequestDAO.getEndDate()
    );
    if (!borrowRequestDAOs.isEmpty()) {
      for (BorrowRequestDAO requestDAO : borrowRequestDAOs) {
        requestDAO.setStatus(BorrowRequestStatusEnum.CANCELED.getValue());
        requestDAO.setIsProcessed(true);
      }
      borrowRequestRepository.saveAll(borrowRequestDAOs);
    }
  }

  /**
   * Declines a borrow request.
   *
   * @param requestID   The ID of the request to decline.
   */
  @Transactional
  public void declineBorrowRequest(String requestID) {
    BorrowRequestDAO borrowRequestDAO = validateRequestAndReturn(requestID);

    userValidationService.validateAuthenticatedUserIsSame(
      borrowRequestDAO.getOwner().getUserID()
    );

    if (
      borrowRequestDAO.getStatus() != BorrowRequestStatusEnum.PENDING.getValue()
    ) {
      throw new ResponseStatusException(
        HttpStatus.CONFLICT,
        "Request cannot be declined, because it isn't pending."
      );
    }

    borrowRequestDAO.setStatus(BorrowRequestStatusEnum.REJECTED.getValue());
    borrowRequestDAO.setIsProcessed(true);
    borrowRequestRepository.save(borrowRequestDAO);
  }

  // -------------------- Helper Methods --------------------

  /**
   * Converts a list of BorrowRequestDAO objects to a list of BorrowRequestDTO objects.
   *
   * @param  list  the list of BorrowRequestDAO objects to be converted
   * @return       the list of BorrowRequestDTO objects after conversion
   */
  private static List<BorrowRequestDTO> toBorrowRequestDTOs(
    List<BorrowRequestDAO> list
  ) {
    return list.stream().map(BorrowRequestDTO::new).toList();
  }

  /**
   * Validates if there are no existing borrow requests for a specific item from a user.
   *
   * @param  item      the item to be checked for existing borrow requests
   * @param  requester the user who made the request
   */
  private void validateNoRequestsForItemFromUser(
    ItemDAO item,
    UserDAO requester
  ) {
    if (
      borrowRequestRepository.existsByRequesterAndIsProcessedAndItem(
        requester,
        false,
        item
      )
    ) {
      throw new ResponseStatusException(
        HttpStatus.CONFLICT,
        "Borrow request already exists for this item."
      );
    }
  }

  /**
   * Converts a string representation of a start date and end date to LocalDate objects.
   *
   * @param  startDate  the string representation of the start date in the format "yyyy-MM-dd"
   * @param  endDate    the string representation of the end date in the format "yyyy-MM-dd"
   * @return            the BorrowDates object containing the converted LocalDate objects
   */
  private static BorrowDates convertStringToLocalDate(
    String startDate,
    String endDate
  ) {
    //TODO Handle the DateTimeParseException
    LocalDate startLocalDate = LocalDate.parse(startDate, formatter);
    LocalDate endLocalDate = LocalDate.parse(endDate, formatter);

    return new BorrowDates(startLocalDate, endLocalDate);
  }

  /**
   * Checks if the start date and end date of a borrow request are valid.
   *
   * @param  startDate  the start date of the borrow request
   * @param  endDate    the end date of the borrow request
   */
  private static void checkTimeframeOfRequest(
    LocalDate startDate,
    LocalDate endDate
  ) {
    if (startDate.isAfter(endDate)) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Start date cannot be after the end date."
      );
    }

    LocalDate currentDate = LocalDate.now();

    if (startDate.isBefore(currentDate)) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Start date cannot be in the past."
      );
    }

    long dayDifference = ChronoUnit.DAYS.between(startDate, endDate);

    if (dayDifference < MINIMUM_TIME || dayDifference > MAXIMUM_TIME) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Borrow length cannot be less than 1 day and more than 1 year."
      );
    }

    if (startDate.isAfter(LocalDate.now().plusDays(FOUR_MONTH_LIMIT))) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "The requested borrowing date is too far in advance."
      );
    }
  }

  /**
   * Validates the request ID and returns the corresponding BorrowRequestDAO object.
   *
   * @param  requestID the ID of the request to be validated
   * @return the BorrowRequestDAO object corresponding to the request ID
   * @throws ResponseStatusException if the request ID does not exist in the borrowRequestRepository
   */
  private BorrowRequestDAO validateRequestAndReturn(String requestID)
    throws ResponseStatusException {
    return borrowRequestRepository
      .findById(requestID)
      .orElseThrow(
        () ->
          new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Borrow request not found"
          )
      );
  }

  /**
   * Validates if an item is borrowable.
   *
   * @param  itemDAO the item to be validated
   * @throws ResponseStatusException if the item is not borrowable
   */
  private void validateItemBorrowable(ItemDAO itemDAO) {
    if (itemDAO.getIsForGiving()) {
      throw new ResponseStatusException(
        HttpStatus.CONFLICT,
        "Item is not borrowable"
      );
    }
  }
}
