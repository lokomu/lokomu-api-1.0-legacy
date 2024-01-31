package no.delalt.back.model.enums;

public enum BorrowRequestStatusEnum {
  PENDING(0),
  ACCEPTED(1),
  REJECTED(2),
  CANCELED(3);

  private final short value;

  BorrowRequestStatusEnum(int value) {
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
