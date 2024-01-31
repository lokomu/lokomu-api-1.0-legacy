package no.delalt.back.model.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import no.delalt.back.configuration.validation.ValidID;

@Entity
@Table(
  name = "conversation",
  schema = "public",
  indexes = {
    @Index(name = "idx_user1_user2", columnList = "user_1_id, user_2_id")
  }
)
public class ConversationDAO {
  @Id
  @ValidID
  @NotNull
  @Column(
    name = "conversation_id",
    columnDefinition = "VARCHAR(21)",
    nullable = false
  )
  private String conversationID;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "user_1_id", nullable = false)
  private UserDAO user1;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "user_2_id", nullable = false)
  private UserDAO user2;

  @ManyToOne
  @JoinColumn(name = "last_message_id")
  private ChatMessageDAO lastMessage;

  public ConversationDAO() {}

  public ConversationDAO(
    String conversationID,
    UserDAO user1,
    UserDAO user2,
    ChatMessageDAO lastMessage
  ) {
    this.conversationID = conversationID;
    this.user1 = user1;
    this.user2 = user2;
    this.lastMessage = lastMessage;
  }

  public String getConversationID() {
    return conversationID;
  }

  public void setConversationID(String conversationID) {
    this.conversationID = conversationID;
  }

  public UserDAO getUser1() {
    return user1;
  }

  public void setUser1(UserDAO user1) {
    this.user1 = user1;
  }

  public UserDAO getUser2() {
    return user2;
  }

  public void setUser2(UserDAO user2) {
    this.user2 = user2;
  }

  public ChatMessageDAO getLastMessage() {
    return lastMessage;
  }

  public void setLastMessage(ChatMessageDAO lastMessage) {
    this.lastMessage = lastMessage;
  }
}
