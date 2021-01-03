package com.alvyn279.discord.repository;

import com.alvyn279.discord.domain.DiscordEvent;

/**
 * Defines all the logical reads and write for the @link{DiscordEvent}
 * entity.
 */
public interface DiscordEventRepository {

    /**
     * Saves a {@link DiscordEvent} to a datastore
     * @param discordEvent DiscordEvent built on info obtained from
     *                     a dispatched discord command
     * @return DiscordEvent
     */
    DiscordEvent saveDiscordEvent(DiscordEvent discordEvent);
}
