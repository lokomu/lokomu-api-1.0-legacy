package no.delalt.back.model.id;

import java.io.Serializable;
import java.util.Objects;

public class UserCommunityID implements Serializable {
  private String user;
  private String community;

  public UserCommunityID() {}

  public UserCommunityID(String user, String community) {
    this.user = user;
    this.community = community;
  }

  /**
   * A method to check if this object is equal to another object.
   *
   * @param  o  the object to compare to
   * @return    true if the objects are equal, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserCommunityID that = (UserCommunityID) o;
    return user.equals(that.user) && community.equals(that.community);
  }

  /**
   * A method to get the hash code of this object.
   *
   * @return  the hash code of this object
   */
  @Override
  public int hashCode() {
    return Objects.hash(user, community);
  }
}
