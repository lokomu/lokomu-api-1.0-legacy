package no.delalt.back.repository;

import no.delalt.back.model.dao.InviteDAO;
import no.delalt.back.model.dao.UserDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InviteRepository extends JpaRepository<InviteDAO, String> {
  List<InviteDAO> findAllByUser(UserDAO user);
}
