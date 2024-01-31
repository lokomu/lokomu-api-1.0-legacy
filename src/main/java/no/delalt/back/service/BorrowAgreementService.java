package no.delalt.back.service;

import no.delalt.back.model.dao.BorrowAgreementDAO;
import no.delalt.back.model.dao.ItemDAO;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.model.dto.input.RatingStatusDTO;
import no.delalt.back.model.dto.output.BorrowAgreementDTO;
import no.delalt.back.model.dto.output.BorrowDatesDTO;
import no.delalt.back.model.enums.BorrowAgreementStatusEnum;
import no.delalt.back.repository.BorrowAgreementRepository;
import no.delalt.back.service.save.RatingSaveService;
import no.delalt.back.service.validation.ItemValidationService;
import no.delalt.back.service.validation.UserValidationService;
import no.delalt.back.util.SecurityUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class BorrowAgreementService {
  private final BorrowAgreementRepository borrowRepository;
  private final UserValidationService userValidationService;
  private final ItemValidationService itemValidationService;
  private final RatingSaveService ratingSaveService;

  public BorrowAgreementService(
    BorrowAgreementRepository borrowRepository,
    UserValidationService userValidationService,
    ItemValidationService itemValidationService,
    RatingSaveService ratingSaveService
  ) {
    this.borrowRepository = borrowRepository;
    this.userValidationService = userValidationService;
    this.itemValidationService = itemValidationService;
    this.ratingSaveService = ratingSaveService;
  }

  // -------------------- Controller Methods --------------------

  /**
   * Retrieves a list of BorrowDatesDTO for a given itemID.
   *
   * @param  itemID  the ID of the item
   * @return         a list of BorrowDatesDTO
   */
  @Transactional(readOnly = true)
  public List<BorrowDatesDTO> retrieveBorrowDatesForItem(String itemID) {
    ItemDAO itemDAO = itemValidationService.validateItemExistsAndReturn(itemID);
    return borrowRepository.findAllBorrowDatesForItem(itemDAO, LocalDate.now());
  }

  /**
   * Retrieves a list of incomplete borrow agreements for the owner.
   *
   * @return a list of BorrowAgreementDTO objects representing the incomplete borrow agreements
   */
  @Transactional(readOnly = true)
  public List<BorrowAgreementDTO> retrieveIncompleteBorrowsForOwner() {
    UserDAO user = userValidationService.validateUserExistsAndReturn(
      SecurityUtil.getAuthenticatedAccountID()
    );
    List<BorrowAgreementDAO> borrowDAOs = borrowRepository.findAllByOwnerAndIsCompleted(
      user,
      false
    );
    return toBorrowDTOs(borrowDAOs);
  }

  /**
   * Retrieves a list of incomplete borrow agreements for the borrower.
   *
   * @return a list of BorrowAgreementDTO objects representing the incomplete borrow agreements
   */
  @Transactional(readOnly = true)
  public List<BorrowAgreementDTO> retrieveIncompleteBorrowsForBorrower() {
    UserDAO user = userValidationService.validateUserExistsAndReturn(
      SecurityUtil.getAuthenticatedAccountID()
    );
    List<BorrowAgreementDAO> borrowDAOs = borrowRepository.findAllByBorrowerAndIsCompleted(
      user,
      false
    );
    return toBorrowDTOs(borrowDAOs);
  }

  /**
   * Retrieves a list of completed borrow agreements for the owner.
   *
   * @return a list of BorrowAgreementDTO objects representing the completed borrow agreements
   */
  @Transactional(readOnly = true)
  public List<BorrowAgreementDTO> retrieveCompletedBorrowsForOwner() {
    UserDAO user = userValidationService.validateUserExistsAndReturn(
      SecurityUtil.getAuthenticatedAccountID()
    );
    List<BorrowAgreementDAO> borrowDAOs = borrowRepository.findAllByOwnerAndIsCompleted(
      user,
      true
    );
    return toBorrowDTOs(borrowDAOs);
  }

  /**
   * Retrieves a list of completed borrow agreements for the borrower.
   *
   * @return a list of BorrowAgreementDTO objects representing the completed borrow agreements
   */
  @Transactional(readOnly = true)
  public List<BorrowAgreementDTO> retrieveCompletedBorrowsForBorrower() {
    UserDAO user = userValidationService.validateUserExistsAndReturn(
      SecurityUtil.getAuthenticatedAccountID()
    );
    List<BorrowAgreementDAO> borrowDAOs = borrowRepository.findAllByBorrowerAndIsCompleted(
      user,
      true
    );
    return toBorrowDTOs(borrowDAOs);
  }

  /**
   * Sets the borrow status to ongoing for the specified borrow agreement ID.
   *
   * @param  borrowAgreementID  the ID of the borrow agreement
   */
  @Transactional
  public void setBorrowStatusToOngoing(String borrowAgreementID) {
    BorrowAgreementDAO borrowDAO = getBorrowFromID(borrowAgreementID);

    userValidationService.validateAuthenticatedUserIsSame(
      borrowDAO.getOwner().getUserID()
    );

    if (
      borrowDAO.getStatus() != BorrowAgreementStatusEnum.INITIATED.getValue()
    ) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Cannot set ongoing status for not initiated status borrows."
      );
    }

    validateNoOngoingBorrowsForItem(borrowDAO.getItem());

    if (LocalDate.now().isBefore(borrowDAO.getStartDate())) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Cannot set ongoing status before the start date"
      );
    }

    borrowDAO.setStatus(BorrowAgreementStatusEnum.ONGOING.getValue());

    borrowRepository.save(borrowDAO);
  }

  /**
   * Sets the borrow status to returned for the specified borrow agreement ID.
   *
   * @param  borrowAgreementID  the ID of the borrow agreement
   */
  @Transactional
  public void setBorrowStatusToReturned(String borrowAgreementID) {
    BorrowAgreementDAO borrowDAO = getBorrowFromID(borrowAgreementID);

    userValidationService.validateAuthenticatedUserIsSame(
      borrowDAO.getOwner().getUserID()
    );

    if (borrowDAO.getStatus() != BorrowAgreementStatusEnum.ONGOING.getValue()) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Cannot set returned status for not ongoing status borrows."
      );
    }

    if (LocalDate.now().isBefore(borrowDAO.getEndDate())) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Cannot set returned status before the end date"
      );
    }

    borrowDAO.setStatus(BorrowAgreementStatusEnum.RETURNED.getValue());
    borrowDAO.setReturnTime(System.currentTimeMillis());

    borrowRepository.save(borrowDAO);
  }

  /**
   * Sets the borrow status to completed for the specified borrow agreement ID.
   *
   * @param  borrowAgreementID  the ID of the borrow agreement
   */
  @Transactional
  public void setBorrowStatusToCompleted(
    String borrowAgreementID,
    RatingStatusDTO ratingStatusDTO
  ) {
    BorrowAgreementDAO borrowDAO = getBorrowFromID(borrowAgreementID);

    userValidationService.validateAuthenticatedUserIsSame(
      borrowDAO.getOwner().getUserID()
    );

    if (
      borrowDAO.getStatus() != BorrowAgreementStatusEnum.RETURNED.getValue()
    ) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Cannot set completed status for not returned status borrows."
      );
    }

    borrowDAO.setStatus(BorrowAgreementStatusEnum.COMPLETED.getValue());
    borrowDAO.setIsCompleted(true);

    borrowRepository.save(borrowDAO);

    ratingSaveService.saveRating(borrowDAO, ratingStatusDTO);
  }

  // -------------------- Helper Methods --------------------

  /**
   * Validates that there are no ongoing borrows for a given item.
   *
   * @param  item	the item to be validated
   */
  private void validateNoOngoingBorrowsForItem(ItemDAO item) {
    if (
      borrowRepository.existsByItemAndIsCompletedAndStatus(
        item,
        false,
        BorrowAgreementStatusEnum.ONGOING.getValue()
      )
    ) {
      throw new ResponseStatusException(
        HttpStatus.CONFLICT,
        "Cannot set ongoing status for an item that already has an ongoing borrow."
      );
    }
  }

  /**
   * Retrieves a BorrowAgreementDAO object from the borrow repository based on the provided borrow ID.
   *
   * @param  borrowID    the ID of the borrow agreement
   * @return             the BorrowAgreementDAO object corresponding to the borrow ID
   */
  private BorrowAgreementDAO getBorrowFromID(String borrowID) {
    return borrowRepository
      .findById(borrowID)
      .orElseThrow(
        () ->
          new ResponseStatusException(HttpStatus.NOT_FOUND, "Borrow not found")
      );
  }

  /**
   * Converts a list of BorrowAgreementDAO objects to a list of BorrowAgreementDTO objects.
   *
   * @param  list    the list of BorrowAgreementDAO objects
   * @return         the list of BorrowAgreementDTO objects
   */
  private static List<BorrowAgreementDTO> toBorrowDTOs(
    List<BorrowAgreementDAO> list
  ) {
    return list.stream().map(BorrowAgreementDTO::new).toList();
  }
}
