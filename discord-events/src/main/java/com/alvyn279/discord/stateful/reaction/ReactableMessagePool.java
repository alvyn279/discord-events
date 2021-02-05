package com.alvyn279.discord.stateful.reaction;

import com.google.inject.Singleton;
import discord4j.core.object.entity.Guild;
import software.amazon.awssdk.utils.ImmutableMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Stateful pool that keeps (in-memory) bot messages which
 * are reactable and actionable.
 */
@Singleton
public class ReactableMessagePool {
    // { GuildId:str -> { MessageId:str -> ReactableMessage } }
    private final Map<String, Map<String, ReactableMessage>> reactableMessages;

    public ReactableMessagePool() {
        this.reactableMessages = new HashMap<>();
    }

    /**
     * Saves a {@link ReactableMessage} in memory given the
     * guild and the bot message id.
     *
     * @param guild            guild
     * @param messageId        bot message id
     * @param reactableMessage reactable/actionable message
     */
    public void putReactableMessage(Guild guild, String messageId, ReactableMessage reactableMessage) {
        reactableMessages.put(
            guild.getId().asString(),
            ImmutableMap.of(messageId, reactableMessage)
        );
    }

    /**
     * Retrieves a previously saved {@link ReactableMessage}.
     * It returns an optional.
     *
     * @param guild     guild
     * @param messageId bot message id (obtained by discord4j provided event listeners
     * @return optional of type ReactableMessage
     */
    public Optional<ReactableMessage> getReactableMessage(Guild guild, String messageId) {
        if (!reactableMessages.containsKey(guild.getId().asString()) ||
            !reactableMessages.get(guild.getId().asString()).containsKey(messageId)) {
            return Optional.empty();
        }
        return Optional.of(reactableMessages.get(guild.getId().asString()).get(messageId));
    }
}
