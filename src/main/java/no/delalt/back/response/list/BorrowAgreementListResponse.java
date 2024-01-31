package no.delalt.back.response.list;

import no.delalt.back.model.dto.output.BorrowAgreementDTO;

import java.util.List;

public record BorrowAgreementListResponse (List<BorrowAgreementDTO> payload) {
}
