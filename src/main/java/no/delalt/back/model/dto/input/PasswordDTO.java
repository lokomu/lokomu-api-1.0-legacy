package no.delalt.back.model.dto.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PasswordDTO(
        @NotBlank @Size(min = 8, max = 64) String oldPassword,
        @NotBlank @Size(min = 8, max = 64) String newPassword
) {}