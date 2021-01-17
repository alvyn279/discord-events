package com.alvyn279.discord.domain;

import discord4j.core.object.entity.Guild;

import discord4j.core.object.entity.User;
import discord4j.core.retriever.EntityRetrievalStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility functions adding Discord context
 */
public class GuildUtils {

    /**
     * Retrieves all the guild {@link User}s through the
     * Discord REST API (not the gateway).
     *
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
