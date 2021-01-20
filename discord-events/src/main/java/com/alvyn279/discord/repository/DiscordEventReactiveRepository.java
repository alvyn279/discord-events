package com.alvyn279.discord.repository;

import com.alvyn279.discord.domain.DiscordEvent;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Declares all the logical reads and write for the @link{DiscordEvent}
 * entity.
 */
public interface DiscordEventReactiveRepository {

    /**
     * Deletes one {@link DiscordEvent} identified with messageId
     * from a datastore in a reactive manner.
     *
     * @param args {@link DeleteDiscordEventCommandArgs} with `guildId`, `userId`, `deleteCode`
     * @return Mono<DiscordEvent> deleted event
     */
    Mono<DiscordEvent> deleteDiscordEvent(DeleteDiscordEventCommandArgs args);

    /**
     * Fetches upcoming {@link DiscordEvent}s from a datastore in a
     * reactive manner. Looks for the next `upcomingLimit` events.
     *
     * @param args {@link ListDiscordEventsCommandArgs} with `upcomingLimit`,
     *             `guildId`, `currentDateTime`
     * @return Mono<List < DiscordEvent>> events
     */
    Mono<List<DiscordEvent>> listDiscordEventsByUpcomingWithLimit(ListDiscordEventsCommandArgs args);

    /**
     * Fetches upcoming {@link DiscordEvent}s from a datastore in a
     * reactive manner. Looks for the events within the next `upcomingTimeLimit`
     * timeframe.
     *
     * @param args {@link ListDiscordEventsCommandArgs} with `upcomingTimeLimit`,
     *             `guildId`, `currentDateTime`
     * @return Mono<List < DiscordEvent>> events
     */
    Mono<List<DiscordEvent>> listDiscordEventsByUpcomingWithTimeLimit(ListDiscordEventsCommandArgs args);

    /**
     * Fetches {@link DiscordEvent}s in a given date time range from a datastore in a
     * reactive manner. Looks for the events between two date-times.
     *
     * @param args {@link ListDiscordEventsCommandArgs} with `startDateTime`,`endDateTime`, `guildId`
     * @return Mono<List < DiscordEvent>> events
     */
    Mono<List<DiscordEvent>> listDiscordEventsByDateTimeRange(ListDiscordEventsCommandArgs args);

    /**
     * Fetches {@link DiscordEvent}s created by given user from a datastore in a
     * reactive manner. Looks for the events created by a user.
     *
     * @param args {@link ListDiscordEventsCommandArgs} with `guildId`,`userId`
     * @return Mono<List < DiscordEvent>> events
     */
    Mono<List<DiscordEvent>> listDiscordEventCreatedByUser(ListDiscordEventsCommandArgs args);

    /**
     * Saves a {@link DiscordEvent} to a datastore in a reactive manner.
     *
     * @param discordEvent DiscordEvent built on info obtained from
     *                     a dispatched discord command
     * @return Mono<DiscordEvent> saved event
     */
    Mono<DiscordEvent> saveDiscordEvent(DiscordEvent discordEvent);
}
