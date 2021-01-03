package com.alvyn279.discord.repository;

import com.alvyn279.discord.domain.DiscordEvent;
import reactor.core.publisher.Mono;

/**
 * Declares all the logical reads and write for the @link{DiscordEvent}
 * entity.
 */
public interface DiscordEventReactiveRepository {

    /**
     * Saves a {@link DiscordEvent} to a datastore in a reactive manner
     * @param discordEvent DiscordEvent built on info obtained from
     *                     a dispatched discord command
     * @return Mono<DiscordEvent>
     */
    Mono<DiscordEvent> saveDiscordEvent(DiscordEvent discordEvent);
}
