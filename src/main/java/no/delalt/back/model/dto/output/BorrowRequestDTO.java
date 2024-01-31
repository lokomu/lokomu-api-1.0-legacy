package no.delalt.back.model.dto.output;

import no.delalt.back.model.dao.BorrowRequestDAO;

public record BorrowRequestDTO(String borrowRequestID, ItemDTO item, UserDTO owner, UserDTO requester,
                               String message, long createdAt, String startDate, String endDate,
                               short status) {

  public BorrowRequestDTO(BorrowRequestDAO borrowRequestDAO) {
    this(
            borrowRequestDAO.getBorrowRequestID(),
            new ItemDTO(borrowRequestDAO.getItem()),
            new UserDTO(borrowRequestDAO.getOwner()),
            new UserDTO(borrowRequestDAO.getRequester()),
            borrowRequestDAO.getMessage(),
            borrowRequestDAO.getCreatedAt(),
            borrowRequestDAO.getStartDate().toString(),
            borrowRequestDAO.getEndDate().toString(),
            borrowRequestDAO.getStatus()
    );
  }
}