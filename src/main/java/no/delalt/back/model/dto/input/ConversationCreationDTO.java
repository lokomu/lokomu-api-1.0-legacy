package no.delalt.back.model.dto.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import no.delalt.back.configuration.validation.ValidID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ConversationCreationDTO(@ValidID String communityID, @NotNull boolean byRequest) {
}
