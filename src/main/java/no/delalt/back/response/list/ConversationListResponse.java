package no.delalt.back.response.list;

import no.delalt.back.model.dto.output.ConversationDTO;

import java.util.List;

public record ConversationListResponse (List<ConversationDTO> payload) {
}
