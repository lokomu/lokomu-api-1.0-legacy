package no.delalt.back.service.save;

import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserSaveService {
  private final UserRepository userRepository;

  public UserSaveService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Saves the given `UserDAO` object to the database.
   *
   * @param  user  the `UserDAO` object to be saved
   */
  public void saveUser(UserDAO user) {
    userRepository.save(user);
  }
}
