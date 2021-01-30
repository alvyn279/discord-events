package com.alvyn279.discord.repository.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

/**
 * POJO that holds information regarding the inputs that users
 * add to the `delete-event` command while using DiscordEventsBot.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class DeleteDiscordEventCommandDTO extends DiscordEventsCommandDTO {
    @NonNull
    private final String deleteCode;
}
