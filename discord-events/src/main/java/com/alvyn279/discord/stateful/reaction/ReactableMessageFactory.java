package com.alvyn279.discord.stateful.reaction;

import com.alvyn279.discord.domain.DiscordEvent;
import com.alvyn279.discord.repository.DiscordEventReactiveRepository;
import com.google.inject.Inject;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;

import java.util.List;

/**
 * Factory for {@link ReactableMessage}.
 * <p>
 * Use injector to get an instance of this factory.
 */
public class ReactableMessageFactory {

    private final DiscordEventReactiveRepository repository;

    @Inject
    public ReactableMessageFactory(DiscordEventReactiveRepository repository) {
        this.repository = repository;
    }

    /**
     * Factory method to create an {@link AttendMessage}. This is useful because
     * it will remove the need for the caller to know about the repository.
     *
     * @param guild         guild
     * @param message       bot message id (reactable message)
     * @param discordEvents list of discord events the reactable Message displays
     * @return {@link AttendMessage}
     */
    public AttendMessage createAttendMessage(Guild guild, Message message, List<DiscordEvent> discordEvents) {
        return AttendMessage.builder()
            .guild(guild)
            .message(message)
            .discordEvents(discordEvents)
            .repository(repository)
            .build();
    }
}

