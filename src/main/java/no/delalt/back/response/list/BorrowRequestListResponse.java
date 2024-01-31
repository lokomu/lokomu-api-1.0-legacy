package no.delalt.back.response.list;

import no.delalt.back.model.dto.output.BorrowRequestDTO;

import java.util.List;

public record BorrowRequestListResponse (List<BorrowRequestDTO> payload) {
}
