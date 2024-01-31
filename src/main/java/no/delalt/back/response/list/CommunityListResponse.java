package no.delalt.back.response.list;

import no.delalt.back.model.dto.output.CommunityDTO;

import java.util.List;

public record CommunityListResponse(List<CommunityDTO> payload) {
}
