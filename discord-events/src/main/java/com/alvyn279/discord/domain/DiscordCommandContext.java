package com.alvyn279.discord.domain;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

/**
 * Command arguments and discord context
 */
@Data
@Builder
public class DiscordCommandContext {
    @NonNull
    private final List<String> tokens;

    @NonNull
    private final MessageCreateEvent messageCreateEvent;

    @NonNull
    private final Guild guild;
}
