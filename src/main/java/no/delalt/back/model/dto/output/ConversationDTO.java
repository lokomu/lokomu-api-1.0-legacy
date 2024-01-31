package no.delalt.back.model.dto.output;

public record ConversationDTO (String conversationID, ChatMessageDTO lastMessage, UserDTO recipient) {}
