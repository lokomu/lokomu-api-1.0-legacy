package no.delalt.back.model.dto.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import no.delalt.back.configuration.validation.StringArraySize;
import no.delalt.back.configuration.validation.ValidID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ItemCreationDTO(
        @NotBlank @Size(max = 50) String title,
        @Size(max = 255) String description,
        @NotNull @NotEmpty @Size(max = 30) @StringArraySize String[] communityIDs,
        @ValidID String image
) {}