package no.delalt.back.model.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import no.delalt.back.configuration.validation.ValidID;
import org.locationtech.jts.geom.Point;

import java.time.LocalDate;

@Entity
@Table(
  name = "account",
  schema = "public",
  indexes = { @Index(name = "email_idx", columnList = "email", unique = true) }
)
public class UserDAO {
  @Id
  @ValidID
  @NotNull
  @Column(name = "user_id", columnDefinition = "VARCHAR(21)", nullable = false)
  private String userID;

  @NotNull
  @Column(name = "email", nullable = false)
  private String email;

  @NotNull
  @Column(name = "first_name", nullable = false)
  private String firstName;

  @NotNull
  @Column(name = "last_name", nullable = false)
  private String lastName;

  @NotNull
  @Column(
    name = "coordinates",
    columnDefinition = "geometry(Point,4326)",
    nullable = false
  )
  private Point coordinates;

  @NotNull
  @Column(name = "last_location_update", nullable = false)
  private LocalDate lastLocationUpdate;

  @Column(name = "image")
  private String image;

  @NotNull
  @Column(name = "hash", nullable = false)
  private String hash;

  @Column(name = "deleted_at")
  private LocalDate deletedAt;

  public UserDAO() {}

  public String getUserID() {
    return this.userID;
  }

  public void setUserID(String userID) {
    this.userID = userID;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getFirstName() {
    return this.firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public Point getCoordinates() {
    return this.coordinates;
  }

  public void setCoordinates(Point coordinates) {
    this.coordinates = coordinates;
  }

  public LocalDate getLastLocationUpdate() {
    return lastLocationUpdate;
  }

  public void setLastLocationUpdate(LocalDate lastLocationUpdate) {
    this.lastLocationUpdate = lastLocationUpdate;
  }

  public String getImage() {
    return this.image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getHash() {
    return hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }

  public LocalDate getDeletedAt() {
    return deletedAt;
  }

  public void setDeletedAt(LocalDate deletedAt) {
    this.deletedAt = deletedAt;
  }
}
