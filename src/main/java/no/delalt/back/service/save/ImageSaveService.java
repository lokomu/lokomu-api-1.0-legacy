package no.delalt.back.service.save;

import no.delalt.back.model.dao.ImageDAO;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.repository.ImageRepository;
import no.delalt.back.service.validation.UserValidationService;
import no.delalt.back.util.NanoIdGenerator;
import no.delalt.back.util.SecurityUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ImageSaveService {
  private final ImageRepository imageRepository;
  private final UserValidationService userValidationService;
  private final static int MINIMUM_SIZE = 500;
  private final static int MAXIMUM_SIZE = 1000000;

  public ImageSaveService(
    ImageRepository imageRepository,
    UserValidationService userValidationService
  ) {
    this.imageRepository = imageRepository;
    this.userValidationService = userValidationService;
  }

  /**
   * Adds an image to the database.
   *
   * @param  image  the byte array of the image to be added
   * @return        the ID of the saved image
   * @throws ResponseStatusException if the image size is not between 500 and 1,000,000 bytes
   */
  @Transactional
  public String addImage(byte[] image) throws ResponseStatusException {
    if (image.length < MINIMUM_SIZE || image.length > MAXIMUM_SIZE) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "The image size should be between 500 and 1,000,000 bytes"
      );
    }

    UserDAO user = userValidationService.validateUserExistsAndReturn(
      SecurityUtil.getAuthenticatedAccountID()
    );
    String uniqueID = NanoIdGenerator.generateNanoID();

    ImageDAO imageDAO = new ImageDAO(uniqueID, image, user);

    imageRepository.save(imageDAO);
    return uniqueID;
  }
}
