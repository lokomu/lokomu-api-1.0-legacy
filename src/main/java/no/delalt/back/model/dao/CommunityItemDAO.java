package no.delalt.back.model.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import no.delalt.back.model.id.CommunityItemID;

@Entity
@Table(name = "community_item", schema = "public")
@IdClass(CommunityItemID.class)
public class CommunityItemDAO {
  @Id
  @NotNull
  @ManyToOne
  @JoinColumn(name = "community_id", nullable = false)
  private CommunityDAO community;

  @Id
  @NotNull
  @ManyToOne
  @JoinColumn(name = "item_id", nullable = false)
  private ItemDAO item;

  public CommunityItemDAO() {}

  public CommunityItemDAO(CommunityDAO community, ItemDAO item) {
    this.community = community;
    this.item = item;
  }

  public CommunityDAO getCommunity() {
    return community;
  }

  public void setCommunity(CommunityDAO community) {
    this.community = community;
  }

  public ItemDAO getItem() {
    return item;
  }

  public void setItem(ItemDAO item) {
    this.item = item;
  }
}
