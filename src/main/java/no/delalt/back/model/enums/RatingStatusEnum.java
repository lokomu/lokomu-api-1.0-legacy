package no.delalt.back.model.enums;

public enum RatingStatusEnum {
  SAME_CONDITION(0),
  BETTER_CONDITION(1),
  WORSE_CONDITION(2),
  LATE_RETURN(3),
  NOT_RETURNED(4);

  private final short value;

  RatingStatusEnum(int value) {
    this.value = (short) value;
  }

  /**
   * Retrieve the value of the variable.
   *
   * @return the value of the variable
   */
  public short getValue() {
    return value;
  }
}
