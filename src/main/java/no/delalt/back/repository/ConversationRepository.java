package no.delalt.back.repository;

import no.delalt.back.model.dao.ConversationDAO;
import no.delalt.back.model.dao.UserDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationRepository
  extends JpaRepository<ConversationDAO, String> {
  Optional<ConversationDAO> findByUser1AndUser2(
    UserDAO user1DAO,
    UserDAO user2DAO
  );
}
