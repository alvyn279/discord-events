package com.alvyn279.discord.async;

import com.alvyn279.discord.domain.DiscordCommandContext;
import reactor.core.publisher.Mono;

/**
 * Interface for the strategy on the !remind-events
 * command
 */
public interface EventReminderServiceStrategy {

    /**
     * Execute function that needs to be implemented
     * for a concrete strategy.
     *
     * @param context discord context
     * @return Mono<void>
     */
    public Mono<Void> execute(DiscordCommandContext context);
}
