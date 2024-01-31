package no.delalt.back.service.deletion;

import no.delalt.back.model.dao.ImageDAO;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.repository.ImageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ImageDeletionService {
  private final ImageRepository imageRepository;

  public ImageDeletionService(ImageRepository imageRepository) {
    this.imageRepository = imageRepository;
  }

  /**
   * Deletes an image from the repository.
   *
   * @param  imageID  the ID of the image to be deleted
   */
  public void deleteImage(String imageID) {
    ImageDAO image = imageRepository
      .findById(imageID)
      .orElseThrow(
        () ->
          new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found")
      );
    imageRepository.delete(image);
  }

  public void deleteImagesForUser(UserDAO userDAO) {
    List<ImageDAO> images = imageRepository.findAllByUser(userDAO);
    imageRepository.deleteAll(images);
  }
}
