package com.alvyn279.discord.repository;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * Generic POJO that holds information regarding the "shadow" inputs
 * that are supplied with a command.
 */
@Data
@SuperBuilder
public class DiscordEventsCommandArgs {
    private final String guildId;
    private final String userId;
}
