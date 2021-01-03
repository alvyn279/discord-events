package com.alvyn279.discord.domain;

import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

/**
 * Interface containing the signature for operations to be performed
 * in response to incoming commands from the discord web socket.
 */
public interface CommandReaction {

    /**
     * Executes a reaction to a given command
     * @param event {@link MessageCreateEvent} Message that is sent from a discord channel
     * @return Publisher with Void type completion
     */
    Mono<Void> execute(MessageCreateEvent event);
}
