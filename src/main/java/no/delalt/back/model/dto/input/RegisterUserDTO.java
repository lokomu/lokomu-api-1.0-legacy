package no.delalt.back.model.dto.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RegisterUserDTO(@Email @NotBlank String email,
                              @NotBlank @Size(min = 8, max = 64) String password,
                              @NotBlank @Size(max = 50) String firstName,
                              @NotBlank @Size(max = 50) String lastName,
                              @Valid CoordinatesCreationDTO coordinates) {}
