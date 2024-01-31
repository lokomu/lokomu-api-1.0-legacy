package no.delalt.back.model.dto.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import no.delalt.back.configuration.validation.ValidID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserModifyDTO(@NotBlank @Size(max = 50) String firstName,
                            @NotBlank @Size(max = 50) String lastName,
                            @ValidID String image) {}