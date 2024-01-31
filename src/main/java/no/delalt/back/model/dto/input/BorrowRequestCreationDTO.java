package no.delalt.back.model.dto.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import no.delalt.back.configuration.validation.ValidID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BorrowRequestCreationDTO(
        @ValidID @NotBlank String itemID,
        @ValidID @NotBlank String communityID,
        @Size(max = 255) String message,
        @NotBlank @Size(max = 10) String startDate,
        @NotBlank @Size(max = 10) String endDate
) {}