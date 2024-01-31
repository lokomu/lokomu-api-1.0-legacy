package no.delalt.back.model.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import no.delalt.back.configuration.validation.ValidID;

@Entity
@Table(name = "invite", schema = "public")
public class InviteDAO {
  @Id
  @ValidID
  @NotNull
  @Column(
    name = "invite_id",
    columnDefinition = "VARCHAR(21)",
    nullable = false
  )
  private String inviteID;

  @NotNull
  @Column(name = "created_at", nullable = false)
  private long createdAt;

  @NotNull
  @Column(name = "is_expired", nullable = false)
  private boolean isExpired;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserDAO user;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "community_id", nullable = false)
  private CommunityDAO community;

  public InviteDAO() {}

  public String getInviteID() {
    return inviteID;
  }

  public void setInviteID(String inviteID) {
    this.inviteID = inviteID;
  }

  public UserDAO getUser() {
    return user;
  }

  public void setUser(UserDAO user) {
    this.user = user;
  }

  public CommunityDAO getCommunity() {
    return community;
  }

  public void setCommunity(CommunityDAO community) {
    this.community = community;
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(long createdAt) {
    this.createdAt = createdAt;
  }

  public boolean getIsExpired() {
    return isExpired;
  }

  public void setIsExpired(boolean expired) {
    isExpired = expired;
  }
}
