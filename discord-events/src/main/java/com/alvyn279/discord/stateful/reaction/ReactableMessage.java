package com.alvyn279.discord.stateful.reaction;

import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import reactor.core.publisher.Mono;

/**
 * Interface for messages that have behaviour when
 * reactions are added and removed.
 */
public interface ReactableMessage {
    /**
     * Behaviour on an added reaction.
     *
     * @param event {@link ReactionAddEvent} object passed to the
     *              event dispatcher
     */
    Mono<Void> onReactionAdd(ReactionAddEvent event);

    /**
     * Behaviour on a removed reaction.
     *
     * @param event {@link ReactionRemoveEvent} object passed to the
     *              event dispatcher
     */
    Mono<Void> onReactionRemove(ReactionRemoveEvent event);
}
