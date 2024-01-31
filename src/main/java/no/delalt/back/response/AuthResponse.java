package no.delalt.back.response;

import no.delalt.back.model.dto.output.UserDTO;

public record AuthResponse (String authToken,
        UserDTO user) {
}
