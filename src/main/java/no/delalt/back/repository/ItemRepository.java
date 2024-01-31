package no.delalt.back.repository;

import no.delalt.back.model.dao.ItemDAO;
import no.delalt.back.model.dao.UserDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<ItemDAO, String> {
  List<ItemDAO> findAllByUser(UserDAO user);
  List<ItemDAO> findAllByUserAndIsDeletedIsFalse(UserDAO user);
}
