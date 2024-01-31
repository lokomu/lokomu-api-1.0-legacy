package no.delalt.back.response.list;

import no.delalt.back.model.dto.output.ChatMessageDTO;

import java.util.List;

public record ChatMessageListResponse(List<ChatMessageDTO> payload) {
}
