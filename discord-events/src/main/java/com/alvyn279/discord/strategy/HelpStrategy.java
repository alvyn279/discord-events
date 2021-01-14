package com.alvyn279.discord.strategy;

import com.alvyn279.discord.domain.BotMessages;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

/**
 * Strategy to return static information to the user.
 * This information is to help the usr know how to use
 * the offered commands.
 */
public class HelpStrategy {

    // Default constructor
    public HelpStrategy() {}

    public Mono<Void> execute(MessageCreateEvent event) {
        return event.getMessage().getChannel()
            .flatMap(messageChannel -> messageChannel.createEmbed(BotMessages::help))
            .then();
    }
}
