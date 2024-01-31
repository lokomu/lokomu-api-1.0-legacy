package no.delalt.back.model.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(
  name = "chat_message",
  schema = "public",
  indexes = {
    @Index(name = "idx_conversation", columnList = "conversation_id"),
    @Index(name = "idx_timeSent", columnList = "time_sent")
  }
)
public class ChatMessageDAO {
  @Id
  @NotNull
  @Size(min = 26, max = 26)
  @Column(
    name = "message_id",
    columnDefinition = "VARCHAR(26)",
    nullable = false
  )
  private String messageID;

  @NotNull
  @Column(name = "text", nullable = false)
  private String text;

  @NotNull
  @Column(name = "time_sent", nullable = false)
  private long timeSent;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "sending_user_id", nullable = false)
  private UserDAO sendingUser;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "conversation_id", nullable = false)
  private ConversationDAO conversation;

  public ChatMessageDAO() {}

  public String getMessageID() {
    return this.messageID;
  }

  public void setMessageID(String messageID) {
    this.messageID = messageID;
  }

  public String getText() {
    return this.text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public long getTimeSent() {
    return this.timeSent;
  }

  public void setTimeSent(long timeSent) {
    this.timeSent = timeSent;
  }

  public UserDAO getSendingUser() {
    return sendingUser;
  }

  public void setSendingUser(UserDAO sendingUser) {
    this.sendingUser = sendingUser;
  }

  public ConversationDAO getConversation() {
    return conversation;
  }

  public void setConversation(ConversationDAO conversation) {
    this.conversation = conversation;
  }
}
