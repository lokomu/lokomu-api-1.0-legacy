package no.delalt.back.model.dto.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GatheringCreationDTO(
        @NotBlank @Size(max = 50) String title,
        @Size(max = 255) String description,
        @NotBlank @Size(max = 10) String dateAndTime
) {}