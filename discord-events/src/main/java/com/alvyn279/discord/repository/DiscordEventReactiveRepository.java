package com.alvyn279.discord.repository;

import com.alvyn279.discord.domain.DiscordEvent;
import com.alvyn279.discord.repository.dto.DeleteDiscordEventCommandDTO;
import com.alvyn279.discord.repository.dto.DiscordEventDTO;
import com.alvyn279.discord.repository.dto.ListDiscordEventsCommandDTO;
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
     * @param args {@link DeleteDiscordEventCommandDTO} with `guildId`, `userId`, `deleteCode`
     * @return Mono<DiscordEvent> deleted event
     */
    Mono<DiscordEvent> deleteDiscordEvent(DeleteDiscordEventCommandDTO args);

    /**
     * Fetches upcoming {@link DiscordEvent}s from a datastore in a
     * reactive manner. Looks for the next `upcomingLimit` events.
     *
     * @param args {@link ListDiscordEventsCommandDTO} with `upcomingLimit`,
     *             `guildId`, `currentDateTime`
     * @return Mono<List < DiscordEvent>> events
     */
    Mono<List<DiscordEvent>> listDiscordEventsByUpcomingWithLimit(ListDiscordEventsCommandDTO args);

    /**
     * Fetches upcoming {@link DiscordEvent}s from a datastore in a
     * reactive manner. Looks for the events within the next `upcomingTimeLimit`
     * timeframe.
     *
     * @param args {@link ListDiscordEventsCommandDTO} with `upcomingTimeLimit`,
     *             `guildId`, `currentDateTime`
     * @return Mono<List < DiscordEvent>> events
     */
    Mono<List<DiscordEvent>> listDiscordEventsByUpcomingWithTimeLimit(ListDiscordEventsCommandDTO args);

    /**
     * Fetches {@link DiscordEvent}s in a given date time range from a datastore in a
     * reactive manner. Looks for the events between two date-times.
     *
     * @param args {@link ListDiscordEventsCommandDTO} with `startDateTime`,`endDateTime`, `guildId`
     * @return Mono<List < DiscordEvent>> events
     */
    Mono<List<DiscordEvent>> listDiscordEventsByDateTimeRange(ListDiscordEventsCommandDTO args);

    /**
     * Fetches {@link DiscordEvent}s created by given user from a datastore in a
     * reactive manner. Looks for the events created by a user.
     *
     * @param args {@link ListDiscordEventsCommandDTO} with `guildId`,`userId`
     * @return Mono<List < DiscordEvent>> events
     */
    Mono<List<DiscordEvent>> listDiscordEventCreatedByUser(ListDiscordEventsCommandDTO args);

    /**
     * Saves a {@link DiscordEvent} to a datastore in a reactive manner.
     *
     * @param discordEventDTO DiscordEvent data transfer object that has nullable
     *                        `description` and `attendees` fields.
     * @return Mono<DiscordEvent> saved event
     */
    Mono<DiscordEvent> saveDiscordEvent(DiscordEventDTO discordEventDTO);
}
