package no.delalt.back.model.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import no.delalt.back.model.id.UserConversationID;

@Entity
@Table(name = "user_conversation", schema = "public")
@IdClass(UserConversationID.class)
public class UserConversationDAO {
  @Id
  @NotNull
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserDAO user;

  @Id
  @NotNull
  @ManyToOne
  @JoinColumn(name = "conversation_id", nullable = false)
  private ConversationDAO conversation;

  public UserConversationDAO() {}

  public UserConversationDAO(UserDAO user, ConversationDAO conversation) {
    this.user = user;
    this.conversation = conversation;
  }

  public void setUser(UserDAO user) {
    this.user = user;
  }

  public ConversationDAO getConversation() {
    return conversation;
  }

  public void setConversation(ConversationDAO conversation) {
    this.conversation = conversation;
  }
}
