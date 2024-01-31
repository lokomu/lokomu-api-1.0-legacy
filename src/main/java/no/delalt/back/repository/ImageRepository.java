package no.delalt.back.repository;

import no.delalt.back.model.dao.ImageDAO;
import no.delalt.back.model.dao.UserDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<ImageDAO, String> {
  List<ImageDAO> findAllByUser(UserDAO userDAO);
}
