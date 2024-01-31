package no.delalt.back.model.id;

import java.io.Serializable;
import java.util.Objects;

public class CommunityItemID implements Serializable {
  private String community;
  private String item;

  public CommunityItemID() {}

  public CommunityItemID(String community, String item) {
    this.community = community;
    this.item = item;
  }

  /**
   * Overrides the equals method to compare two CommunityItemID objects for equality.
   *
   * @param  o The object to compare with this CommunityItemID.
   * @return   true if the objects are equal, false otherwise.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CommunityItemID that = (CommunityItemID) o;
    return (community.equals(that.community) && item.equals(that.item));
  }

  /**
   * Calculates the hash code of the object based on the community and item properties.
   *
   * @return          The hash code of the object.
   */
  @Override
  public int hashCode() {
    return Objects.hash(community, item);
  }
}
