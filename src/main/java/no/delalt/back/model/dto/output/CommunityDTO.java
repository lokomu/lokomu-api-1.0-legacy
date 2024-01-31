package no.delalt.back.model.dto.output;

import no.delalt.back.model.dao.CommunityDAO;

public record CommunityDTO(String communityID, String name, String description, short visibility, String location, String image) {

  public CommunityDTO(CommunityDAO communityDAO) {
    this(
            communityDAO.getCommunityID(),
            communityDAO.getName(),
            communityDAO.getDescription(),
            communityDAO.getVisibility(),
            communityDAO.getLocation(),
            communityDAO.getImage()
    );
  }
}