package no.delalt.back.service.deletion;

import no.delalt.back.model.dao.InviteDAO;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.repository.InviteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InviteDeletionService {
  private final InviteRepository inviteRepository;

  public InviteDeletionService(InviteRepository inviteRepository) {
    this.inviteRepository = inviteRepository;
  }

  public void deleteInvitesByUser(UserDAO userDAO) {
    List<InviteDAO> inviteDAOList = inviteRepository.findAllByUser(userDAO);

    for (InviteDAO inviteDAO : inviteDAOList) {
      inviteDAO.setIsExpired(true);
    }
    inviteRepository.saveAll(inviteDAOList);
  }
}
