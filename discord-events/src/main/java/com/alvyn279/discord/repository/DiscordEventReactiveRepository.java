package com.alvyn279.discord.repository;

import com.alvyn279.discord.domain.DiscordEvent;
import com.alvyn279.discord.domain.ListDiscordEventsCommandInput;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Declares all the logical reads and write for the @link{DiscordEvent}
 * entity.
 */
public interface DiscordEventReactiveRepository {

    /**
     * Saves a {@link DiscordEvent} to a datastore in a reactive manner
     * @param discordEvent DiscordEvent built on info obtained from
     *                     a dispatched discord command
     * @return Mono<DiscordEvent> saved events
     */
    Mono<DiscordEvent> saveDiscordEvent(DiscordEvent discordEvent);

    /**
     * Fetches upcoming {@link DiscordEvent}s from a datastore in a
     * reactive manner
     *
     * @param input {@link ListDiscordEventsCommandInput} with `upcomingLimit`,
     *                                                  `guildId`, `currentDate`
     * @return Mono<List<DiscordEvent>> events
     */
    Mono<List<DiscordEvent>> listDiscordEventsByUpcoming(ListDiscordEventsCommandInput input);

    /**
     * Fetches {@link DiscordEvent}s on a given date from a datastore in a
     * reactive manner
     *
     * @param input {@link ListDiscordEventsCommandInput} with `date`, `guildId`
     * @return Mono<List<DiscordEvent>> events
     */
    Mono<List<DiscordEvent>> listDiscordEventsByDate(ListDiscordEventsCommandInput input);

    /**
     * Fetches {@link DiscordEvent}s in a given date range from a datastore in a
     * reactive manner
     *
     * @param input {@link ListDiscordEventsCommandInput} with `startDate`,`endDate`, `guildId`
     * @return Mono<List<DiscordEvent>> events
     */
    Mono<List<DiscordEvent>> listDiscordEventsByDateRange(ListDiscordEventsCommandInput input);
}
