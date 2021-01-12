package com.alvyn279.discord.strategy;

import com.alvyn279.discord.domain.DiscordEvent;
import com.alvyn279.discord.domain.DiscordCommandContext;
import reactor.core.publisher.Mono;

/**
 * Strategy interface to perform a list operation of
 * all {@link DiscordEvent}s in the data store.
 */
public interface ListDiscordEventsStrategy {

    /**
     * Executes the listing all discord events.
     * @param context Discord context with arguments
     * @return Mono<Void>
     */
    public Mono<Void> execute(DiscordCommandContext context);
}
