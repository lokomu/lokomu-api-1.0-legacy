package no.delalt.back.response.list;

import no.delalt.back.model.dto.output.GatheringDTO;

import java.util.List;

public record GatheringListResponse(List<GatheringDTO> payload) {
}
