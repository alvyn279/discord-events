package com.alvyn279.discord.repository;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * POJO that holds information regarding the inputs that users
 * add to the `delete-event` command while using DiscordEventsBot.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class DeleteDiscordEventCommandArgs extends DiscordEventsCommandArgs {
    private final String deleteCode;
}
