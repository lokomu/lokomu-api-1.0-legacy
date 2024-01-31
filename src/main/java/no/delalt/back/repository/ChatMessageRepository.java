package no.delalt.back.repository;

import no.delalt.back.model.dao.ChatMessageDAO;
import no.delalt.back.model.dao.ConversationDAO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository
  extends JpaRepository<ChatMessageDAO, String> {
  List<ChatMessageDAO> findByConversation(
    ConversationDAO conversation,
    Pageable pageable
  );
}
