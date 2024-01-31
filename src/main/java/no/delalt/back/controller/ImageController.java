package no.delalt.back.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;
import no.delalt.back.configuration.validation.ValidID;
import no.delalt.back.model.dao.ImageDAO;
import no.delalt.back.response.id.ImageIDResponse;
import no.delalt.back.service.retrieval.ImageRetrievalService;
import no.delalt.back.service.save.ImageSaveService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Validated
@RequestMapping("/image")
public class ImageController {
  private final ImageSaveService imageSaveService;
  private final ImageRetrievalService imageRetrievalService;

  public ImageController(
    ImageSaveService imageSaveService,
    ImageRetrievalService imageRetrievalService
  ) {
    this.imageSaveService = imageSaveService;
    this.imageRetrievalService = imageRetrievalService;
  }

  //TODO Validate that it is a real image
  /**
   * Adds an image to the system.
   *
   * @param  image  the byte array representing the image to be added
   * @return        the response entity containing the image ID
   * @throws ResponseStatusException if an error occurs while adding the image
   */
  @Operation(summary = "Adds an image to the system")
  @PostMapping(
    path = "/",
    consumes = { MediaType.IMAGE_JPEG_VALUE },
    produces = "application/json"
  )
  public ResponseEntity<ImageIDResponse> addImage(@RequestBody byte[] image)
    throws ResponseStatusException {
    String imageID = imageSaveService.addImage(image);
    ImageIDResponse imageIDResponse = new ImageIDResponse(imageID);

    return ResponseEntity.ok(imageIDResponse);
  }

  /**
   * Retrieves an image from the system.
   *
   * @param  imageID  the ID of the image to be retrieved
   * @return          the response entity containing the image
   * @throws ResponseStatusException if an error occurs while retrieving the image
   */
  @Operation(summary = "Retrieves an image from the system")
  @GetMapping(
    path = "/{imageID}",
    produces = { MediaType.IMAGE_JPEG_VALUE, "application/json" }
  )
  public ResponseEntity<byte[]> getImage(
    @PathVariable @ValidID @NotBlank String imageID
  )
    throws ResponseStatusException {
    ImageDAO imageDAO = imageRetrievalService.getImage(imageID);

    return ResponseEntity.ok(imageDAO.getImage());
  }
}
