package no.delalt.back.model.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import no.delalt.back.model.id.UserCommunityID;

@Entity
@Table(name = "user_community", schema = "public")
@IdClass(UserCommunityID.class)
public class UserCommunityDAO {
  @Id
  @NotNull
  @ManyToOne
  @JoinColumn(name = "community_id", nullable = false)
  private CommunityDAO community;

  @Id
  @NotNull
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserDAO user;

  @NotNull
  @Column(name = "is_administrator", nullable = false)
  private boolean isAdministrator;

  public UserCommunityDAO() {}

  public UserCommunityDAO(
    CommunityDAO community,
    UserDAO user,
    boolean isAdministrator
  ) {
    this.community = community;
    this.user = user;
    this.isAdministrator = isAdministrator;
  }

  public CommunityDAO getCommunity() {
    return community;
  }

  public UserDAO getUser() {
    return user;
  }

  public boolean getIsAdministrator() {
    return isAdministrator;
  }

  public void setIsAdministrator(boolean administrator) {
    isAdministrator = administrator;
  }
}
