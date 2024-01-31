package no.delalt.back.model.dto.output;

import no.delalt.back.model.dao.BorrowAgreementDAO;

public record BorrowAgreementDTO(String borrowID, ItemDTO item, UserDTO borrower, UserDTO owner,
                                 long createdAt, String startDate, String endDate, Long returnTime,
                                 short status) {

  public BorrowAgreementDTO(BorrowAgreementDAO borrowDAO) {
    this(
            borrowDAO.getBorrowID(),
            new ItemDTO(borrowDAO.getItem()),
            new UserDTO(borrowDAO.getBorrower()),
            new UserDTO(borrowDAO.getOwner()),
            borrowDAO.getCreatedAt(),
            borrowDAO.getStartDate().toString(),
            borrowDAO.getEndDate().toString(),
            borrowDAO.getReturnTime(),
            borrowDAO.getStatus()
    );
  }
}
