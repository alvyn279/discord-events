package com.alvyn279.discord.stateful.reaction;

import com.alvyn279.discord.domain.DiscordEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.entity.Guild;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

/**
 * Object for messages sent by the bot in response
 * to `!attend-event` command.
 */
@Data
@Builder
public class AttendMessage implements ReactableMessage {
    @NonNull
    private final Guild guild;
    @NonNull
    private final List<DiscordEvent> discordEvents;
    @NonNull
    private final String messageId;

    @Override
    public void onReactionAdd(ReactionAddEvent event) {
        // TODO
    }

    @Override
    public void onReactionRemove(ReactionRemoveEvent event) {
        // TODO
    }
}
