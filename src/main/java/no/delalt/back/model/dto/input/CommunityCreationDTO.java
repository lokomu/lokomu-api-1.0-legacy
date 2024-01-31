package no.delalt.back.model.dto.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import no.delalt.back.configuration.validation.ValidID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CommunityCreationDTO(
        @NotBlank @Size(max = 50) String name,
        @NotBlank @Size(max = 255) String description,
        @NotNull @Min(0) @Max(2) short visibility,
        @NotBlank @Size(max = 50) String location,
        @ValidID String image
) {}