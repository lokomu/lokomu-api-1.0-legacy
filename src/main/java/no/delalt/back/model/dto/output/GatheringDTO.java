package no.delalt.back.model.dto.output;

import no.delalt.back.model.dao.UserDAO;

import java.time.LocalDateTime;

public record GatheringDTO (String gatheringID, String title, String description, UserDTO user, String dateTime) {
    public GatheringDTO(String gatheringID, String title, String description, UserDAO userDAO, LocalDateTime dateTime) {
        this(
                gatheringID,
                title,
                description,
                new UserDTO(userDAO),
                dateTime.toString()
        );
    }
}
