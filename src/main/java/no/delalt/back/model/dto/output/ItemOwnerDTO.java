package no.delalt.back.model.dto.output;

import no.delalt.back.model.dao.ItemDAO;

public record ItemOwnerDTO(String title, String description, String[] communityIDs, String image) {

  public ItemOwnerDTO(ItemDAO itemDAO, String[] communityIDs) {
    this(
            itemDAO.getTitle(),
            itemDAO.getDescription(),
            communityIDs,
            itemDAO.getImage()
    );
  }
}