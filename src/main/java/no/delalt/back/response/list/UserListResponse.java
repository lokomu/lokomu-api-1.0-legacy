package no.delalt.back.response.list;

import no.delalt.back.model.dto.output.UserDTO;

import java.util.List;

public record UserListResponse(List<UserDTO> payload) {
}
