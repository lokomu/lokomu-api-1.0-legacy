package no.delalt.back.service.retrieval;

import no.delalt.back.model.dao.ImageDAO;
import no.delalt.back.repository.ImageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ImageRetrievalService {
  private final ImageRepository imageRepository;

  public ImageRetrievalService(ImageRepository imageRepository) {
    this.imageRepository = imageRepository;
  }

  /**
   * Retrieves an image from the image repository based on the given image ID.
   *
   * @param  imageID  the ID of the image to retrieve
   * @return          the ImageDAO object representing the retrieved image
   */
  @Transactional(readOnly = true)
  public ImageDAO getImage(String imageID) {
    return imageRepository
      .findById(imageID)
      .orElseThrow(
        () ->
          new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found")
      );
  }
}
