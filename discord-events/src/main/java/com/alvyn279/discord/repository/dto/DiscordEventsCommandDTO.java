package com.alvyn279.discord.repository.dto;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

/**
 * Generic POJO that holds information regarding the "shadow" inputs
 * that are supplied with a command.
 */
@Data
@SuperBuilder
public class DiscordEventsCommandDTO {
    @NonNull
    private final String guildId;

    private final String userId;
}
