package no.delalt.back.model.id;

import java.io.Serializable;
import java.util.Objects;

public class UserConversationID implements Serializable {
  private String user;
  private String conversation;

  public UserConversationID() {}

  public UserConversationID(String user, String conversation) {
    this.user = user;
    this.conversation = conversation;
  }

  /**
   * Overrides the equals method to check if two UserConversationID objects are equal.
   *
   * @param  o The object to compare with this UserConversationID.
   * @return   true if the objects are equal, false otherwise.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserConversationID that = (UserConversationID) o;
    return user.equals(that.user) && conversation.equals(that.conversation);
  }

  /**
   * Generates the hash code for the object based on the user and conversation properties.
   *
   * @return the hash code value for the object
   */
  @Override
  public int hashCode() {
    return Objects.hash(user, conversation);
  }
}
