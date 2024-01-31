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

import java.time.LocalDateTime;

@Entity
@Table(
  name = "gathering",
  schema = "public",
  indexes = {
    @Index(name = "idx_is_expired", columnList = "is_expired"),
    @Index(name = "idx_community_id", columnList = "community_id")
  }
)
public class GatheringDAO {
  @Id
  @ValidID
  @NotNull
  @Column(
    name = "gathering_id",
    columnDefinition = "VARCHAR(21)",
    nullable = false
  )
  private String gatheringID;

  @NotNull
  @Column(name = "date_and_time", nullable = false)
  private LocalDateTime dateAndTime;

  @NotNull
  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "description")
  private String description;

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

  public GatheringDAO() {}

  public String getGatheringID() {
    return gatheringID;
  }

  public void setGatheringID(String gatheringID) {
    this.gatheringID = gatheringID;
  }

  public LocalDateTime getDateAndTime() {
    return dateAndTime;
  }

  public void setDateAndTime(LocalDateTime dateAndTime) {
    this.dateAndTime = dateAndTime;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean getIsExpired() {
    return isExpired;
  }

  public void setIsExpired(boolean isExpired) {
    this.isExpired = isExpired;
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
}
