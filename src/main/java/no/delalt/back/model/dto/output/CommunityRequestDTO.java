package no.delalt.back.model.dto.output;

import no.delalt.back.model.dao.CommunityRequestDAO;

public record CommunityRequestDTO(String communityRequestID, String text, UserDTO user, CommunityDTO community) {

  public CommunityRequestDTO(CommunityRequestDAO communityRequestDAO) {
    this(
            communityRequestDAO.getCommunityRequestID(),
            communityRequestDAO.getText(),
            new UserDTO(communityRequestDAO.getUser()),
            new CommunityDTO(communityRequestDAO.getCommunity())
    );
  }
}