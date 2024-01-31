package no.delalt.back.model.enums;

public enum BorrowAgreementStatusEnum {
  INITIATED(0),
  ONGOING(1),
  RETURNED(2),
  COMPLETED(3);

  private final short value;

  BorrowAgreementStatusEnum(int value) {
    this.value = (short) value;
  }

  /**
   * Gets the value of the function.
   *
   * @return the value of the function
   */
  public short getValue() {
    return value;
  }
}
