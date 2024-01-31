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
  name = "community_request",
  schema = "public",
  indexes = { @Index(name = "idx_community", columnList = "community_id") }
)
public class CommunityRequestDAO {
  @Id
  @ValidID
  @NotNull
  @Column(
    name = "community_request_id",
    columnDefinition = "VARCHAR(21)",
    nullable = false
  )
  private String communityRequestID;

  @NotNull
  @Column(name = "text")
  private String text;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserDAO user;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "community_id", nullable = false)
  private CommunityDAO community;

  public CommunityRequestDAO() {}

  public String getCommunityRequestID() {
    return communityRequestID;
  }

  public void setCommunityRequestID(String communityRequestID) {
    this.communityRequestID = communityRequestID;
  }

  public UserDAO getUser() {
    return user;
  }

  public void setUser(UserDAO userID) {
    this.user = userID;
  }

  public CommunityDAO getCommunity() {
    return community;
  }

  public void setCommunity(CommunityDAO communityID) {
    this.community = communityID;
  }

  public String getText() {
    return this.text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
