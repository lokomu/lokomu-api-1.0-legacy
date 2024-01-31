package no.delalt.back.response.list;

import no.delalt.back.model.dto.output.BorrowDatesDTO;

import java.util.List;

public record BorrowAgreementDatesListResponse (List<BorrowDatesDTO> payload) {
}
