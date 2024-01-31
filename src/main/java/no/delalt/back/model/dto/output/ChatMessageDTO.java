package no.delalt.back.model.dto.output;

import no.delalt.back.model.dao.ChatMessageDAO;

public record ChatMessageDTO(String ID, String content, long timestamp, String from) {

  public ChatMessageDTO(ChatMessageDAO chatMessageDAO) {
    this(
            chatMessageDAO.getMessageID(),
            chatMessageDAO.getText(),
            chatMessageDAO.getTimeSent(),
            chatMessageDAO.getSendingUser().getUserID()
    );
  }
}
