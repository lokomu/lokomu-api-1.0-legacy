package no.delalt.back.service.validation;

import no.delalt.back.model.dao.ImageDAO;
import no.delalt.back.service.retrieval.ImageRetrievalService;
import no.delalt.back.util.SecurityUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
public class ImageValidationService {
  private final ImageRetrievalService imageRetrievalService;

  public ImageValidationService(ImageRetrievalService imageRetrievalService) {
    this.imageRetrievalService = imageRetrievalService;
  }

  /**
   * Validates if the image is owned by the user.
   *
   * @param  imageID   the ID of the image to validate
   * @throws ResponseStatusException   if the image is not owned by the user
   */
  public void validateImageOwnedByUser(String imageID) {
    ImageDAO imageDAO = imageRetrievalService.getImage(imageID);
    if (!checkImageOwner(imageDAO)) {
      throw new ResponseStatusException(
        HttpStatus.FORBIDDEN,
        "Image not owned by user."
      );
    }
  }

  /**
   * Checks if the owner of the image is the authenticated user.
   *
   * @param  imageDAO  the ImageDAO object representing the image
   * @return           true if the owner of the image matches the authenticated user, false otherwise
   */
  private static boolean checkImageOwner(ImageDAO imageDAO) {
    return Objects.equals(
      imageDAO.getUser().getUserID(),
      SecurityUtil.getAuthenticatedAccountID()
    );
  }
}
