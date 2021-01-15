package com.alvyn279.discord.repository;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

/**
 * POJO that holds information regarding the inputs that users
 * add to the `list-events` command while using DiscordEventsBot.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class ListDiscordEventsCommandArgs extends DiscordEventsCommandArgs {
    private final Integer upcomingLimit;
    private final Instant currentDateTime;
    private final Instant startDateTime;
    private final Instant endDateTime;
}
