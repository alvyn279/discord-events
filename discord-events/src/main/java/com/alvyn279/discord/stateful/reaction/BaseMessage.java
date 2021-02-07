package com.alvyn279.discord.stateful.reaction;

import discord4j.core.object.reaction.ReactionEmoji;
import lombok.experimental.SuperBuilder;

import java.util.Optional;

/**
 * Base class for messages that can be reacted to.
 */
@SuperBuilder
public class BaseMessage {
    /**
     * Utility function to retrieve the raw unicode string
     * from a discord {@link ReactionEmoji} with context.
     *
     * @param emoji ReactionEmoji
     * @return optional raw string
     */
    protected Optional<String> retrieveRawUnicode(ReactionEmoji emoji) {
        if (emoji.asUnicodeEmoji().isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(emoji
            .asUnicodeEmoji()
            .get()
            .getRaw());
    }
}
