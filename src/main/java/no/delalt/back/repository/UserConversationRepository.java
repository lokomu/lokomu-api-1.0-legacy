package no.delalt.back.repository;

import no.delalt.back.model.dao.UserConversationDAO;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.model.id.UserConversationID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserConversationRepository
  extends JpaRepository<UserConversationDAO, UserConversationID> {
  List<UserConversationDAO> findByUser(UserDAO user);
}
