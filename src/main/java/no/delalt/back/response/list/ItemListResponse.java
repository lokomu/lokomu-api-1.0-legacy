package no.delalt.back.response.list;

import no.delalt.back.model.dto.output.ItemDTO;

import java.util.List;

public record ItemListResponse(List<ItemDTO> payload) {
}
