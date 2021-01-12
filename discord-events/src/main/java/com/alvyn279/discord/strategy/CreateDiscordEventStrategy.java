package com.alvyn279.discord.strategy;

import com.alvyn279.discord.domain.DiscordEvent;
import com.alvyn279.discord.domain.DiscordEventCommandContext;
import reactor.core.publisher.Mono;


/**
 * Strategy interface to perform the creation of a {@link DiscordEvent}
 */
public interface CreateDiscordEventStrategy {

    /**
     * Executes the creation of the discord event.
     * @param context Discord context with arguments
     * @return Mono<Void>
     */
    public Mono<Void> execute(DiscordEventCommandContext context);
}
