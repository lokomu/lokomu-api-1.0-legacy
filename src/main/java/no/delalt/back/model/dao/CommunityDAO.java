package no.delalt.back.model.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import no.delalt.back.configuration.validation.ValidID;
import org.locationtech.jts.geom.Point;

@Entity
@Table(
  name = "community",
  schema = "public",
  indexes = { @Index(name = "idx_visibility", columnList = "visibility") }
)
public class CommunityDAO {
  @Id
  @ValidID
  @NotNull
  @Column(
    name = "community_id",
    columnDefinition = "VARCHAR(21)",
    nullable = false
  )
  private String communityID;

  @NotNull
  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description")
  private String description;

  @NotNull
  @Min(0)
  @Max(2)
  @Column(name = "visibility", nullable = false)
  private short visibility;

  @Column(name = "location")
  private String location;

  @Column(name = "image")
  private String image;

  @Column(name = "coordinates", columnDefinition = "geometry(Point,4326)")
  private Point coordinates;

  @PrePersist
  @PreUpdate
  public void enforceNullCoordinatesForInvisibleCommunities() {
    if (visibility == 0) {
      coordinates = null;
    }
  }

  public CommunityDAO() {}

  public String getCommunityID() {
    return this.communityID;
  }

  public void setCommunityID(String communityID) {
    this.communityID = communityID;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public short getVisibility() {
    return this.visibility;
  }

  public void setVisibility(short visibility) {
    this.visibility = visibility;
  }

  public String getLocation() {
    return this.location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public void setCoordinates(Point coordinates) {
    this.coordinates = coordinates;
  }

  public String getImage() {
    return this.image;
  }

  public void setImage(String image) {
    this.image = image;
  }
}
