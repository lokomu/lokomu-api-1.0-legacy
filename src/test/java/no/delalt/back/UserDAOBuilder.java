package no.delalt.back;

import no.delalt.back.model.dao.UserDAO;

public class UserDAOBuilder {
  private String userID;
  private String email;
  private String firstName;
  private String lastName;
  private String image;
  private String hash;

  public UserDAOBuilder withUserID(String userID) {
    this.userID = userID;
    return this;
  }

  public UserDAOBuilder withEmail(String email) {
    this.email = email;
    return this;
  }

  public UserDAOBuilder withFirstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  public UserDAOBuilder withLastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  public UserDAOBuilder withImage(String image) {
    this.image = image;
    return this;
  }

  public UserDAOBuilder withHash(String hash) {
    this.hash = hash;
    return this;
  }

  public UserDAO build() {
    UserDAO userDAO = new UserDAO();
    userDAO.setUserID(userID);
    userDAO.setEmail(email);
    userDAO.setFirstName(firstName);
    userDAO.setLastName(lastName);
    userDAO.setImage(image);
    userDAO.setHash(hash);
    return userDAO;
  }
}
