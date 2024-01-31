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
  name = "item",
  schema = "public",
  indexes = { @Index(name = "idx_user", columnList = "user_id") }
)
public class ItemDAO {
  @Id
  @ValidID
  @NotNull
  @Column(name = "item_id", columnDefinition = "VARCHAR(21)", nullable = false)
  private String itemID;

  @NotNull
  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "description")
  private String description;

  @Column(name = "image")
  private String image;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserDAO user;

  @NotNull
  @Column(name = "is_for_giving", nullable = false)
  private boolean isForGiving;

  @NotNull
  @Column(name = "is_deleted", nullable = false)
  private boolean isDeleted;

  public ItemDAO() {}

  public String getItemID() {
    return this.itemID;
  }

  public void setItemID(String itemID) {
    this.itemID = itemID;
  }

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean getIsDeleted() {
    return this.isDeleted;
  }

  public void setIsDeleted(boolean isDeleted) {
    this.isDeleted = isDeleted;
  }

  public UserDAO getUser() {
    return this.user;
  }

  public void setUser(UserDAO user) {
    this.user = user;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public boolean getIsForGiving() {
    return isForGiving;
  }

  public void setIsForGiving(boolean isForGiving) {
    this.isForGiving = isForGiving;
  }
}
