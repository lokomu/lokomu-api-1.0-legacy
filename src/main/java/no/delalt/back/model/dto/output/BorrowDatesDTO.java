package no.delalt.back.model.dto.output;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record BorrowDatesDTO(String startDate, String endDate) {

  public BorrowDatesDTO(LocalDate startDate, LocalDate endDate) {
    this(
            startDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
            endDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
    );
  }
}