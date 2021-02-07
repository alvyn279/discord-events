package com.alvyn279.discord.domain;

import discord4j.core.object.entity.Guild;

import discord4j.core.object.entity.User;
import discord4j.core.retriever.EntityRetrievalStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.alvyn279.discord.domain.Constants.NUMBER_TO_RAW_EMOJI_STRING;
import static com.alvyn279.discord.domain.Constants.RAW_EMOJI_STRING_TO_NUMBER;

/**
 * Utility functions adding Discord context
 */
public class GuildUtils {

    /**
     * Obtains the raw string unicode for a numbered emoji.
     *
     * @param number number
     * @return raw string
     */
    public static String getRawReactionEmojiFromNumber(Integer number) {
        if (number < 0 || number > 9 || !NUMBER_TO_RAW_EMOJI_STRING.containsKey(number)) {
            throw new RuntimeException("Discord does not have emojis outside the 0-9 inclusive range.");
        }
        return NUMBER_TO_RAW_EMOJI_STRING.get(number);
    }

    /**
     * Obtains the index represented by the numbered emoji
     * unicode passed as param.
     *
     * @param raw emoji unicode raw string
     * @return index it represents
     */
    public static Optional<Integer> getNumberedEmojiIndex(String raw) {
        if (!RAW_EMOJI_STRING_TO_NUMBER.containsKey(raw)) {
            return Optional.empty();
        }
        return Optional.of(RAW_EMOJI_STRING_TO_NUMBER.get(raw));
    }

    /**
     * Retrieves all the guild {@link User}s through the
     * Discord REST API (not the gateway).
     * <p>
     * Temporary: this REST API operation takes a significant amount of
     * time. We should probably cache the result.
     *
     * @param guild Discord guild from messageCreateEvent
     * @return Mono of id to User map
     */
    public static Mono<Map<String, User>> retrieveGuildUsers(Guild guild) {
        Map<String, User> userIdToUsername = new HashMap<>();

        return guild.getMembers(EntityRetrievalStrategy.REST)
            .flatMap(member -> {
                userIdToUsername.put(member.getId().asString(), member);
                return Flux.just(member);
            })
            .then(Mono.just(userIdToUsername));
    }
}
