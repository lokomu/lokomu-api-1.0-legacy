package no.delalt.back.model.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import no.delalt.back.model.id.UserGatheringID;

@Entity
@Table(name = "user_gathering", schema = "public")
@IdClass(UserGatheringID.class)
public class UserGatheringDAO {
  @Id
  @NotNull
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserDAO user;

  @Id
  @NotNull
  @ManyToOne
  @JoinColumn(name = "gathering_id", nullable = false)
  private GatheringDAO gathering;

  public UserGatheringDAO() {}

  public UserGatheringDAO(UserDAO user, GatheringDAO gathering) {
    this.user = user;
    this.gathering = gathering;
  }

  public UserDAO getUser() {
    return user;
  }

  public GatheringDAO getGathering() {
    return gathering;
  }

  public void setUser(UserDAO user) {
    this.user = user;
  }

  public void setGathering(GatheringDAO gathering) {
    this.gathering = gathering;
  }
}
