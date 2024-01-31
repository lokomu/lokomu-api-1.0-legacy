package no.delalt.back.model.object;

import no.delalt.back.model.dao.UserDAO;

public record UserPair(UserDAO user1, UserDAO user2) {}