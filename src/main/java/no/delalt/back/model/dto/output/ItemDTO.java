package no.delalt.back.model.dto.output;

import no.delalt.back.model.dao.ItemDAO;

public record ItemDTO(String itemID, String title, String description, UserDTO user, String image) {

  public ItemDTO(ItemDAO itemDAO) {
    this(
            itemDAO.getItemID(),
            itemDAO.getTitle(),
            itemDAO.getDescription(),
            new UserDTO(itemDAO.getUser()),
            itemDAO.getImage()
    );
  }
}
