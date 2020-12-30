package com.alvyn279.discord.domain;

import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

public interface Command {

    Mono<Void> execute(MessageCreateEvent event);
}
