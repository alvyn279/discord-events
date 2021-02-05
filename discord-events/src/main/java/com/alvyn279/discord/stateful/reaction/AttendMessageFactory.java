package com.alvyn279.discord.stateful.reaction;

import com.alvyn279.discord.domain.DiscordEvent;
import com.alvyn279.discord.repository.DiscordEventReactiveRepository;
import com.google.inject.Inject;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.entity.Guild;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

/**
 * Factory for {@link AttendMessage}
 */
public class AttendMessageFactory {

    private final DiscordEventReactiveRepository repository;

    @Inject
    public AttendMessageFactory(DiscordEventReactiveRepository repository) {
        this.repository = repository;
    }

    /**
     * Factory method to create an {@link AttendMessage}. This is needed because
     * it will remove the need for the caller to know about the repository.
     *
     * @param guild         guild
     * @param messageId     bot message id (reactable message)
     * @param discordEvents list of discord events the reactable Message displays
     * @return {@link AttendMessage}
     */
    public AttendMessage createAttendMessage(Guild guild, String messageId, List<DiscordEvent> discordEvents) {
        return AttendMessage.builder()
            .guild(guild)
            .messageId(messageId)
            .discordEvents(discordEvents)
            .build();
    }

    /**
     * Object for messages sent by the bot in response
     * to `!attend-event` command.
     */
    @Data
    @Builder
    private class AttendMessage implements ReactableMessage {
        @NonNull
        private final Guild guild;
        @NonNull
        private final List<DiscordEvent> discordEvents;
        @NonNull
        private final String messageId;

        @Override
        public void onReactionAdd(ReactionAddEvent event) {
            // TODO: use repository to store reactions
        }

        @Override
        public void onReactionRemove(ReactionRemoveEvent event) {
            // TODO: use repository to remove reactions
        }
    }
}

