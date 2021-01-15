package com.alvyn279.discord.strategy;

import com.alvyn279.discord.domain.DiscordCommandContext;
import com.alvyn279.discord.domain.DiscordEvent;
import reactor.core.publisher.Mono;

/**
 * Strategy interface to perform a delete operation on a
 * {@link DiscordEvent}s in the data store.
 */
public interface DeleteDiscordEventsStrategy {

    /**
     * Executes the deletion of discord events.
     * @param context Discord context with arguments
     * @return Mono<Void>
     */
    public Mono<Void> execute(DiscordCommandContext context);
}
