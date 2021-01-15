package com.alvyn279.discord.exception;

import com.alvyn279.discord.domain.DiscordEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Exception thrown when a discord user tries to perform sensitive
 * operations on events that do not belong to them.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AccessDeniedException extends Exception {
    DiscordEvent discordEvent;
    public AccessDeniedException(String message, DiscordEvent discordEvent) {
        super(message);
        this.discordEvent = discordEvent;
    }
}
