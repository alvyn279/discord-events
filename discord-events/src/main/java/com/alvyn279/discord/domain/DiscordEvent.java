package com.alvyn279.discord.domain;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

/**
 * Class that represents a Discord Event that the users create while
 * interacting with the bot.
 */
@Builder
@Value
public class DiscordEvent {
    /**
     * The unique event ID for the event created with the bot
     */
    String eventId;

    /**
     * The user ID for Discord user that created the event
     */
    String createdBy;

    /**
     * The name/title of the event
     */
    String name;

    /**
     * The epoch timestamp at which the event will start
     */
    Instant timestamp;

    /**
     * (Optional) The description for the event
     */
    String description;
}
