package no.delalt.back.model.enums;

public enum CommunityVisibilityEnum {
  OPEN(0),
  CLOSED(1),
  HIDDEN(2);

  private final short value;

  CommunityVisibilityEnum(int value) {
    this.value = (short) value;
  }

  /**
   * Returns the value of the enum.
   *
   * @return the value of the enum
   */
  public short getValue() {
    return value;
  }
}
