package no.delalt.back.model.dto.output;

import no.delalt.back.model.dao.UserDAO;

public record UserDTO(String userID, String firstName, String lastName, String image) {
  public UserDTO(UserDAO userDAO) {
    this(
            userDAO.getUserID(),
            userDAO.getFirstName(),
            userDAO.getLastName(),
            userDAO.getImage()
    );
  }
}
