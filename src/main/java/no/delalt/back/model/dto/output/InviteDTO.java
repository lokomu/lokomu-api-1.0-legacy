package no.delalt.back.model.dto.output;

import no.delalt.back.model.dao.InviteDAO;

public record InviteDTO(String inviteID, String communityID) {

  public InviteDTO(InviteDAO inviteDAO) {
    this(
            inviteDAO.getInviteID(),
            inviteDAO.getCommunity().getCommunityID()
    );
  }
}