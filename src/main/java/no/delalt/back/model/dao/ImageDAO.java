package no.delalt.back.model.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import no.delalt.back.configuration.validation.ValidID;

@Entity
@Table(name = "image", schema = "public")
public class ImageDAO {
  @Id
  @ValidID
  @NotNull
  @Column(name = "image_id", columnDefinition = "VARCHAR(21)", nullable = false)
  private String imageID;

  @NotNull
  @Lob
  @Column(length = 1048576, name = "image", nullable = false)
  private byte[] image;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserDAO user;

  public ImageDAO() {}

  public ImageDAO(String imageID, byte[] image, UserDAO user) {
    this.imageID = imageID;
    this.image = image;
    this.user = user;
  }

  public String getImageID() {
    return imageID;
  }

  public void setImageID(String imageID) {
    this.imageID = imageID;
  }

  public byte[] getImage() {
    return image;
  }

  public void setImage(byte[] image) {
    this.image = image;
  }

  public UserDAO getUser() {
    return user;
  }

  public void setUser(UserDAO user) {
    this.user = user;
  }
}
