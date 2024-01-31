package no.delalt.back.model.id;

import java.io.Serializable;
import java.util.Objects;

public class UserGatheringID implements Serializable {
  private String user;
  private String gathering;

  public UserGatheringID() {}

  public UserGatheringID(String user, String gathering) {
    this.user = user;
    this.gathering = gathering;
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
    UserGatheringID that = (UserGatheringID) o;
    return (
      Objects.equals(user, that.user) &&
      Objects.equals(gathering, that.gathering)
    );
  }

  /**
   * A method to get the hash code of this object.
   *
   * @return  the hash code of this object
   */
  @Override
  public int hashCode() {
    return Objects.hash(user, gathering);
  }
}
