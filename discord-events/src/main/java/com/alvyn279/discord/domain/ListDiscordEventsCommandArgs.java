package com.alvyn279.discord.domain;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * POJO that holds information regarding the inputs that users
 * add to the `list-events` command while using DiscordEventsBot.
 */
@Data
@Builder
public class ListDiscordEventsCommandArgs {
    String guildId;
    String userId;
    Integer upcomingLimit;
    Instant currentDateTime;
    Instant startDateTime;
    Instant endDateTime;
}
